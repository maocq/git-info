package persistence.pr

import java.time.ZonedDateTime

import javax.inject.Inject
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.ExecutionContext

case class PRRecord(
  id: Int, iid: Int, projectId: Int, title: String, description: Option[String], state: String, createdAt: ZonedDateTime, updatedAt: ZonedDateTime,
  mergedBy: Option[Int], mergedAt: Option[ZonedDateTime], closedBy: Option[Int], closedAt: Option[ZonedDateTime],
  targetBranch: String, sourceBranch: String, userNotesCount: Int, upvotes: Int, downvotes: Int, author: Int
)

class PTDAO  @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext)
  extends HasDatabaseConfigProvider[JdbcProfile] {

}
