package controllers

import domain.model.GError
import domain.model.GError.ValidationError
import play.api.libs.json.{JsObject, JsString, JsValue, Json, Writes}

trait ErrorsTransformer {

  implicit val errorAplicacionWts: Writes[GError] = Writes {
    case validationError: ValidationError => writesValidationError(validationError)
    case error => writesGError(error)
  }

  def writesValidationError(error: ValidationError): JsValue = {
    JsObject(Seq(
      "message" -> JsString(error.message),
      "errrorCode" -> JsString(error.errrorCode),
      "filds" -> Json.toJson(error.filds)
    ))
  }

  def writesGError(error: GError): JsValue = {
    JsObject(Seq(
      "message" -> JsString(error.message),
      "errrorCode" -> JsString(error.errrorCode)
    ))
  }

}
