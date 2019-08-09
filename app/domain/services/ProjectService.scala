package domain.services

import cats.data.EitherT
import cats.implicits._
import domain.model.GError.DomainError
import domain.model.{Commit, GError, Project}
import domain.repositories.commit.CommitRepository
import domain.repositories.project.ProjectRepository
import implicits.implicits._
import infrastructure.{CommitGitLabDTO, ProjectGitLabDTO}
import infrastructure.gitlab.GitLabService
import javax.inject.Inject
import monix.eval.Task

class ProjectService @Inject()(projectRepositoy: ProjectRepository, commitRepository: CommitRepository, gitLab: GitLabService) {

    def register(proyectId: Int): EitherT[Task, GError, Project] = {
      for {
        d <- gitLab.getProject(proyectId).toEitherT
        p <- transformProject(d)
        _ <- validateExistProject(p).toEitherT
        z <- projectRepositoy.insertEither(p).toEitherT
      } yield z
    }

    def registerCommits(proyectId: Int) = {
        for {
          a <- commitRepository.getLastDateCommit().map(_.asRight[GError]).toEitherT
          x <- gitLab.getAllCommits(proyectId, a).toEitherT
          c <- transformCommits(x, proyectId)
          f <- filterCommits(c).map(_.asRight[GError]).toEitherT
          z <- commitRepository.insertAll(f).map(_.asRight[GError]).toEitherT
        } yield z


    }

    private def validateExistProject(project: Project): Task[Either[GError, Project]] = {
      projectRepositoy.findByID(project.id)
        .map(opt => Either.cond( opt.isEmpty, project, DomainError("Project exist", "12201") ))
    }

    private def filterCommits(commits: List[Commit]): Task[List[Commit]] = {
      commitRepository.getExistingId(commits).map(already => filterCommits(commits, already))
    }

    private def filterCommits(commits: List[Commit], commitsAlready: List[Commit]): List[Commit] = {
      val idsAlready = commitsAlready.map(_.id)
      commits.filter(c => !idsAlready.contains(c.id))
    }

  private def transformProject(dto: ProjectGitLabDTO): EitherT[Task, GError, Project] = EitherT.fromEither {
    Project(dto.id, dto.description, dto.name, dto.name_with_namespace, dto.path, dto.path_with_namespace,
      dto.created_at, dto.default_branch, dto.ssh_url_to_repo, dto.http_url_to_repo, dto.web_url).asRight
  }

  private def transformCommits(dtos: List[CommitGitLabDTO], projectId: Int): EitherT[Task, GError, List[Commit]] = EitherT.fromEither {
    dtos.map(c => Commit(c.id, c.short_id, c.created_at, c.parent_ids.mkString(","), c.title, c.message, c.author_name,
      c.author_email, c.authored_date, c.committer_name, c.committer_email, c.committed_date, projectId)).asRight
  }

}
