package domain.commands

import cats.implicits._
import controllers.ErrorsTransformer
import domain.model.{GError, TransformerDomain}
import domain.services.ProjectService
import implicits.implicits._
import infrastructure.{MessageDTO, ProjectIDDTO, TransformerDTOsHTTP}
import javax.inject.Inject
import monix.eval.Task
import monix.execution.Scheduler
import play.api.Logger
import play.api.libs.json.Json

class UpdateProjectCommand @Inject()(projectService: ProjectService) extends Command[ProjectIDDTO] with TransformerDomain with TransformerDTOsHTTP with ErrorsTransformer {
  val logger: Logger = Logger(this.getClass)

  implicit lazy val executor: Scheduler = monix.execution.Scheduler.Implicits.global

  def execute(dto: ProjectIDDTO): Task[Consequence] = {
    (for {
      x <- updateInfoProject(dto.id).toEitherT
    } yield x)
      .fold(leftConsequence, r => rightConsequence(Json.toJson(r)))
  }

  def updateInfoProject(projectID: Int) = {
    Task.eval {
      projectService.registerCommits(projectID)
        .fold(Json.toJson(_), Json.toJson(_))
        .doOnFinish(_ => projectService.finishUpdating(projectID))
        .foreach(i => logger.info(s"Response: $i"))

      MessageDTO("Updating info").asRight[GError]
    }
  }
}

