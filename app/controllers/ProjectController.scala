package controllers

import domain.commands.{RegisterProjectCommand, UpdateProjectCommand}
import infrastructure.{InfoUserDTO, TransformerDTOsHTTP}
import javax.inject.Inject
import persistence.querys.ProjectQueryDAO
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Action, AnyContent, ControllerComponents, Request}

import scala.concurrent.ExecutionContext

class ProjectController @Inject()(
  registerProject: RegisterProjectCommand,
  updateProjectCommand: UpdateProjectCommand,
  projectQueryDAO: ProjectQueryDAO,
  cc: ControllerComponents
)(implicit ec: ExecutionContext)
  extends CommandsController(cc) with TransformerDTOsHTTP {

  def registerProject: Action[JsValue] = Action.async(parse.json) { implicit request: Request[JsValue]  =>
    ejecutar(registerProject, request.body)
  }

  def updateProject: Action[JsValue] = Action.async(parse.json) { implicit request: Request[JsValue]  =>
    ejecutar(updateProjectCommand, request.body)
  }

  def infoUsers() = Action.async { implicit request: Request[AnyContent] =>
    projectQueryDAO.commitUser().map { commits =>
      commits.groupBy(commit => commit.email).map(tuple => {
        val values = tuple._2.foldLeft((0,0,0)){ case (acc, nxt) =>  (acc._1 + 1, acc._2 + nxt.additions, acc._3 + nxt.deletions)}
        InfoUserDTO(tuple._1, values._1, values._2, values._3)
      } ).toList
    }.map(list => Ok(Json.toJson(list.sortBy(_.commits)(Ordering.Int.reverse))))
  }
}
