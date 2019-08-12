package infrastructure

import persistence.querys.{CommitsForUser, DiffsUser}
import play.api.libs.json.Json

trait TransformerDTOsHTTP {

  implicit val projectIDDTOReads = Json.format[ProjectIDDTO]

  implicit val diffsUserFmt = Json.format[DiffsUser]
  implicit val commitsForUserFmt = Json.format[CommitsForUser]

}
