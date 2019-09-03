package persistence.user

import javax.inject.Inject
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}


case class UserGitRecord(id: Int, name: String, username: String, avatarUrl: String, webUrl: String)

class UserDAO @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext)
  extends HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  def insert(userRecord: UserGitRecord): Future[UserGitRecord] = db.run {
    (usersdb returning usersdb) += userRecord
  }

  def findByID(userRecord: UserGitRecord): Future[Option[UserGitRecord]] = db.run {
    usersdb.filter(u => u.id === userRecord.id).result.headOption
  }

  def insertIfNotExist(userRecord: UserGitRecord): Future[UserGitRecord] = {
    findByID(userRecord).flatMap(option =>
      option.map(record => insert(record)).getOrElse(Future.successful(userRecord))
    )
  }

  def insertOrUpdate(userRecord: UserGitRecord): Future[Option[UserGitRecord]] = db.run {
    (usersdb returning usersdb) insertOrUpdate userRecord
  }

  private class UsersGitRecord(tag: Tag)  extends Table[UserGitRecord](tag, "users") {
    def id = column[Int]("id", O.PrimaryKey)

    def name = column[String]("name")
    def username = column[String]("username")
    def state = column[String]("state")
    def avatarUrl = column[String]("avatar_url")
    def webUrl = column[String]("web_url")

    def * = (id, name, username, avatarUrl, webUrl) <> (UserGitRecord.tupled, UserGitRecord.unapply)
  }

  private val usersdb = TableQuery[UsersGitRecord]

}
