package controllers

import cats.data.EitherT
import cats.implicits._
import infrastructure._
import infrastructure.gitlab.GitLabService
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
class HomeController @Inject()(gitLab: GitLabService, http: ServiceHTTP, cc: ControllerComponents)
  extends AbstractController(cc) with TransformerDTOs {

  /**
   * Create an Action to render an HTML page.
   *
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def index() = Action { implicit request: Request[AnyContent] =>

    gitLab.getAllCommits(580).foreach(all =>{
      println(all)
    })

    /*
    for {
      x <- EitherT(gitLab.getProject(580))
      y <- EitherT(gitLab.getCommits(580))
      z <- EitherT(gitLab.getCommitsDiff(580, "56829dddb4d80b6e51de207e51a5baa1e66edfaf"))
    } yield {
      (x, y, z)
    }
     */

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

  def testTask: Task[Either[String, Int]] = Task(1.asRight)

  def testFuture: Future[Either[String, Int]] = Future(2.asRight)

}
