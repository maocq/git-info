package domain.commands

import cats.implicits._
import domain.model.GError
import domain.services.ProjectService
import implicits.implicits._
import infrastructure.{MessageDTO, ProjectIDDTO, TransformerDTOsHTTP}
import javax.inject.Inject
import monix.eval.Task
import monix.execution.Scheduler
import play.api.libs.json.Json

class UpdateProjectCommand @Inject()(projectService: ProjectService) extends Command[ProjectIDDTO] with TransformerDTOsHTTP {

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
        .fold(l => "Error =(", r => "Ok =)")
        .doOnFinish(_ => projectService.finishUpdating(projectID))
        .runToFuture

      MessageDTO("Updating info").asRight[GError]
    }
  }
}

