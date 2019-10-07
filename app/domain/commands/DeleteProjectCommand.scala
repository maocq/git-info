package domain.commands

import controllers.ErrorsTransformer
import implicits.implicits._
import domain.model.TransformerDomain
import domain.services.ProjectService
import infrastructure.ProjectIDDTO
import javax.inject.Inject
import monix.eval.Task
import monix.execution.Scheduler
import play.api.Logger
import play.api.libs.json.Json

class DeleteProjectCommand @Inject()(projectService: ProjectService) extends Command[ProjectIDDTO] with TransformerDomain with ErrorsTransformer {
  val logger: Logger = Logger(this.getClass)

  implicit lazy val executor: Scheduler = monix.execution.Scheduler.Implicits.global

  def execute(dto: ProjectIDDTO): Task[Consequence] = {
   projectService.deleteProject(dto.id).toEitherT
     .fold(leftConsequence, r => rightConsequence(Json.toJson(r)))
  }
}
