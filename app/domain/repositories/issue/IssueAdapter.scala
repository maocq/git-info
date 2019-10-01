package domain.repositories.issue

import domain.model.Issue
import persistence.issue.IssueRecord

trait IssueAdapter {

  def transform(i: Issue): IssueRecord = {
    IssueRecord(i.id, i.iid, i.projectId, i.title, i.description, i.state, i.createdAt, i.updatedAt,
      i.closedAt, i.closedBy, i.author, i.assignee, i.webUrl)
  }

  def transform(r: IssueRecord): Issue = {
    Issue(r.id, r.iid, r.projectId, r.title, r.description, r.state, r.createdAt, r.updatedAt,
      r.closedAt, r.closedBy, r.author, r.assignee, r.webUrl)
  }

}
