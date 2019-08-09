package domain.repositories.commit

import domain.model.Commit
import persistence.commit.CommitRecord

trait CommitAdapter {

  def transform(c: CommitRecord): Commit = {
    Commit(c.id, c.shortId, c.createdAt, c.parentIds, c.title, c.message, c.authorName,
      c.authorEmail, c.authoredDate, c.committerName, c.committerEmail, c.committedDate, c.projectId)
  }

  def transform(d: Commit): CommitRecord = {
    CommitRecord(d.id, d.shortId, d.createdAt, d.parentIds, d.title, d.message, d.authorName,
      d.authorEmail, d.authoredDate, d.committerName, d.committerEmail, d.committedDate, d.projectId)
  }

}
