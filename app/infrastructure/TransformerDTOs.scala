package infrastructure

import play.api.libs.json.Json

trait TransformerDTOs {

  implicit val commitGitLabDTOReads = Json.format[CommitGitLabDTO]
  implicit val commitDiffGitLabDTOReads = Json.format[CommitDiffGitLabDTO]

}
