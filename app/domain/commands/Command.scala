package domain.commands

import play.api.libs.json.JsValue

import scala.concurrent.Future

trait Command  {

  def execute(jsValue:  JsValue) : Future[Consequence]
}
