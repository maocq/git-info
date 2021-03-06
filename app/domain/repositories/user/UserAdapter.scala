package domain.repositories.user

import domain.model.UserGit
import persistence.user.UserGitRecord

trait UserAdapter {

  def transform(u: UserGit): UserGitRecord = {
    UserGitRecord(u.id, u.name, u.username, u.email, u.avatarUrl, u.webUrl)
  }

  def transform(r: UserGitRecord): UserGit = {
    UserGit(r.id, r.name, r.username, r.email, r.avatarUrl, r.webUrl)
  }

}
