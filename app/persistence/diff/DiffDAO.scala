package persistence.diff

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

  def test1() = db.run {
    (for {
      d <- diffsdb
      c <- commitsdb
    } yield (c.committedDate, d.additions + d.deletions))
        .groupBy{ case (d, a) => dateDB(d)}
        .map{ case (d, group) => (d, group.countDistinct)}
        .sortBy(_._1.desc)
        .result

    //commitsdb.groupBy(c => dateDB(c.committedDate)).map{ case (id, group) => (id, group.map(_.title).countDistinct)}.result

  }

}
