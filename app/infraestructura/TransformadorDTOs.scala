package infraestructura

import play.api.libs.json.Json

trait TransformadorDTOs {

  implicit val commitGitLabDTOReads = Json.format[CommitGitLabDTO]

}
