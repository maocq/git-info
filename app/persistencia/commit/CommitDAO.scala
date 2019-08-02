package persistencia.commit

import java.sql.Timestamp
import java.time.{ZoneOffset, ZonedDateTime}

import javax.inject.Inject
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.Future

case class CommitRecord(
  id: String, shortId: String, createdAt: ZonedDateTime, parentIds: String, title: String, message: String, authorName: String,
  authorEmail: String, authoredDate: ZonedDateTime, committerName: String, committerEmail: String, committedDate: ZonedDateTime, projectId: Int
)

class CommitDAO @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)
  extends HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._


  def insertar(commitRecord: CommitRecord): Future[CommitRecord] = {
    val insertar = (commitsdb returning commitsdb) += commitRecord
    db.run(insertar)
  }




  implicit val JavaZonedDateTimeMapper = MappedColumnType.base[ZonedDateTime, Timestamp](
    l => Timestamp.from(l.toInstant),
    t => ZonedDateTime.ofInstant(t.toInstant, ZoneOffset.UTC)
  )

  private class CommitsRecord(tag: Tag)  extends Table[CommitRecord](tag, "commits") {
    def id = column[String]("id", O.PrimaryKey)

    def shortId = column[String]("short_id")
    def createdAt = column[ZonedDateTime]("created_at")
    def parentIds = column[String]("parent_ids")
    def title = column[String]("title")
    def message = column[String]("message")
    def authorName = column[String]("author_name")
    def authorEmail = column[String]("author_email")
    def authoredDate = column[ZonedDateTime]("authored_date")
    def committerName = column[String]("committer_name")
    def committerEmail = column[String]("committer_email")
    def committedDate = column[ZonedDateTime]("committed_date")
    def projectId = column[Int]("project_id")

    def email = column[String]("email")

    def * = (id, shortId, createdAt, parentIds, title, message, authorName, authorEmail, authoredDate, committerName, committerEmail, committedDate, projectId) <> (CommitRecord.tupled, CommitRecord.unapply)
  }

  private val commitsdb = TableQuery[CommitsRecord]

}