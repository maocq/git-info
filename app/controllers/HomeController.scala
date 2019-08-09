package controllers

import cats.data.EitherT
import cats.implicits._
import domain.repositories.project.ProjectRepository
import domain.services.ProjectService
import implicits.implicits._
import infrastructure._
import infrastructure.gitlab.GitLabService
import javax.inject._
import monix.eval.Task
import monix.execution.Scheduler.Implicits.global
import persistence.commit.{CommitDAO, CommitRecord}
import persistence.diff.{DiffDAO, DiffRecord}
import persistence.project.{ProjectDAO, ProjectRecord}
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(projectService: ProjectService, pr: ProjectRepository, projectDAO: ProjectDAO, commitDAO: CommitDAO, diffDAO: DiffDAO, gitLab: GitLabService, http: ServiceHTTP, cc: ControllerComponents)(implicit ec: ExecutionContext)
  extends AbstractController(cc) with TransformerDTOs {

  /**
   * Create an Action to render an HTML page.
   *
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def index() = Action { implicit request: Request[AnyContent] =>

    val projectId = 586
    /*
    projectService.register(projectId)
      .fold(left => left.toString, right => right.toString)
      .foreach(println(_))
     */

    projectService.registerCommits(projectId)
      .fold(left => left.toString, right => right.toString)
      .recover{case err => err.getMessage}
      .foreach(println(_))

  /*
    for {
      x <- gitLab.getProject(projectId).toEitherT
      y <- gitLab.getAllCommits(projectId).toEitherT
      z <- traverseFold(y)( commit => gitLab.getCommitsDiff(projectId, commit.id)).toEitherT
      a <- insertarProject(x).toEitherT
      b <- insertarCommits(y, a.id).toEitherT
      c <- insertarDiffs(z).toEitherT
    } yield {
      (x, y, z)
    }
   */

    Ok(views.html.index())
  }

  def insertarProject(p: ProjectGitLabDTO): Future[Either[String, ProjectRecord]] = {
    projectDAO.insert(ProjectRecord(p.id, p.description, p.name, p.name_with_namespace, p.path, p.path_with_namespace,
      p.created_at, p.default_branch, p.ssh_url_to_repo, p.http_url_to_repo, p.web_url)).map(_.asRight)
  }

  def insertarCommits(commits: List[CommitGitLabDTO], projectId: Int): Future[Either[String, List[CommitRecord]]] = {
    val records = commits.map(c => CommitRecord(c.id, c.short_id, c.created_at, c.parent_ids.toString(), c.title, c.message, c.author_name,
      c.author_email, c.authored_date, c.committer_name, c.committer_email, c.committed_date, projectId))
    commitDAO.insertAll(records).map(_.asRight)
  }

  def insertarDiffs(diffs: List[(String, List[CommitDiffGitLabDTO])]): Future[Either[String, List[DiffRecord]]] = {

    val records = diffs.flatMap(list => list._2.map(d => {
      val add = d.diff.split("\\n").filter(_.startsWith("+")).size
      val del = d.diff.split("\\n").filter(_.startsWith("-")).size
      DiffRecord(0, d.old_path, d.new_path, d.a_mode, d.b_mode, d.new_file, d.renamed_file, d.deleted_file, d.diff, add, del, list._1)
    }))
    diffDAO.insertAll(records).map(_.asRight)
  }

  def traverseFold[L, R, T](elements: List[T])(f: T => Future[Either[L, R]]): Future[Either[L, List[R]]] = {
    elements.foldLeft( EitherT(Future.successful(List.empty[R].asRight[L])) ) {
      (acc, nxt) => acc.flatMap(list => EitherT(f(nxt)).map(list :+ _))
    }.value
  }

  def traverseFoldF[A, T](elements: List[T])(f: T => Future[A]): Future[List[A]] = {
    elements.foldLeft(Future.successful(List.empty[A])) { case (acc, nxt) => acc.flatMap(list => f(nxt).map(list :+ _)) }
  }

  def sequenceEither[L, R](eithers: List[Either[L, R]]): Either[L, List[R]] = {
    eithers.foldLeft(List.empty[R].asRight[L])( (acc, nxt) => acc.flatMap(list => nxt.map(list :+ _)) )
  }

  def testTask: Task[Either[String, Int]] = Task(1.asRight)

  def testFuture: Future[Either[String, Int]] = Future(2.asRight)

}
