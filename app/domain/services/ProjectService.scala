package domain.services

import cats.data.EitherT
import cats.implicits._
import domain.model.{Commit, Diff, GError, Project}
import domain.repositories.commit.CommitRepository
import domain.repositories.project.ProjectRepository
import implicits.implicits._
import infrastructure.gitlab.GitLabService
import infrastructure.{CommitDiffGitLabDTO, CommitGitLabDTO, ProjectGitLabDTO}
import javax.inject.Inject
import monix.eval.Task

class ProjectService @Inject()(projectRepositoy: ProjectRepository, commitRepository: CommitRepository, gitLab: GitLabService) {

  def register(proyectId: Int): EitherT[Task, GError, Project] = for {
      _ <- projectRepositoy.validateNotExistProject(proyectId).toEitherT
      d <- gitLab.getProject(proyectId).toEitherT
      p <- transformProject(d)
      r <- projectRepositoy.insertEither(p).toEitherT
    } yield r

  def registerCommits(projectId: Int): EitherT[Task, GError, (List[Commit], List[Diff])] = for {
      _ <- projectRepositoy.findByIDEither(projectId).toEitherT
      l <- commitRepository.getLastDateCommit(projectId).map(_.asRight[GError]).toEitherT
      a <- gitLab.getAllCommits(projectId, l).toEitherT
      c <- transformCommits(a, projectId)
      f <- filterCommits(c).map(_.asRight[GError]).toEitherT
      t <- traverseFold(f)(commit => gitLab.getCommitsDiff(projectId, commit.id))
      d <- transformDiffs(t)
      r <- projectRepositoy.insertInfoCommits(f, d).map(_.asRight[GError]).toEitherT
    } yield r


  private def filterCommits(commits: List[Commit]): Task[List[Commit]] = {
    def filterCommits(commits: List[Commit], commitsAlready: List[Commit]): List[Commit] = {
      val idsAlready = commitsAlready.map(_.id)
      commits.filter(c => !idsAlready.contains(c.id))
    }
    commitRepository.getExistingId(commits).map(filterCommits(commits, _))
  }

  private def transformProject(dto: ProjectGitLabDTO): EitherT[Task, GError, Project] = EitherT.fromEither {
    Project(dto.id, dto.description, dto.name, dto.name_with_namespace, dto.path, dto.path_with_namespace,
      dto.created_at, dto.default_branch, dto.ssh_url_to_repo, dto.http_url_to_repo, dto.web_url).asRight
  }

  private def transformCommits(dtos: List[CommitGitLabDTO], projectId: Int): EitherT[Task, GError, List[Commit]] = EitherT.fromEither {
    dtos.map(c => Commit(c.id, c.short_id, c.created_at, c.parent_ids.mkString(","), c.title, c.message, c.author_name,
      c.author_email, c.authored_date, c.committer_name, c.committer_email, c.committed_date, projectId)).asRight
  }

  def transformDiffs(diffs: List[(String, List[CommitDiffGitLabDTO])]): EitherT[Task, GError, List[Diff]] = EitherT.fromEither {
    diffs.flatMap(list => list._2.map(d => {
      val lines = d.diff.split("\n");
      Diff(0, d.old_path, d.new_path, d.a_mode, d.b_mode, d.new_file, d.renamed_file, d.deleted_file, d.diff, lines.count(_.startsWith("+")), lines.count(_.startsWith("-")), list._1)
    })).asRight
  }

  def traverseFold[L, R, T](elements: List[T])(f: T => Task[Either[L, R]]): EitherT[Task, L, List[R]] = {
    elements.foldLeft(EitherT(Task.now(List.empty[R].asRight[L]))) {
      (acc, nxt) => acc.flatMap(list => EitherT(f(nxt)).map(list :+ _))
    }
  }

}
