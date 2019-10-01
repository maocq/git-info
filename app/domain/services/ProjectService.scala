package domain.services

import java.time.ZonedDateTime

import cats.data.EitherT
import cats.implicits._
import domain.model.GError.ValidationError
import domain.model._
import domain.repositories.commit.CommitRepository
import domain.repositories.group.GroupRepository
import domain.repositories.issue.IssueRepository
import domain.repositories.project.ProjectRepository
import implicits.implicits._
import infrastructure.gitlab.GitLabService
import infrastructure.{CommitDiffGitLabDTO, CommitGitLabDTO, IssueGitLabDTO, ProjectGitLabDTO}
import javax.inject.Inject
import monix.eval.Task

class ProjectService @Inject()(
  grouppRepository: GroupRepository, projectRepositoy: ProjectRepository,
  commitRepository: CommitRepository, issueRepository: IssueRepository, gitLab: GitLabService
) {

  def getProject(proyectId: Int): Task[Either[GError, Project]] = projectRepositoy.findByIDEither(proyectId)

  def registerGroup(name: String): EitherT[Task, GError, Group] = {
    for {
      g <- validateGroup(0, name)
      r <- grouppRepository.insert(g).map(_.asRight[GError]).toEitherT
    } yield r
  }

  def registerIssues(projectId: Int): EitherT[Task, GError, List[Issue]] = {
    for {
      _ <- projectRepositoy.findByIDEither(projectId).toEitherT
      l <- issueRepository.getLastDateIssues(projectId).map(_.asRight[GError]).toEitherT
      a <- gitLab.getAllIssues(projectId, l).toEitherT
      i <- transformIssues(a)
      r <- issueRepository.insertAll(i).map(_.asRight[GError]).toEitherT
    } yield r
  }

  def registerProject(proyectId: Int, groupId: Int): EitherT[Task, GError, Project] = for {
      g <- grouppRepository.findByIDEither(groupId).toEitherT
      _ <- projectRepositoy.validateNotExistProject(proyectId).toEitherT
      d <- gitLab.getProject(proyectId).toEitherT
      p <- transformProject(d, g.id)
      r <- projectRepositoy.insertEither(p).toEitherT
    } yield r

  def registerCommits(projectId: Int): EitherT[Task, GError, (List[Commit], List[Diff])] = for {
      _ <- projectRepositoy.findByIDEither(projectId).toEitherT
      _ <- projectRepositoy.onUpdating(projectId).toEitherT
      l <- commitRepository.getLastDateCommit(projectId).map(_.asRight[GError]).toEitherT
      a <- gitLab.getAllCommits(projectId, l).toEitherT
      c <- transformCommits(a, projectId)
      f <- filterCommits(c).map(_.asRight[GError]).toEitherT
      t <- traverseFold(f)(commit => gitLab.getCommitsDiff(projectId, commit.id))
      d <- transformDiffs(t)
      r <- projectRepositoy.insertInfoCommits(f, d).map(_.asRight[GError]).toEitherT
      _ <- projectRepositoy.offUpdating(projectId).toEitherT
    } yield r

  def finishUpdating(projectId: Int): Task[Either[GError, Int]] = {
    projectRepositoy.offUpdating(projectId)
  }

  private def validateGroup(id: Int, name: String): EitherT[Task, GError, Group] = EitherT.fromEither {
    Group(id, name, ZonedDateTime.now()).validar.leftMap(error => ValidationError("Validation error", "20000", error))
  }

  private def filterCommits(commits: List[Commit]): Task[List[Commit]] = {
    def filterCommits(commits: List[Commit], commitsAlready: List[Commit]): List[Commit] = {
      val idsAlready = commitsAlready.map(_.id)
      commits.filter(c => !idsAlready.contains(c.id))
    }
    commitRepository.getExistingId(commits).map(filterCommits(commits, _))
  }

  private def transformProject(dto: ProjectGitLabDTO, groupId: Int): EitherT[Task, GError, Project] = EitherT.fromEither {
    Project(dto.id, dto.description, dto.name, dto.name_with_namespace, dto.path, dto.path_with_namespace,
      dto.created_at, dto.default_branch, dto.ssh_url_to_repo, dto.http_url_to_repo, dto.web_url, groupId).asRight
  }

  private def transformCommits(dtos: List[CommitGitLabDTO], projectId: Int): EitherT[Task, GError, List[Commit]] = EitherT.fromEither {
    dtos.map(c => Commit(c.id, c.short_id, c.created_at, c.parent_ids.mkString(","), c.title, c.message, c.author_name,
      c.author_email, c.authored_date, c.committer_name, c.committer_email, c.committed_date, projectId)).asRight
  }

  private def transformIssues(dtos: List[IssueGitLabDTO]): EitherT[Task, GError, List[Issue]] = EitherT.fromEither {
    dtos.map(i => Issue(i.id, i.iid, i.project_id, i.title, i.description, i.state, i.created_at, i.updated_at, i.closed_at,
      i.closed_by.map(_.id), i.author.id, i.assignee.map(_.id), i.web_url)).asRight
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
