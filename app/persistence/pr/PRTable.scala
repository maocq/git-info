package persistence.pr

import java.time.ZonedDateTime

import implicits.SlickImplicits

object PRTable extends SlickImplicits {

  import slick.jdbc.PostgresProfile.api._

  class PRsRecord(tag: Tag)  extends Table[PRRecord](tag, "pull_requests") {
    def id = column[Int]("id", O.PrimaryKey)

    def iid = column[Int]("iid")
    def projectId = column[Int]("project_id")
    def title = column[String]("title")
    def description = column[Option[String]]("description")
    def state = column[String]("state")
    def createdAt = column[ZonedDateTime]("created_at")
    def updatedAt = column[ZonedDateTime]("updated_at")
    def mergedBy = column[Option[Int]]("merged_by")
    def mergedAt = column[Option[ZonedDateTime]]("merged_at")
    def closedBy = column[Option[Int]]("closed_by")
    def closedAt = column[Option[ZonedDateTime]]("closed_at")
    def targetBranch = column[String]("target_branch")
    def sourceBranch = column[String]("source_branch")
    def userNotesCount = column[Int]("user_notes_count")
    def upvotes = column[Int]("upvotes")
    def downvotes = column[Int]("downvotes")
    def author = column[Int]("author")

    def * = (id, iid, projectId, title, description, state, createdAt, updatedAt, mergedBy, mergedAt, closedBy, closedAt, targetBranch, sourceBranch, userNotesCount, upvotes, downvotes, author) <> (PRRecord.tupled, PRRecord.unapply)
  }

}
