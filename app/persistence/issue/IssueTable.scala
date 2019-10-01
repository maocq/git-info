package persistence.issue

import java.time.ZonedDateTime

import implicits.SlickImplicits


object IssueTable extends SlickImplicits {

  import slick.jdbc.PostgresProfile.api._

  val issuesdb = TableQuery[IssuesRecord]

  class IssuesRecord(tag: Tag)  extends Table[IssueRecord](tag, "issues") {
    def id = column[Int]("id", O.PrimaryKey)

    def iid = column[Int]("iid")
    def projectId = column[Int]("project_id")
    def title = column[String]("title")
    def description = column[Option[String]]("description")
    def state = column[String]("state")
    def createdAt = column[ZonedDateTime]("created_at")
    def updatedAt = column[ZonedDateTime]("updated_at")
    def closedAt = column[Option[ZonedDateTime]]("closed_at")
    def closedBy = column[Option[Int]]("closed_by")
    def author = column[Int]("author")
    def assignee = column[Option[Int]]("assignee")
    def webUrl = column[String]("web_url")

    def * = (id, iid, projectId, title, description, state, createdAt, updatedAt, closedAt, closedBy, author, assignee, webUrl) <> (IssueRecord.tupled, IssueRecord.unapply)
  }

}
