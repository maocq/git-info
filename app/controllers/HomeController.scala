package controllers

import cats.data.EitherT
import cats.implicits._
import infrastructure.TransformerDTOsHTTP
import infrastructure.gitlab.GitLabService
import javax.inject._
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class HomeController @Inject()(cc: ControllerComponents, gitLabService: GitLabService)(implicit ec: ExecutionContext)
  extends AbstractController(cc) with TransformerDTOsHTTP {

  def index() = Action { implicit request: Request[AnyContent] =>
    gitLabService.getMergeRequest(174, 1).map(s => s.toString).recover{case es => es.toString}.foreach(e => println(e))
    Ok(views.html.index())
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
