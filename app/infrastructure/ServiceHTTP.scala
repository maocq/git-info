package infrastructure

import cats.implicits._
import javax.inject.Inject
import play.api.libs.json.Reads
import play.api.libs.ws.{WSClient, WSResponse}
import play.mvc.Http.Status.{OK, CREATED, ACCEPTED}

import scala.concurrent.{ExecutionContext, Future}

class ServiceHTTP @Inject()(ws: WSClient)(implicit ec: ExecutionContext)  {

  def get[A](url: String)(implicit m: Reads[A]): Future[Either[ErrorHTTP, ResponseHTTP[A]]] = get(url, Map())

  def get[A](url: String, headers: Map[String, String])(implicit m: Reads[A]): Future[Either[ErrorHTTP, ResponseHTTP[A]]] = {

    ws.url(url)
      .addHttpHeaders(headers.toSeq:_*).get()
      .map(validateEstate(_).flatMap(getDTO(_)))
  }

  private def validateEstate(response: WSResponse): Either[ErrorHTTP, WSResponse] = response.status match {
        case OK | CREATED | ACCEPTED  => response.asRight
        case _ => ErrorHTTP("Error status", response).asLeft
  }

  private def getDTO[A](response: WSResponse)(implicit m: Reads[A]): Either[ErrorHTTP, ResponseHTTP[A]] = {
    response.json.validate[A].fold(
      error => ErrorHTTP("Error Json " + error, response).asLeft,
      dto => ResponseHTTP(dto, response).asRight
    )
  }

}
