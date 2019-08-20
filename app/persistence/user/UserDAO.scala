package persistence.user

import javax.inject.Inject
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.Future


case class UserRecord(id: Int, name: String, username: String, avatarUrl: String, webUrl: String)

class UserDAO @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)
  extends HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  def insert(userRecord: UserRecord): Future[Option[UserRecord]] = db.run {
    (usersdb returning usersdb) insertOrUpdate userRecord
  }

  private class UsersRecord(tag: Tag)  extends Table[UserRecord](tag, "users") {
    def id = column[Int]("id", O.PrimaryKey)

    def name = column[String]("name")
    def username = column[String]("username")
    def state = column[String]("state")
    def avatarUrl = column[String]("avatar_url")
    def webUrl = column[String]("web_url")

    def * = (id, name, username, avatarUrl, webUrl) <> (UserRecord.tupled, UserRecord.unapply)
  }

  private val usersdb = TableQuery[UsersRecord]

}
