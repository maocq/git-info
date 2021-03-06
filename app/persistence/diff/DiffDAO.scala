package persistence.diff

import java.sql.Date
import java.time.ZonedDateTime

import javax.inject.Inject
import persistence.commit.CommitTable.commitsdb
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.Future

case class DiffRecord(
  id: Int, oldPath: String, newPath: String, aMode: String, bMode: String, newFile: Boolean,
  renamedFile: Boolean, deletedFile: Boolean, diff: String, additions: Int, deletions: Int, commitId: String
)

class DiffDAO @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)
  extends HasDatabaseConfigProvider[JdbcProfile] {

  import DiffTable._
  import profile.api._

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

  case class DateValue(date: Date, value: Int)

  def test1() = db.run {
    val z = ZonedDateTime.now().minusYears(1)

    (for {
      d <- diffsdb
      c <- commitsdb if d.commitId === c.id
    } yield (c.committedDate, d.additions + d.deletions)).groupBy{ case (d, a) => dateDB(d)}
      .map{ case (d, group) => (d, group.map(_._2).sum.getOrElse(0))}
      .filter(f => f._1 >= Date.valueOf(z.toLocalDate))
      .sortBy(_._1.desc)
      .map(_.mapTo[DateValue])
      .result
  }

}
