package domain.commands

import cats.implicits._
import controllers.ErrorsTransformer
import domain.model.TransformerDomain
import domain.services.ProjectService
import implicits.implicits._
import infrastructure._
import javax.inject.Inject
import monix.eval.Task
import monix.execution.Scheduler
import play.api.Logger
import play.api.libs.json.{JsValue, Json}

class UpdateProjectsGroupCommand @Inject()(projectService: ProjectService) extends Command[GroupIDDTO] with TransformerDomain with ErrorsTransformer {
  val logger: Logger = Logger(this.getClass)

  implicit lazy val executor: Scheduler = monix.execution.Scheduler.Implicits.global

  def execute(dto: GroupIDDTO): Task[Consequence] = {
    projectService.deleteGroup(dto.id).toEitherT
      .fold(leftConsequence, r => rightConsequence(Json.toJson(r)))
  }

  private def updateInfoProject(projectID: Int): Task[JsValue] = {
    projectService.updateInfoProject(projectID)
      .fold(Json.toJson(_), Json.toJson(_))
      .recover{case error => projectService.finishUpdating(projectID).runToFuture; Json.toJson(error.getMessage)}
  }

}
