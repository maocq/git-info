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


  def insert(diffRecord: DiffRecord): Future[DiffRecord] = {
    val insertar = (diffsdb returning diffsdb) += diffRecord
    db.run(insertar)
  }

  def insertAll(diffsRecord: List[DiffRecord]): Future[List[DiffRecord]] = {
    val inserts = DBIO.sequence(diffsRecord.map(c => (diffsdb returning diffsdb) += c))
      .transactionally
    db.run(inserts)
  }



  implicit val JavaZonedDateTimeMapper = MappedColumnType.base[ZonedDateTime, Timestamp](
    l => Timestamp.from(l.toInstant),
    t => ZonedDateTime.ofInstant(t.toInstant, ZoneOffset.UTC)
  )

  private class DiffsRecord(tag: Tag)  extends Table[DiffRecord](tag, "diffs") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)

    def oldPath = column[String]("old_path")
    def newPath = column[String]("new_path")
    def aMode = column[String]("a_mode")
    def bMode = column[String]("b_mode")
    def newFile = column[Boolean]("new_file")
    def renamedFile = column[Boolean]("renamed_file")
    def deletedFile = column[Boolean]("deleted_file")
    def diff = column[String]("diff")
    def additions = column[Int]("additions")
    def deletions = column[Int]("deletions")
    def commitId = column[String]("commit_id")

    def * = (id, oldPath, newPath, aMode, bMode, newFile, renamedFile, deletedFile, diff, additions, deletions, commitId) <> (DiffRecord.tupled, DiffRecord.unapply)
  }

  private val diffsdb = TableQuery[DiffsRecord]

}
