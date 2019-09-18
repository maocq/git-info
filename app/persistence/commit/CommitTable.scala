package persistence.commit

import java.sql.Timestamp
import java.time.{ZoneOffset, ZonedDateTime}

object CommitTable {

  import slick.jdbc.PostgresProfile.api._

  val commitsdb = TableQuery[CommitsRecord]

  implicit val JavaZonedDateTimeMapper = MappedColumnType.base[ZonedDateTime, Timestamp](
    l => Timestamp.from(l.toInstant),
    t => ZonedDateTime.ofInstant(t.toInstant, ZoneOffset.UTC)
  )

  class CommitsRecord(tag: Tag)  extends Table[CommitRecord](tag, "commits") {
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

    //def project = foreignKey("project_fk", projectId, projectDAO.gett)(_.id)
    def * = (id, shortId, createdAt, parentIds, title, message, authorName, authorEmail, authoredDate, committerName, committerEmail, committedDate, projectId) <> (CommitRecord.tupled, CommitRecord.unapply)
  }
}
