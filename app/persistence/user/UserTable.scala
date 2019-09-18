package persistence.user

object UserTable {

  import slick.jdbc.PostgresProfile.api._

  val usersdb = TableQuery[UsersGitRecord]

  class UsersGitRecord(tag: Tag)  extends Table[UserGitRecord](tag, "users") {
    def id = column[Int]("id", O.PrimaryKey)

    def name = column[String]("name")
    def username = column[String]("username")
    def state = column[String]("state")
    def avatarUrl = column[String]("avatar_url")
    def webUrl = column[String]("web_url")

    def * = (id, name, username, avatarUrl, webUrl) <> (UserGitRecord.tupled, UserGitRecord.unapply)
  }
}
