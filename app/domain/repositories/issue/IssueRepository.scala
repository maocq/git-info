package domain.repositories.issue

import java.time.ZonedDateTime

import domain.model.Issue
import javax.inject.Inject
import monix.eval.Task
import persistence.issue.IssueDAO


class IssueRepository @Inject()(issueDAO: IssueDAO) extends IssueAdapter {

  def insertAll(issues: List[Issue]): Task[List[Issue]] = Task.deferFuture {
    issueDAO insertAll issues.map(transform)
  }.map( _ map transform)

  def getLastDateIssues(projectId: Int): Task[Option[ZonedDateTime]] = Task.deferFuture  {
    issueDAO getLastDateIssues projectId
  }

}
