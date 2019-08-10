package infrastructure

import play.api.libs.json.Json

trait TransformerDTOsHTTP {

  implicit val testReads = Json.format[Test]

}
