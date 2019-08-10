package domain.model

sealed trait GError extends Product with Serializable {
  val message: String
  val errrorCode: String
  val error: Option[Throwable]
}

object GError {

  case class DomainError(message: String, errrorCode:String, error: Option[Throwable] = None) extends GError
  case class ValidationError(message: String, errrorCode:String, filds: Seq[String], error: Option[Throwable] = None) extends GError
  case class TechnicalError(message: String, errrorCode:String, error: Option[Throwable] = None) extends GError

}
