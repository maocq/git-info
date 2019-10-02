package domain.repositories.user

import domain.model.UserGit
import javax.inject.Inject
import monix.eval.Task
import persistence.user.UserDAO

class UserRepository @Inject()(userDAO: UserDAO) extends UserAdapter {

  def insert(userGit: UserGit): Task[UserGit] = Task.deferFuture {
    userDAO insert transform(userGit)
  } map transform

  def insertAll(usersGit: List[UserGit]): Task[List[UserGit]] = Task.deferFuture {
    userDAO insertAll usersGit.map(transform)
  }.map( _ map transform)

  def insertIfNotExist(userGit: UserGit): Task[UserGit] = Task.deferFuture {
    userDAO insertIfNotExist transform(userGit)
  } map transform

  def getRegisteredUsers(users: List[Int]): Task[List[UserGit]] = Task.deferFuture {
    userDAO getRegisteredUsers users
  }.map(_.map(transform).toList)

}
