package domain.services

import cats.data.EitherT
import cats.implicits._
import domain.model.{GError, Project}
import domain.repositories.ProjectRepository
import implicits.implicits._
import infrastructure.ProjectGitLabDTO
import infrastructure.gitlab.GitLabService
import javax.inject.Inject
import monix.eval.Task

class ProjectService @Inject()(projectRepositoy: ProjectRepository, gitLab: GitLabService) {

    def register(id: Int): EitherT[Task, GError, Project] = {
      for {
        d <- gitLab.getProject(id).toEitherT
        p <- EitherT.fromEither(toProject(d))
        z <- projectRepositoy.insertEither(p).toEitherT
      } yield z
    }


    def toProject(dto: ProjectGitLabDTO): Either[GError, Project] = {
      Project(dto.id, dto.description, dto.name, dto.name_with_namespace, dto.path, dto.path_with_namespace,
        dto.created_at, dto.default_branch, dto.ssh_url_to_repo, dto.http_url_to_repo, dto.web_url).asRight[GError]
    }

}
