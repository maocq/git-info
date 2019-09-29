package persistence.group

import java.time.ZonedDateTime

import implicits.SlickImplicits

object GroupTable extends SlickImplicits {

  import slick.jdbc.PostgresProfile.api._

  val groupsdb = TableQuery[GroupsRecord]

  class GroupsRecord(tag: Tag)  extends Table[GroupRecord](tag, "groups") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)

    def name = column[String]("name")
    def createdAt = column[ZonedDateTime]("created_at")

    def * = (id, name, createdAt) <> (GroupRecord.tupled, GroupRecord.unapply)
  }
}
