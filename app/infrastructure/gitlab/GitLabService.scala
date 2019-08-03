package infrastructure.gitlab

import cats.implicits._
import infrastructure._
import javax.inject.Inject
import play.api.mvc.ControllerComponents

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

class GitLabService@Inject()(http: ServiceHTTP, cc: ControllerComponents)
                            (implicit ec: ExecutionContext) extends TransformerDTOs {

  def getProject(projectId: Int): Future[Either[String, ProjectGitLabDTO]] = {
    http.get[ProjectGitLabDTO](s"https://gitlab.seven4n.com/api/v4/projects/$projectId",
      Map("Private-Token" -> "mx7o6YbX7euiykysiGMg"))
      .map(_.bimap(_.toString, _.response))
  }

  def getAllCommits(id: Int): Future[Either[String, List[CommitGitLabDTO]]] = getAllCommits(id, 1, Nil).map(_.bimap(_.toString, _.response))

  def getCommitsDiff(projectId: Int, commit: String): Future[Either[String, (String, List[CommitDiffGitLabDTO])]] = {
    http.get[List[CommitDiffGitLabDTO]](
      s"https://gitlab.seven4n.com/api/v4/projects/$projectId/repository/commits/$commit/diff",
      Map("Private-Token" -> "mx7o6YbX7euiykysiGMg"))
      .map(_.bimap(left => left.toString, right => (commit, right.response)))
  }

  private def getAllCommits(id: Int, page: Int, lista: List[CommitGitLabDTO]): Future[Either[ErrorHTTP, ResponseHTTP[List[CommitGitLabDTO]]]] ={
    getCommits(id, page).flatMap {
      case l @ Left(_) => Future.successful(l)
      case Right(x) => getNextPage(x) match {
        case 0 => Future.successful(x.copy(response = lista ::: x.response).asRight)
        case n => getAllCommits(id, n, lista ::: x.response)
      }
    }
  }

  private def getCommits(projectId: Int, page: Int): Future[Either[ErrorHTTP, ResponseHTTP[List[CommitGitLabDTO]]]] = {
    http.get[List[CommitGitLabDTO]](s"https://gitlab.seven4n.com/api/v4/projects/$projectId/repository/commits?page=$page&per_page=100&since=2010-07-15T00:00:00Z",
      Map("Private-Token" -> "mx7o6YbX7euiykysiGMg"))
  }

  private def getNextPage(responseHTTP: ResponseHTTP[List[CommitGitLabDTO]]): Int = {
    Try(responseHTTP.completeResponse.header("X-Next-Page")
      .map(_.toInt).getOrElse(0)).getOrElse(0)
  }

}
