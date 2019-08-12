package domain.model

import play.api.libs.json.Json

trait TransformerDomain {

  implicit val projectReads = Json.format[Project]
  implicit val commitReads = Json.format[Commit]
  implicit val diffReads = Json.format[Diff]

}