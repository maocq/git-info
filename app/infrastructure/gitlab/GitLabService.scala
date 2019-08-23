package infrastructure.gitlab

import java.time.ZonedDateTime

import cats.implicits._
import domain.model.GError
import domain.model.GError.DomainError
import infrastructure._
import javax.inject.Inject
import monix.eval.Task
import play.api.mvc.ControllerComponents

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

class GitLabService@Inject()(http: ServiceHTTP, cc: ControllerComponents)
                            (implicit ec: ExecutionContext) extends TransformerDTOs {

  def getProject(projectId: Int): Task[Either[GError, ProjectGitLabDTO]] = Task.deferFuture {
    http.get[ProjectGitLabDTO](s"https://gitlab.seven4n.com/api/v4/projects/$projectId",
      Map("Private-Token" -> "mx7o6YbX7euiykysiGMg"))
  }.map(_.bimap(l => DomainError(s"Project not exist - ${l.error}", "11001"), _.response))
    .recover{case error => DomainError(error.getMessage, "11000").asLeft}


  def getAllCommits(id: Int, date: Option[ZonedDateTime]): Task[Either[GError, List[CommitGitLabDTO]]] = Task.deferFuture {
    getAllCommits(id, 1, Nil, date.map(_.plusSeconds(1)))
  }.map(_.bimap(l => DomainError(l.error, "11002"), _.response))
    .recover{case error => DomainError(error.getMessage, "11000").asLeft}


  def getCommitsDiff(projectId: Int, commit: String): Task[Either[GError, (String, List[CommitDiffGitLabDTO])]] = Task.deferFuture {
    http.get[List[CommitDiffGitLabDTO]](
      s"https://gitlab.seven4n.com/api/v4/projects/$projectId/repository/commits/$commit/diff?per_page=500",
      Map("Private-Token" -> "mx7o6YbX7euiykysiGMg"))
  }.map(_.bimap(l => DomainError(l.error, "11001"), right => (commit, right.response)))
    .recover{case error => DomainError(error.getMessage, "11000").asLeft}



  private def getAllCommits(id: Int, page: Int, lista: List[CommitGitLabDTO], date: Option[ZonedDateTime]): Future[Either[ErrorHTTP, ResponseHTTP[List[CommitGitLabDTO]]]] ={
    getCommits(id, page, date).flatMap {
      case l @ Left(_) => Future.successful(l)
      case Right(x) => getNextPage(x) match {
        case 0 => Future.successful(x.copy(response = (lista ::: x.response).reverse).asRight)
        case n => getAllCommits(id, n, lista ::: x.response, date)
      }
    }
  }

  //En construccion
  def getMergeRequest(projectId: Int, page: Int): Future[Either[ErrorHTTP, ResponseHTTP[List[MergeRequestDTO]]]] = {
    http.get[List[MergeRequestDTO]](s"https://gitlab.seven4n.com/api/v4/projects/$projectId/merge_requests?per_page=100",
      Map("Private-Token" -> "mx7o6YbX7euiykysiGMg"))
  }

  private def getCommits(projectId: Int, page: Int, date: Option[ZonedDateTime]): Future[Either[ErrorHTTP, ResponseHTTP[List[CommitGitLabDTO]]]] = {
    http.get[List[CommitGitLabDTO]](s"https://gitlab.seven4n.com/api/v4/projects/$projectId/repository/commits?page=$page&per_page=100&since=${date.getOrElse("")}",
      Map("Private-Token" -> "mx7o6YbX7euiykysiGMg"))
  }

  private def getNextPage(responseHTTP: ResponseHTTP[List[CommitGitLabDTO]]): Int = {
    Try(responseHTTP.completeResponse.header("X-Next-Page")
      .map(_.toInt).getOrElse(0)).getOrElse(0)
  }

}
