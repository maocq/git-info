package infrastructure

import play.api.libs.ws.WSResponse

case class ResponseHTTP[A](response: A, completeResponse: WSResponse)
case class ErrorHTTP(error: String, completeResponse: WSResponse)