package infrastructure

import play.api.libs.json.Json

trait TransformerDTOs {

  implicit val projectGitLabDTOReads = Json.format[ProjectGitLabDTO]
  implicit val commitGitLabDTOReads = Json.format[CommitGitLabDTO]
  implicit val commitDiffGitLabDTOReads = Json.format[CommitDiffGitLabDTO]
  implicit val userGitLabDTOGitLabDTOReads = Json.format[UserGitLabDTO]
  implicit val mergeRequestDTOGitLabDTOReads = Json.format[MergeRequestDTO]

}
