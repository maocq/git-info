package persistence.diff

import java.sql.Timestamp
import java.time.{ZoneOffset, ZonedDateTime}

import javax.inject.Inject
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.Future

case class DiffRecord(
  id: Int, oldPath: String, newPath: String, aMode: String, bMode: String, newFile: Boolean,
  renamedFile: Boolean, deletedFile: Boolean, diff: String, additions: Int, deletions: Int, commitId: String
)

class DiffDAO @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)
  extends HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._
  import DiffTable._


  def insert(diffRecord: DiffRecord): Future[DiffRecord] = db.run {
    (diffsdb returning diffsdb) += diffRecord
  }

  def insertAllDBIO(diffsRecord: List[DiffRecord]): DBIO[List[DiffRecord]] =  {
    DBIO.sequence(diffsRecord.map(c => (diffsdb returning diffsdb) += c))
  }

  def insertAll(diffsRecord: List[DiffRecord]): Future[List[DiffRecord]] = db.run {
    insertAllDBIO(diffsRecord)
      .transactionally
  }
}
