package controllers

import domain.commands.RegisterProjectCommand
import infrastructure.TransformerDTOsHTTP
import javax.inject.Inject
import play.api.libs.json.JsValue
import play.api.mvc.{Action, ControllerComponents, Request}

import scala.concurrent.ExecutionContext

class ProjectController @Inject()(registerProject: RegisterProjectCommand, cc: ControllerComponents)(implicit ec: ExecutionContext)
  extends CommandsController(cc) with TransformerDTOsHTTP {

  def registerProject: Action[JsValue] = Action.async(parse.json) { implicit request: Request[JsValue]  =>
    ejecutar(registerProject, request.body)
  }
}
