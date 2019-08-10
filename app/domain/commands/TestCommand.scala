package domain.commands

import cats.implicits._
import domain.model.GError.TechnicalError
import play.api.libs.json.JsValue

import scala.concurrent.Future

class TestCommand extends Command {

  def execute(jsValue: JsValue): Future[Consequence] = {
    Future.successful(Consequence(TechnicalError("=(", "0").asLeft))
  }
}
