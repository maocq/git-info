package infrastructure.gitlab

import cats.implicits._
import infrastructure._
import javax.inject.Inject
import play.api.mvc.ControllerComponents

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

class GitLabService@Inject()(http: ServiceHTTP, cc: ControllerComponents)
                            (implicit ec: ExecutionContext) extends TransformerDTOs {

  def getProject(id: Int): Future[Either[String, ResponseHTTP[ProjectGitLabDTO]]] = {
    http.get[ProjectGitLabDTO](s"https://gitlab.seven4n.com/api/v4/projects/$id",
      Map("Private-Token" -> "mx7o6YbX7euiykysiGMg")).map(_.leftMap(_.toString))
  }

  def getCommits(id: Int, page: Int): Future[Either[String, ResponseHTTP[List[CommitGitLabDTO]]]] = {
    http.get[List[CommitGitLabDTO]](s"https://gitlab.seven4n.com/api/v4/projects/$id/repository/commits?page=$page&per_page=100&since=2010-07-15T00:00:00Z",
      Map("Private-Token" -> "mx7o6YbX7euiykysiGMg")).map(_.leftMap(_.toString))
  }

  def getAllCommits(id: Int): Future[Either[String, ResponseHTTP[List[CommitGitLabDTO]]]] = getAllCommits(id, 1, List.empty)

  def getCommitsDiff(id: Int, commit: String): Future[Either[String, ResponseHTTP[List[CommitDiffGitLabDTO]]]] = {
    http.get[List[CommitDiffGitLabDTO]](
      s"https://gitlab.seven4n.com/api/v4/projects/$id/repository/commits/$commit/diff",
      Map("Private-Token" -> "mx7o6YbX7euiykysiGMg"))
      .map(_.leftMap(_.toString))
  }

  private def getAllCommits(id: Int, page: Int, lista: List[CommitGitLabDTO]): Future[Either[String, ResponseHTTP[List[CommitGitLabDTO]]]] ={
    getCommits(id, page).flatMap {
      case l @ Left(_) => Future.successful(l)
      case Right(x) => getNextPage(x) match {
        case 0 => Future.successful(x.copy(response = lista ::: x.response).asRight)
        case n => getAllCommits(id, n, lista ::: x.response)
      }
    }
  }

  private def getNextPage(responseHTTP: ResponseHTTP[List[CommitGitLabDTO]]): Int = {
    Try(responseHTTP.completeResponse.header("X-Next-Page")
      .map(_.toInt).getOrElse(0)).getOrElse(0)
  }

  /*
  def getAllCommits(id: Int, page: Int, lista: List[CommitGitLabDTO]): EitherT[Future, String, ResponseHTTP[List[CommitGitLabDTO]]] ={
    EitherT(getCommits(id, page)).flatMapF{ x =>
      getNextPage(x) match {
        case 0 => Future.successful(x.copy(response = lista ::: x.response).asRight)
        case n => other(id, n, lista ::: x.response)
      }
    }
  }
  */

}
