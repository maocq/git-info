package domain.model

import play.api.libs.json.Json

trait TransformerDomain {

  implicit val groupFmt = Json.format[Group]
  implicit val projectFmt = Json.format[Project]
  implicit val commitFmt = Json.format[Commit]
  implicit val diffFmt = Json.format[Diff]
  implicit val issueFmt = Json.format[Issue]
  implicit val prFmt = Json.format[PR]
  implicit val commitsFmt = Json.format[Commits]
  implicit val newInfoFmt = Json.format[NewInfo]

}
