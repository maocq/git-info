package controllers

import cats.data.EitherT
import cats.implicits._
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
class HomeController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  /**
   * Create an Action to render an HTML page.
   *
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def index() = Action { implicit request: Request[AnyContent] =>

    (for {
      x <- EitherT(testTask)
      y <- EitherT(Task.deferFuture(testFuture))
    } yield x + y)
      .fold(left => left + "=(", right => right + " =)")
      .foreach(println(_))

    Ok(views.html.index())
  }

  def testTask: Task[Either[String, Int]] = Task(1.asRight)

  def testFuture: Future[Either[String, Int]] = Future(2.asRight)


}
