package controllers

import domain.commands.Command
import domain.model.GError
import domain.model.GError.{DomainError, TechnicalError, ValidationError}
import play.api.Logger
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{AbstractController, ControllerComponents, Result}

import scala.concurrent.{ExecutionContext, Future}

class CommandsController(cc: ControllerComponents)(implicit ec: ExecutionContext)
  extends AbstractController(cc) with ErrorsTransformer {

  val logger: Logger = Logger(this.getClass)

  def ejecutar(command: Command, jsValue: JsValue): Future[Result] = {
    logger.info(s"Command ${command.getClass.getName} Json: $jsValue")

    command.execute(jsValue).map(consequence =>
      consequence.response.fold(handleError, handleResponse)
    ) recover { case error =>
      logger.error(s"Internal server error ${error.getMessage}", error)
      InternalServerError(Json.toJson(TechnicalError("Internal server error", "30000")))
    }

  }

  private def handleResponse(json: JsValue): Result = {
    logger.info(s"Response: $json")
    Ok(json)
  }

  private def handleError(error: GError): Result = error match {
      case domainError: DomainError =>
        logger.error(s"Not acceptable ${Json.toJson(domainError)}", domainError.error.orNull)
        NotAcceptable(Json.toJson(domainError))
      case validationError: ValidationError =>
        logger.error(s"BadRequest ${Json.toJson(validationError)}", validationError.error.orNull)
        BadRequest(Json.toJson(validationError))
      case error =>
        logger.error(s"Internal server error ${Json.toJson(error)}", error.error.orNull)
        InternalServerError(Json.toJson(error))
    }
}
