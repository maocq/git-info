package persistence.group

import java.time.ZonedDateTime

import javax.inject.Inject
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

case class GroupRecord(
  id: Int, name: String, createdAt: ZonedDateTime
)

class GroupDAO @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext)
  extends HasDatabaseConfigProvider[JdbcProfile] {

  import GroupTable._
  import profile.api._

  def insert(groupRecord: GroupRecord): Future[GroupRecord] = db.run {
    (groupsdb returning groupsdb) += groupRecord
  }

  def findByID(id: Int): Future[Option[GroupRecord]] = db.run {
    groupsdb.filter(_.id === id).result.headOption
  }
}
