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

  import IssueTable._
  import profile.api._

  def findByID(issuesRecord: IssueRecord): Future[Option[IssueRecord]] = db.run {
    issuesdb.filter(u => u.id === issuesRecord.id).result.headOption
  }

  def getLastDateIssues(projectId: Int): Future[Option[ZonedDateTime]] = db.run {
    issuesdb.filter(_.projectId === projectId).sortBy(_.updatedAt.desc).map(_.updatedAt).take(1).result.headOption
  }

  def insertOrUpdateAll(issuesRecord: List[IssueRecord]): Future[List[IssueRecord]] = db.run {
    (for {
      s <- insertOrUpdateSeq(issuesRecord)
    } yield issuesRecord).transactionally
  }

  private def insertOrUpdateSeq(issuesRecord: List[IssueRecord]): DBIO[List[Option[IssueRecord]]] = {
    DBIO.sequence(issuesRecord.map(i => (issuesdb returning issuesdb).insertOrUpdate(i) ))
  }
}
