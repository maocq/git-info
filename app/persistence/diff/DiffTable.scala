package persistence.diff

import implicits.SlickImplicits

object DiffTable extends SlickImplicits {

  import slick.jdbc.PostgresProfile.api._

  val diffsdb = TableQuery[DiffsRecord]

  class DiffsRecord(tag: Tag)  extends Table[DiffRecord](tag, "diffs") {
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

    //def commit = foreignKey("commit_fk", commitId, commitsdb)(_.id)
    def * = (id, oldPath, newPath, aMode, bMode, newFile, renamedFile, deletedFile, diff, additions, deletions, commitId) <> (DiffRecord.tupled, DiffRecord.unapply)
  }
}
