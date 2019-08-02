package infrastructure

import java.time.ZonedDateTime

case class ProjectGitLabDTO(
  id: Int, description: String, name: String, name_with_namespace: String, path: String, path_with_namespace: String,
  created_at: ZonedDateTime, default_branch: String, ssh_url_to_repo: String, http_url_to_repo: String, web_url: String
)

case class CommitGitLabDTO(
  id: String, short_id: String, created_at: ZonedDateTime, parent_ids: List[String], title: String, message: String, author_name: String,
  author_email: String, authored_date: ZonedDateTime, committer_name: String, committer_email: String, committed_date: ZonedDateTime
)

case class CommitDiffGitLabDTO(old_path: String, new_path: String, a_mode: String, b_mode: String, new_file: Boolean, renamed_file: Boolean, deleted_file: Boolean, diff: String)
