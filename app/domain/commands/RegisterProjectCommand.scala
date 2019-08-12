package domain.commands

import cats.implicits._
import domain.model.{GError, TransformerDomain}
import domain.services.ProjectService
import implicits.implicits._
import infrastructure.ProjectIDDTO
import javax.inject.Inject
import monix.eval.Task
import monix.execution.Scheduler.Implicits.global
import play.api.libs.json.Json


class RegisterProjectCommand @Inject()(projectService: ProjectService) extends Command[ProjectIDDTO] with TransformerDomain {

  def execute(dto: ProjectIDDTO): Task[Consequence] = {
    (for {
        x <- projectService.register(dto.id)
        _ <- updateInfoProject(dto.id).toEitherT
    } yield x)
      .fold(leftConsequence, r => rightConsequence(Json.toJson(r)))
  }

  def updateInfoProject(projectID: Int) = {
    Task.eval{
      projectService.registerCommits(projectID).value.runToFuture
      "ok".asRight[GError]
    }
  }
}