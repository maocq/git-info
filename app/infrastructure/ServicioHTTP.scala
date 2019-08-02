package infrastructure

import cats.implicits._
import javax.inject.Inject
import play.api.libs.json.Reads
import play.api.libs.ws.{WSClient, WSResponse}
import play.mvc.Http.Status.{OK, CREATED, ACCEPTED}

import scala.concurrent.{ExecutionContext, Future}

class ServicioHTTP @Inject() (ws: WSClient)(implicit ec: ExecutionContext)  {

  def get[A](url: String)(implicit m: Reads[A]): Future[Either[ErrorHTTP, ResponseHTTP[A]]] = get(url, Map())

  def get[A](url: String, headers: Map[String, String])(implicit m: Reads[A]): Future[Either[ErrorHTTP, ResponseHTTP[A]]] = {

    ws.url(url)
      .addHttpHeaders(headers.toSeq:_*).get()
      .map(validarEstado(_).flatMap(obtenerDTO(_)))
  }

  private def validarEstado(respuesta: WSResponse): Either[ErrorHTTP, WSResponse] = respuesta.status match {
        case OK | CREATED | ACCEPTED  => respuesta.asRight
        case _ => ErrorHTTP("Error status", respuesta).asLeft
  }

  private def obtenerDTO[A](respuesta: WSResponse)(implicit m: Reads[A]): Either[ErrorHTTP, ResponseHTTP[A]] = {
    respuesta.json.validate[A].fold(
      error => ErrorHTTP("Error Json " + error, respuesta).asLeft,
      dto => ResponseHTTP(dto, respuesta).asRight
    )
  }

}
