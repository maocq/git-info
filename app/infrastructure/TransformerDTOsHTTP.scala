package infrastructure

import controllers.IssuesForStatus
import domain.model.TransformerDomain
import persistence.group.GroupRecord
import persistence.project.{IssueState, UserIssuesClosed}
import persistence.querys.{CategoryValueDTO, CommitsForUser, DiffsUser, FilesWithCommits, ImpactGroupDTO, InfoGroupDTO, InfoIssuesDTO, LinesGroupDTO, NumberFileDTO, NumbersGroupDTO}
import play.api.libs.json.Json

trait TransformerDTOsHTTP extends TransformerDomain {

  //Verificar
  implicit val projectIDDTOReads = Json.format[ProjectIDDTO]
  implicit val groupDTOReads = Json.format[GroupDTO]
  implicit val messageDTOFmt = Json.format[MessageDTO]

  implicit val infoUserDTOFmt = Json.format[InfoUserDTO]
  implicit val diffsUserFmt = Json.format[DiffsUser]
  implicit val commitsForUserFmt = Json.format[CommitsForUser]
  implicit val filesWithCommitsFmt = Json.format[FilesWithCommits]

  implicit val issueStateFmt = Json.format[IssueState]
  implicit val issuesForStatusFmt = Json.format[IssuesForStatus]
  implicit val UserIssuesClosedFmt = Json.format[UserIssuesClosed]
  //End Verificar

  implicit val linesGroupFmt = Json.format[LinesGroupDTO]
  implicit val numberFileFmt = Json.format[NumberFileDTO]
  implicit val numbersinfoGroupFmt = Json.format[NumbersGroupDTO]
  implicit val infoGroupFmt = Json.format[InfoGroupDTO]
  implicit val impactGroupFmt = Json.format[ImpactGroupDTO]

  implicit val groupRecordFmt = Json.format[GroupRecord]
  implicit val categoryValuemt = Json.format[CategoryValueDTO]
  implicit val infoIssuesFmt = Json.format[InfoIssuesDTO]
}
