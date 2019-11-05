package domain.commands

import controllers.ErrorsTransformer
import domain.model.TransformerDomain
import domain.services.ProjectService
import infrastructure.{GroupDTO, GroupUpdateDTO}
import javax.inject.Inject
import monix.eval.Task
import monix.execution.Scheduler
import play.api.libs.json.Json

class UpdateGroupCommand @Inject()(projectService: ProjectService) extends Command[GroupUpdateDTO] with TransformerDomain with ErrorsTransformer {

  implicit lazy val executor: Scheduler = monix.execution.Scheduler.Implicits.global

  def execute(dto: GroupUpdateDTO): Task[Consequence] = {
    projectService.updateGroup(dto.id, dto.name)
      .fold(leftConsequence, r => rightConsequence(Json.toJson(r)))
  }
}
