package domain.repositories.project

import cats.implicits._
import domain.model.GError.DomainError
import domain.model.{Commit, Commits, Diff, GError, Project}
import domain.repositories.commit.CommitAdapter
import domain.repositories.diff.DiffAdapter
import javax.inject.Inject
import monix.eval.Task
import persistence.project.ProjectDAO

class ProjectRepository @Inject()(projectDAO: ProjectDAO) extends ProjectAdapter with CommitAdapter with DiffAdapter {

    def findByID(id: Int): Task[Option[Project]] = Task.deferFuture{
        projectDAO findByID id
    }.map(_ map transform)

    def getProjectsByGroup(groupId: Int): Task[Seq[Project]] = Task.deferFuture {
        projectDAO getProjectsByGroup groupId
    }.map(_ map transform)

    def insert(project: Project): Task[Project] = Task.deferFuture{
        projectDAO insert transform(project)
    } map transform

    def insertEither(project: Project): Task[Either[GError, Project]] = insert(project).map(_.asRight[GError])

    def insertInfoCommits(commits: List[Commit], diffs: List[Diff]): Task[Commits] = Task.deferFuture {
        projectDAO.insertInfoCommits(commits.map(transform), diffs.map(transform))
    }.map( r => Commits(r._1.map(transform), r._2.map(transform)) )

    def findByIDEither(id: Int): Task[Either[GError, Project]] = {
        findByID(id).map(_.toRight(DomainError("Project not found", "12101")))
    }

    def validateNotExistProject(projectId: Int): Task[Either[GError, Int]] = {
        findByID(projectId)
          .map(opt => Either.cond(opt.isEmpty, projectId, DomainError("Project exist", "12201")))
    }

    def onUpdating(projectId: Int): Task[Either[GError, Int]] = Task.deferFuture {
        projectDAO onUpdating projectId
    }.map(n => Either.cond(n > 0, n, DomainError("Updating project", "13000")))

    def offUpdating(projectId: Int): Task[Either[GError, Int]] = Task.deferFuture {
        projectDAO offUpdating projectId
    }.map(_.asRight)

    def deleteInfoProject(projectId: Int): Task[Option[Project]] = Task.deferFuture {
        projectDAO deleteInfoProject projectId
    }.map(_ map transform)

    def deleteInfoProjectE(projectId: Int): Task[Either[DomainError, Project]] = {
        deleteInfoProject(projectId).map(_.toRight(DomainError("Project not found", "12101")))
    }
}
