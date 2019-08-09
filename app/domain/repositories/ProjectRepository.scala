package domain.repositories

import domain.model.Project
import javax.inject.Inject
import monix.eval.Task
import persistence.project.{ProjectDAO, ProjectRecord}

class ProjectRepository @Inject()(projectDAO: ProjectDAO) extends ProjectAdapter {

    def findByID(id: Int): Task[Option[ProjectRecord]] = Task.deferFuture{
        projectDAO findByID id
    }

    def insert(project: Project): Task[ProjectRecord] = Task.deferFuture{
        projectDAO insert transform(project)
    }

}
