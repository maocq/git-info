package persistence.issue

import java.time.ZonedDateTime

import javax.inject.Inject
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

case class IssueRecord(
  id : Int, iid : Int, projectId : Int, title : String, description : Option[String], state : String, createdAt : ZonedDateTime, updatedAt : ZonedDateTime,
  closedAt : Option[ZonedDateTime], closedBy : Option[Int], author : Int, assignee : Option[Int], webUrl : String
)

class IssueDAO @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext)
  extends HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._
  import IssueTable._

  def getLastDateIssues(projectId: Int): Future[Option[ZonedDateTime]] = db.run {
    issuesdb.filter(_.projectId === projectId).sortBy(_.updatedAt.desc).map(_.updatedAt).take(1).result.headOption
  }

}
