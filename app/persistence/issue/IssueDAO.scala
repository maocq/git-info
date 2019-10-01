package persistence.issue

import java.time.ZonedDateTime

import javax.inject.Inject
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.ExecutionContext

case class IssueRecord(
  id : Int, iid : Int, projectId : Int, title : String, description : Option[String], state : String, createdAt : ZonedDateTime, updatedAt : ZonedDateTime,
  closedAt : Option[ZonedDateTime], closedBy : Option[Int], author : Int, assignee : Option[Int], webUrl : String
)

class IssueDAO @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext)
  extends HasDatabaseConfigProvider[JdbcProfile] {


}
