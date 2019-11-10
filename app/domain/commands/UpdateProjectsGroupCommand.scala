package domain.commands

import cats.implicits._
import controllers.ErrorsTransformer
import domain.model.GError
import domain.services.ProjectService
import implicits.implicits._
import infrastructure._
import javax.inject.Inject
import monix.eval.Task
import monix.execution.Scheduler
import play.api.Logger
import play.api.libs.json.{JsValue, Json}

class UpdateProjectsGroupCommand @Inject()(projectService: ProjectService) extends Command[GroupIDDTO] with TransformerDTOsHTTP with ErrorsTransformer {
  val logger: Logger = Logger(this.getClass)

  implicit lazy val executor: Scheduler = monix.execution.Scheduler.Implicits.global

  def execute(dto: GroupIDDTO): Task[Consequence] = {
    (for {
      g <- projectService.getGroup(dto.id).toEitherT
      x <- updateInfoGroup(g.id).toEitherT
    } yield x)
      .fold(leftConsequence, r => rightConsequence(Json.toJson(r)))
  }

  private def updateInfoGroup(groupID: Int): Task[Either[GError, InfoUpdated]] = {
    projectService.getProjectsByGroup(groupID)
      .flatMap(projects => Task.traverse(projects){project => updateInfoProject(project.id)})
      .map(r => InfoUpdated("Info updated", r.sum).asRight)
  }

  private def updateInfoProject(projectID: Int): Task[Int] = {
    projectService.updateInfoProject(projectID)
      .fold(l => 0, r => (r.commits.commits.size + r.issues.size + r.prs.size))
      .recover{case error => projectService.finishUpdating(projectID).runToFuture; 0}
  }

}
