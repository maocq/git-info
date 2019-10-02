package persistence.user

import javax.inject.Inject
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}


case class UserGitRecord(id: Int, name: String, username: String, avatarUrl: String, webUrl: String)

class UserDAO @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext)
  extends HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._
  import UserTable._

  def insert(userRecord: UserGitRecord): Future[UserGitRecord] = db.run {
    (usersdb returning usersdb) += userRecord
  }

  def findByID(userRecord: UserGitRecord): Future[Option[UserGitRecord]] = db.run {
    usersdb.filter(u => u.id === userRecord.id).result.headOption
  }

  def insertIfNotExist(userRecord: UserGitRecord): Future[UserGitRecord] = {
    findByID(userRecord).flatMap(option =>
      option.map(record => Future.successful(record)).getOrElse(insert(userRecord))
    )
  }

  def getRegisteredUsers(users: List[Int]): Future[Seq[UserGitRecord]] = db.run {
    usersdb.filter(_.id.inSet(users)).result
  }

  def insertOrUpdate(userRecord: UserGitRecord): Future[Option[UserGitRecord]] = db.run {
    (usersdb returning usersdb) insertOrUpdate userRecord
  }

}
