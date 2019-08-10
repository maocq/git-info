package controllers

import cats.implicits._
import domain.commands.Command
import domain.model.GError
import domain.model.GError.{DomainError, TechnicalError, ValidationError}
import monix.eval.Task
import monix.execution.Scheduler.Implicits.global
import play.api.Logger
import play.api.libs.json.{JsValue, Json, Reads}
import play.api.mvc.{AbstractController, ControllerComponents, Result}

import scala.concurrent.Future

class CommandsController(cc: ControllerComponents)
  extends AbstractController(cc) with ErrorsTransformer {

  val logger: Logger = Logger(this.getClass)

  def ejecutar[T](command: Command[T], jsValue: JsValue)(implicit m: Reads[T]): Future[Result] = {
    logger.info(s"Command ${command.getClass.getName} Json: $jsValue")

    validateJson(jsValue).fold(
      error => Task.now(BadRequest(Json.toJson(ValidationError("Json error", "20000", error)))),
      dto => execute(command, dto)
    ).recover { case error =>
      logger.error(s"Internal server error ${error.getMessage}", error)
      InternalServerError(Json.toJson(TechnicalError("Internal server error", "30000")))
    }.runToFuture
  }

  private def validateJson[T](json: JsValue)(implicit m: Reads[T]): Either[List[String], T] = {
    json.validate[T].asEither.leftMap(_.map(_._1.path.mkString(",")).toList)
  }

  private def execute[T](command: Command[T], value: T): Task[Result] = {
    command.execute(value).map(consequence => consequence.response.fold(handleError, handleResponse))
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
