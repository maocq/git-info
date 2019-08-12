package domain.commands

import domain.model.TransformerDomain
import domain.services.ProjectService
import infrastructure.ProjectIDDTO
import javax.inject.Inject
import monix.eval.Task
import play.api.libs.json.Json

class RegisterProjectCommand @Inject()(projectService: ProjectService) extends Command[ProjectIDDTO] with TransformerDomain {

  def execute(dto: ProjectIDDTO): Task[Consequence] = {

    projectService.register(dto.id)
      .fold(leftConsequence, r => rightConsequence(Json.toJson(r)))
  }
}
