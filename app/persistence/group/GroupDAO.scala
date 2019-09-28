package persistence.group

import java.time.ZonedDateTime

import javax.inject.Inject
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.ExecutionContext

case class GroupRecord(
  id: Int, name: String, createdAt: ZonedDateTime
)

class GroupDAO @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext)
  extends HasDatabaseConfigProvider[JdbcProfile] {

  import GroupTable._
  import profile.api._

  def insert(groupRecord: GroupRecord) = db.run {
    (groupsdb returning groupsdb) += groupRecord
  }

}
