package infrastructure

import persistence.querys.{CommitsForUser, DiffsUser, FilesWithCommits}
import play.api.libs.json.Json

trait TransformerDTOsHTTP {

  implicit val projectIDDTOReads = Json.format[ProjectIDDTO]
  implicit val messageDTOFmt = Json.format[MessageDTO]

  implicit val infoUserDTOFmt = Json.format[InfoUserDTO]
  implicit val diffsUserFmt = Json.format[DiffsUser]
  implicit val commitsForUserFmt = Json.format[CommitsForUser]
  implicit val filesWithCommitsFmt = Json.format[FilesWithCommits]
}
