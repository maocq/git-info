package infrastructure

import play.api.libs.json.Json

trait TransformerDTOsHTTP {

  implicit val projectIDDTOReads = Json.format[ProjectIDDTO]

}
