package domain.services

import cats.data.EitherT
import cats.implicits._
import domain.model.GError.DomainError
import domain.model.{GError, Project}
import domain.repositories.project.ProjectRepository
import implicits.implicits._
import infrastructure.{CommitGitLabDTO, ProjectGitLabDTO}
import infrastructure.gitlab.GitLabService
import javax.inject.Inject
import monix.eval.Task

class ProjectService @Inject()(projectRepositoy: ProjectRepository, gitLab: GitLabService) {

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
          x <- gitLab.getAllCommits(proyectId).toEitherT
        } yield x



    }

    def validateExistProject(project: Project): Task[Either[DomainError, Project]] = {
      projectRepositoy.findByID(project.id)
        .map(opt => Either.cond( opt.isEmpty, project, DomainError("Project exist", "12201") ))
    }





    def transformProject(dto: ProjectGitLabDTO): EitherT[Task, GError, Project] = EitherT.fromEither {
      Project(dto.id, dto.description, dto.name, dto.name_with_namespace, dto.path, dto.path_with_namespace,
        dto.created_at, dto.default_branch, dto.ssh_url_to_repo, dto.http_url_to_repo, dto.web_url).asRight[GError]
    }

    def transformCommits(dtos: List[CommitGitLabDTO]) = {

      1
    }

}
