package domain.commands

import cats.implicits._
import domain.model.{GError, TransformerDomain}
import domain.services.ProjectService
import implicits.implicits._
import infrastructure.ProjectIDDTO
import javax.inject.Inject
import monix.eval.Task
import monix.execution.Scheduler
import play.api.libs.json.Json


class RegisterProjectCommand @Inject()(projectService: ProjectService) extends Command[ProjectIDDTO] with TransformerDomain {

  implicit lazy val executor: Scheduler = monix.execution.Scheduler.Implicits.global

  def execute(dto: ProjectIDDTO): Task[Consequence] = {
    (for {
        x <- projectService.register(dto.id)
        _ <- updateInfoProject(dto.id).toEitherT
    } yield x)
      .fold(leftConsequence, r => rightConsequence(Json.toJson(r)))
  }

  def updateInfoProject(projectID: Int) = {
    Task.eval {
      projectService.registerCommits(projectID)
        .fold(l => "=(", r => "=)")
        .doOnFinish(_ => projectService.finishUpdating(projectID))
        .runToFuture

      "ok".asRight[GError]
    }
  }
}
