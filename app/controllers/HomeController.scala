package controllers

import cats.data.EitherT
import cats.implicits._
import domain.services.ProjectService
import infrastructure.TransformerDTOsHTTP
import javax.inject._
import persistence.querys.ProjectQueryDAO
import play.api.libs.json.Json
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class HomeController @Inject()(projectQueryDAO: ProjectQueryDAO, projectService: ProjectService, cc: ControllerComponents)(implicit ec: ExecutionContext)
  extends AbstractController(cc) with TransformerDTOsHTTP {

  def index() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.index())
  }

  def test() = Action.async { implicit request: Request[AnyContent] =>
    projectQueryDAO.commitsForUser().map(r => Ok(Json.toJson(r)))
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
}
