package infrastructure

import controllers.IssuesForStatus
import domain.model.TransformerDomain
import persistence.project.{IssueState, UserIssuesClosed}
import persistence.querys.{CommitsForUser, DiffsUser, FilesWithCommits, InfoGroupDTO}
import play.api.libs.json.Json

trait TransformerDTOsHTTP extends TransformerDomain {

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

  implicit val infoGroupFmt = Json.format[InfoGroupDTO]
}
