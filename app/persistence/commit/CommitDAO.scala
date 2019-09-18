package persistence.commit

import java.time.ZonedDateTime

import javax.inject.Inject
import persistence.diff.DiffDAO
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.Future

case class CommitRecord(
  id: String, shortId: String, createdAt: ZonedDateTime, parentIds: String, title: String, message: String, authorName: String,
  authorEmail: String, authoredDate: ZonedDateTime, committerName: String, committerEmail: String, committedDate: ZonedDateTime, projectId: Int
)

class CommitDAO @Inject() (protected val dbConfigProvider: DatabaseConfigProvider, diffDAO: DiffDAO)
  extends HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._
  import CommitTable._

  def insert(commitRecord: CommitRecord): Future[CommitRecord] = db.run {
    (commitsdb returning commitsdb) += commitRecord
  }

  def insertAllDBIO(commitsRecord: List[CommitRecord]): DBIO[List[CommitRecord]] = {
    DBIO.sequence(commitsRecord.map(c => (commitsdb returning commitsdb) += c))
  }

  def insertAll(commitsRecord: List[CommitRecord]): Future[List[CommitRecord]] = db.run {
    insertAllDBIO(commitsRecord).transactionally
  }


  def getExistingId(ids: List[String]): Future[Seq[CommitRecord]] = db.run {
    commitsdb.filter(as => as.id.inSet( ids)).result
  }

  def getLastDateCommit(projectId: Int): Future[Option[ZonedDateTime]] = db.run {
    commitsdb.filter(_.projectId === projectId).sortBy(_.committedDate.desc).map(_.committedDate).take(1).result.headOption
  }
}
