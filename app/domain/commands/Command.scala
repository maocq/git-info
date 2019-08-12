package domain.commands

import cats.implicits._
import domain.model.GError
import monix.eval.Task
import play.api.libs.json.JsValue


trait Command[T]  {

  def execute(dto:  T) : Task[Consequence]

  protected def leftConsequence(error: GError): Consequence = Consequence(error.asLeft)
  protected def rightConsequence(json: JsValue): Consequence = Consequence(json.asRight)
}
