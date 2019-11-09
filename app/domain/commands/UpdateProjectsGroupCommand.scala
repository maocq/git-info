package domain.commands

import cats.implicits._
import controllers.ErrorsTransformer
import domain.model.GError
import domain.services.ProjectService
import implicits.implicits._
import infrastructure._
import javax.inject.Inject
import monix.eval.Task
import monix.execution.Scheduler
import play.api.Logger
import play.api.libs.json.{JsValue, Json}

class UpdateProjectsGroupCommand @Inject()(projectService: ProjectService) extends Command[GroupIDDTO] with TransformerDTOsHTTP with ErrorsTransformer {
  val logger: Logger = Logger(this.getClass)

  implicit lazy val executor: Scheduler = monix.execution.Scheduler.Implicits.global

  def execute(dto: GroupIDDTO): Task[Consequence] = {
    (for {
      g <- projectService.getGroup(dto.id).toEitherT
      x <- updateInfoGroup(g.id).toEitherT
    } yield x)
      .fold(leftConsequence, r => rightConsequence(Json.toJson(r)))
  }

  private def updateInfoGroup(groupID: Int): Task[Either[GError, MessageDTO]] = {
    Task.eval {
      projectService.getProjectsByGroup(groupID)
        .flatMap(projects => Task.traverse(projects){project => updateInfoProject(project.id)})
        .foreach(i => logger.info(s"Response: $i"))

      MessageDTO("Updating info").asRight[GError]
    }
  }


  private def updateInfoProject(projectID: Int): Task[JsValue] = {
    projectService.updateInfoProject(projectID)
      .fold(Json.toJson(_), Json.toJson(_))
      .recover{case error => projectService.finishUpdating(projectID).runToFuture; Json.toJson(error.getMessage)}
  }

}
