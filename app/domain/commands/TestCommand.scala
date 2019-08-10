package domain.commands

import domain.model.TransformerDomain
import domain.services.ProjectService
import infrastructure.Test
import javax.inject.Inject
import monix.eval.Task
import play.api.libs.json.Json

class TestCommand @Inject()(projectService: ProjectService) extends Command[Test] with TransformerDomain {

  def execute(value: Test): Task[Consequence] = {

    val projectId = value.count
    projectService.register(projectId)
      .fold(leftConsequence, r => rightConsequence(Json.toJson(r)))
  }
}
