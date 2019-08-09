package domain.repositories.commit

import domain.model.Commit
import javax.inject.Inject
import monix.eval.Task
import persistence.commit.CommitDAO

class CommitRepository @Inject()(commitDAO: CommitDAO) extends CommitAdapter {

  def insertAll(commits: List[Commit]): Task[List[Commit]] = Task.deferFuture {
    commitDAO insertAll commits.map(transform)
  }.map( _ map transform)

  def getExistingId(commits: List[Commit]): Task[List[Commit]] = Task.deferFuture {
    commitDAO.getExistingId(commits.map(_.id))
  }.map(_.map(transform).toList)

}
