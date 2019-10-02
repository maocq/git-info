package persistence.pr

import java.time.ZonedDateTime

import javax.inject.Inject
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

case class PRRecord(
  id: Int, iid: Int, projectId: Int, title: String, description: Option[String], state: String, createdAt: ZonedDateTime, updatedAt: ZonedDateTime,
  mergedBy: Option[Int], mergedAt: Option[ZonedDateTime], closedBy: Option[Int], closedAt: Option[ZonedDateTime],
  targetBranch: String, sourceBranch: String, userNotesCount: Int, upvotes: Int, downvotes: Int, author: Int
)

class PRDAO  @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext)
  extends HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._
  import PRTable._

  def getLastDatePRs(projectId: Int): Future[Option[ZonedDateTime]] = db.run {
    prsdb.filter(_.projectId === projectId).sortBy(_.updatedAt.desc).map(_.updatedAt).take(1).result.headOption
  }

  def insertOrUpdateAll(prsRecord: List[PRRecord]): Future[List[PRRecord]] = db.run {
    (for {
      p <- insertOrUpdateSeq(prsRecord)
    } yield prsRecord).transactionally
  }

  private def insertOrUpdateSeq(prsRecord: List[PRRecord]): DBIO[List[Option[PRRecord]]] = {
    DBIO.sequence(prsRecord.map(p => (prsdb returning prsdb).insertOrUpdate(p) ))
  }
}
