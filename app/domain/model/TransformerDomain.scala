package domain.model

import play.api.libs.json.Json

trait TransformerDomain {

  implicit val groupReads = Json.format[Group]
  implicit val projectReads = Json.format[Project]
  implicit val commitReads = Json.format[Commit]
  implicit val diffReads = Json.format[Diff]
  implicit val issueReads = Json.format[Issue]
  implicit val prReads = Json.format[PR]

}
