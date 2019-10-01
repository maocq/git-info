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

case class UserGitLabDTO(id: Int, name: String, username: String, state: String, avatar_url: String, web_url: String)

case class IssueGitLabDTO(
  id : Int, iid : Int, project_id : Int, title : String, description : Option[String], state : String, created_at : ZonedDateTime, updated_at : ZonedDateTime,
  closed_at : Option[ZonedDateTime], closed_by : Option[UserGitLabDTO], author : UserGitLabDTO, assignee : Option[UserGitLabDTO], web_url : String
)

case class PRGitLabDTO(
  id: Int, iid: Int, project_id: Int, title: String, description: String, state: String, created_at: ZonedDateTime, updated_at: ZonedDateTime,
  merged_by: Option[UserGitLabDTO], merged_at: Option[ZonedDateTime], closed_by: Option[UserGitLabDTO], closed_at: Option[ZonedDateTime],
  target_branch: String, source_branch: String, user_notes_count: Int, upvotes: Int, downvotes: Int, author: UserGitLabDTO
)

/*
assignee: Option[UserGitLabDTO],
,
source_project_id: Int,
target_project_id: Int
*/
