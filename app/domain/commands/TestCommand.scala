package domain.commands

import domain.services.ProjectService
import infrastructure.TransformerDTOs
import javax.inject.Inject
import monix.eval.Task
import play.api.libs.json.{JsValue, Json}

class TestCommand @Inject()(projectService: ProjectService) extends Command with TransformerDTOs {

  def execute(jsValue: JsValue): Task[Consequence] = {
    val projectId = 580

    projectService.register(projectId)
      .fold(leftConsequence, r => rightConsequence(Json.toJson(r)))
  }
}
