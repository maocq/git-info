package controllers

import cats.data.EitherT
import cats.implicits._
import infrastructure._
import javax.inject._
import monix.eval.Task
import monix.execution.Scheduler.Implicits.global
import play.api.mvc._

import scala.concurrent.Future

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(http: ServicioHTTP, cc: ControllerComponents)
  extends AbstractController(cc) with TransformadorDTOs {

  /**
   * Create an Action to render an HTML page.
   *
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def index() = Action { implicit request: Request[AnyContent] =>

    (for {
      _ <- EitherT(testTask)
      y <- EitherT(Task.deferFuture(commitsDiff))
    } yield y.respuesta)
      .fold(left => left + "=(", right => right)
      .foreach(println(_))

    Ok(views.html.index())
  }

  def testTask: Task[Either[String, Int]] = Task(1.asRight)

  def testFuture: Future[Either[String, Int]] = Future(2.asRight)

  def commits: Future[Either[String, RespuestaHTTP[List[CommitGitLabDTO]]]] = {
    http.get[List[CommitGitLabDTO]]("https://gitlab.seven4n.com/api/v4/projects/586/repository/commits?per_page=100", Map("Private-Token" -> "mx7o6YbX7euiykysiGMg"))
      .map(_.leftMap(_.toString))
  }

  def commitsDiff: Future[Either[String, RespuestaHTTP[List[CommitDiffGitLabDTO]]]] = {
    http.get[List[CommitDiffGitLabDTO]](
      "https://gitlab.seven4n.com/api/v4/projects/580/repository/commits/56829dddb4d80b6e51de207e51a5baa1e66edfaf/diff", Map("Private-Token" -> "mx7o6YbX7euiykysiGMg"))
      .map(_.leftMap(_.toString))
  }

}
