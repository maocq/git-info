package infrastructure

import domain.model.Project
import play.api.libs.json.Json

trait TransformerDTOs {

  implicit val projectGitLabDTOReads = Json.format[ProjectGitLabDTO]
  implicit val commitGitLabDTOReads = Json.format[CommitGitLabDTO]
  implicit val commitDiffGitLabDTOReads = Json.format[CommitDiffGitLabDTO]


  implicit val projectReads = Json.format[Project]

}
