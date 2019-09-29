package domain.commands

import controllers.ErrorsTransformer
import domain.model.TransformerDomain
import domain.services.ProjectService
import infrastructure.GroupDTO
import javax.inject.Inject
import monix.eval.Task
import monix.execution.Scheduler
import play.api.libs.json.Json

class RegisterGroupCommand @Inject()(projectService: ProjectService) extends Command[GroupDTO] with TransformerDomain with ErrorsTransformer {

  implicit lazy val executor: Scheduler = monix.execution.Scheduler.Implicits.global

  def execute(dto: GroupDTO): Task[Consequence] = {
    projectService.registerGroup(dto.name)
      .fold(leftConsequence, r => rightConsequence(Json.toJson(r)))
  }
}
