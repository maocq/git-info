package persistence.group

import java.time.ZonedDateTime

import implicits.SlickImplicits

object GroupTable extends SlickImplicits {

  import slick.jdbc.PostgresProfile.api._

  val groupsdb = TableQuery[GroupsRecord]

  class GroupsRecord(tag: Tag)  extends Table[GroupRecord](tag, "group") {
    def id = column[Int]("id", O.PrimaryKey)

    def name = column[String]("name")
    def createdAt = column[ZonedDateTime]("created_at")

    def * = (id, name, createdAt) <> (GroupRecord.tupled, GroupRecord.unapply)
  }
}
