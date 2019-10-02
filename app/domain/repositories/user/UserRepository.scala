package domain.repositories.user

import domain.model.UserGit
import javax.inject.Inject
import monix.eval.Task
import persistence.user.UserDAO

class UserRepository @Inject()(userDAO: UserDAO) extends UserAdapter {

  def insertIfNotExist(userGit: UserGit): Task[UserGit] = Task.deferFuture {
    userDAO insertIfNotExist transform(userGit)
  } map transform

}
