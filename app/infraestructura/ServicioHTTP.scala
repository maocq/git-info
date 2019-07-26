package infraestructura

import cats.implicits._
import javax.inject.Inject
import play.api.libs.json.Reads
import play.api.libs.ws.{WSClient, WSResponse}
import play.mvc.Http.Status.{OK, CREATED, ACCEPTED}

import scala.concurrent.{ExecutionContext, Future}

class ServicioHTTP[A] @Inject() (ws: WSClient)(implicit ec: ExecutionContext)  {

  def get(url: String)(implicit m: Reads[A]): Future[Either[ErrorHTTP, RespuestaHTTP[A]]] = get(url, Map())

  def get(url: String, headers: Map[String, String])(implicit m: Reads[A]): Future[Either[ErrorHTTP, RespuestaHTTP[A]]] = {

    ws.url(url)
      .addHttpHeaders(headers.toSeq:_*).get()
      .map(validarEstado(_).flatMap(obtenerDTO(_)))
  }

  private def validarEstado(respuesta: WSResponse): Either[ErrorHTTP, WSResponse] = respuesta.status match {
        case OK | CREATED | ACCEPTED  => respuesta.asRight
        case _ => ErrorHTTP("Error status", respuesta).asLeft
  }

  private def obtenerDTO(respuesta: WSResponse)(implicit m: Reads[A]): Either[ErrorHTTP, RespuestaHTTP[A]] = {
    respuesta.json.validate[A].fold(
      error => ErrorHTTP("Error Json " + error, respuesta).asLeft,
      dto => RespuestaHTTP(dto, respuesta).asRight
    )
  }

}
