package infrastructure.gitlab

import cats.implicits._
import infrastructure.{CommitDiffGitLabDTO, CommitGitLabDTO, ProjectGitLabDTO, ResponseHTTP, ServiceHTTP, TransformerDTOs}
import javax.inject.Inject
import play.api.mvc.ControllerComponents

import scala.concurrent.{ExecutionContext, Future}

class GitLabService@Inject()(http: ServiceHTTP, cc: ControllerComponents)
                            (implicit ec: ExecutionContext) extends TransformerDTOs {

  def getProject(id: Int): Future[Either[String, ResponseHTTP[ProjectGitLabDTO]]] = {
    http.get[ProjectGitLabDTO](s"https://gitlab.seven4n.com/api/v4/projects/$id",
      Map("Private-Token" -> "mx7o6YbX7euiykysiGMg")).map(_.leftMap(_.toString))
  }

  def getCommits(id: Int): Future[Either[String, ResponseHTTP[List[CommitGitLabDTO]]]] = {
    http.get[List[CommitGitLabDTO]](s"https://gitlab.seven4n.com/api/v4/projects/$id/repository/commits?per_page=100",
      Map("Private-Token" -> "mx7o6YbX7euiykysiGMg")).map(_.leftMap(_.toString))
  }

  def getCommitsDiff(id: Int, commit: String): Future[Either[String, ResponseHTTP[List[CommitDiffGitLabDTO]]]] = {
    http.get[List[CommitDiffGitLabDTO]](
      s"https://gitlab.seven4n.com/api/v4/projects/$id/repository/commits/$commit/diff",
      Map("Private-Token" -> "mx7o6YbX7euiykysiGMg"))
      .map(_.leftMap(_.toString))
  }

}
