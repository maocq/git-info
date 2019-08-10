package domain.repositories.commit

import java.time.ZonedDateTime

import domain.model.{Commit, Diff}
import domain.repositories.diff.DiffAdapter
import javax.inject.Inject
import monix.eval.Task
import persistence.commit.CommitDAO

class CommitRepository @Inject()(commitDAO: CommitDAO) extends CommitAdapter with DiffAdapter {

  def insertAll(commits: List[Commit]): Task[List[Commit]] = Task.deferFuture {
    commitDAO insertAll commits.map(transform)
  }.map( _ map transform)

  def insertInfoCommits(commits: List[Commit], diffs: List[Diff]): Task[(List[Commit], List[Diff])] = Task.deferFuture {
    commitDAO.insertInfoCommits(commits.map(transform), diffs.map(transform))
  }.map( r => (r._1.map(transform), r._2.map(transform)) )

  def getExistingId(commits: List[Commit]): Task[List[Commit]] = Task.deferFuture {
    commitDAO.getExistingId(commits.map(_.id))
  }.map(_.map(transform).toList)

  def getLastDateCommit(projectId: Int): Task[Option[ZonedDateTime]] = Task.deferFuture  {
    commitDAO getLastDateCommit projectId
  }

}
