package controllers

import domain.commands.TestCommand
import javax.inject.Inject
import play.api.libs.json.JsValue
import play.api.mvc.{Action, ControllerComponents, Request}

import scala.concurrent.ExecutionContext

class ProjectController @Inject()(test: TestCommand, cc: ControllerComponents)(implicit ec: ExecutionContext)
  extends CommandsController(cc) {

  def listarComando: Action[JsValue] = Action.async(parse.json) { implicit request: Request[JsValue]  =>
    ejecutar(test, request.body)
  }
}
