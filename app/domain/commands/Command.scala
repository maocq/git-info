package domain.commands

import cats.implicits._
import domain.model.GError
import monix.eval.Task
import play.api.libs.json.JsValue


trait Command  {

  def execute(jsValue:  JsValue) : Task[Consequence]

  protected def leftConsequence(error: GError): Consequence = Consequence(error.asLeft)
  protected def rightConsequence(json: JsValue): Consequence = Consequence(json.asRight)
}
