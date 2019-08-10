package domain.model

import play.api.libs.json.Json

trait TransformerDomain {

  implicit val projectReads = Json.format[Project]

}
