package controllers

import cats.data.EitherT
import cats.implicits._
import infrastructure._
import infrastructure.gitlab.GitLabService
import javax.inject._
import monix.eval.Task
import monix.execution.Scheduler.Implicits.global
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(gitLab: GitLabService, http: ServiceHTTP, cc: ControllerComponents)(implicit ec: ExecutionContext)
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

    for {
      x <- EitherT(gitLab.getProject(projectId))
      y <- EitherT(gitLab.getAllCommits(projectId))
      z <- EitherT(traverseFold(y)( commit => gitLab.getCommitsDiff(projectId, commit.id)))
    } yield {
      (x, y, z)
    }

    /*
    (for {
      _ <- EitherT(testTask)
      y <- EitherT(Task.deferFuture(gitLab.getProject(580)))
    } yield y.response)
      .fold(left => left + "=(", right => right)
      .foreach(println(_))
     */

    Ok(views.html.index())
  }

  def traverseFold[L, R, T](elements: List[T])(f: T => Future[Either[L, R]]): Future[Either[L, List[R]]] = {
    elements.foldLeft( EitherT(Future.successful(List.empty[R].asRight[L])) ) {
      (acc, nxt) => acc.flatMap(list => EitherT(f(nxt)).map(as => as:: list))
    }.value
  }

  def sequence[L, R](eithers: List[Either[L, R]]): Either[L, List[R]] = {
    eithers.foldRight(List.empty[R].asRight[L])( (elem, acc) => acc.flatMap(lista => elem.map(_ :: lista)) )
  }

  def testTask: Task[Either[String, Int]] = Task(1.asRight)

  def testFuture: Future[Either[String, Int]] = Future(2.asRight)

}
