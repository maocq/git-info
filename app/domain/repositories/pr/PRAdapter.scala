package domain.repositories.pr

import domain.model.PR
import persistence.pr.PRRecord

class PRAdapter {

  def transform(p: PR): PRRecord = {
    PRRecord( p.id, p.iid, p.projectId, p.title, p.description, p.state, p.createdAt, p.updatedAt, p.mergedBy, p.mergedAt,
      p.closedBy, p.closedAt, p.targetBranch, p.sourceBranch, p.userNotesCount, p.upvotes, p.downvotes, p.author)
  }

  def transform(r: PRRecord): PR = {
    PR( r.id, r.iid, r.projectId, r.title, r.description, r.state, r.createdAt, r.updatedAt, r.mergedBy, r.mergedAt,
      r.closedBy, r.closedAt, r.targetBranch, r.sourceBranch, r.userNotesCount, r.upvotes, r.downvotes, r.author)
  }

}
