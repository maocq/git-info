package infrastructure

import play.api.libs.ws.WSResponse

case class RespuestaHTTP[A](respuesta: A, respuestaCompleta: WSResponse)
case class ErrorHTTP(error: String, respuestaCompleta: WSResponse)