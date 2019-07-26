package infraestructura

import java.time.ZonedDateTime

case class CommitGitLabDTO(
  id: String, short_id: String, created_at: ZonedDateTime, parent_ids: List[String], title: String, message: String, author_name: String,
  author_email: String, authored_date: ZonedDateTime, committer_name: String, committer_email: String, committed_date: ZonedDateTime
)