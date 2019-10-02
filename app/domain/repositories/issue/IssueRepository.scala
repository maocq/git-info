package domain.repositories.issue

import java.time.ZonedDateTime

import domain.model.Issue
import javax.inject.Inject
import monix.eval.Task
import persistence.issue.IssueDAO


class IssueRepository @Inject()(issueDAO: IssueDAO) extends IssueAdapter {

  def insertOrUpdateAll(issues: List[Issue]): Task[List[Issue]] = Task.deferFuture {
    issueDAO insertOrUpdateAll issues.map(transform)
  }.map( _ map transform)

  def getLastDateIssues(projectId: Int): Task[Option[ZonedDateTime]] = Task.deferFuture  {
    issueDAO getLastDateIssues projectId
  }

}
