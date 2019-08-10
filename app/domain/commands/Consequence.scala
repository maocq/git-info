package domain.commands

import domain.model.GError
import play.api.libs.json.JsValue

case class Consequence(response: Either[GError, JsValue], events: List[Event] = Nil)
