package implicits

import java.sql.{Date, Timestamp}
import java.time.{LocalDate, ZoneOffset, ZonedDateTime}

trait SlickImplicits {

  import slick.jdbc.PostgresProfile.api._

  implicit val JavaZonedDateTimeMapper = MappedColumnType.base[ZonedDateTime, Timestamp](
    l => Timestamp.from(l.toInstant),
    t => ZonedDateTime.ofInstant(t.toInstant, ZoneOffset.UTC)
  )

  implicit val localDateToDate = MappedColumnType.base[LocalDate, Date](
    l => Date.valueOf(l),
    d => d.toLocalDate
  )

  val dateDB = SimpleFunction.unary[ZonedDateTime, Date]("date")

}
