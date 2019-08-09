package domain.repositories

import cats.implicits._
import domain.model.GError.DomainError
import domain.model.{GError, Project}
import javax.inject.Inject
import monix.eval.Task
import persistence.project.{ProjectDAO, ProjectRecord}

class ProjectRepository @Inject()(projectDAO: ProjectDAO) extends ProjectAdapter {

    def findByID(id: Int): Task[Option[Project]] = Task.deferFuture{
        projectDAO findByID id
    }.map(_ map transform)

    def insert(project: Project): Task[Project] = Task.deferFuture{
        projectDAO insert transform(project)
    } map transform

    def insertEither(project: Project): Task[Either[GError, Project]] = insert(project).map(_.asRight[GError])

    def findByIDEither(id: Int): Task[Either[DomainError, Project]] = {
        findByID(id).map(ee => ee.toRight(DomainError("Project not found", "12101")))
    }

}
