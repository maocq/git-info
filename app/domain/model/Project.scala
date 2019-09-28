package domain.model

import java.time.ZonedDateTime

case class Project (
  id: Int, description: String, name: String, nameWithNamespace: String, path: String, pathWithNamespace: String, createdAt: ZonedDateTime, defaultBranch: String,
  sshUrlToRepo: String, httpUrlToRepo: String, webUrl: String, groupId: Int
)

case class Commit(
  id: String, shortId: String, createdAt: ZonedDateTime, parentIds: String, title: String, message: String, authorName: String,
  authorEmail: String, authoredDate: ZonedDateTime, committerName: String, committerEmail: String, committedDate: ZonedDateTime, projectId: Int
)

case class Diff(
  id: Int, oldPath: String, newPath: String, aMode: String, bMode: String, newFile: Boolean,
  renamedFile: Boolean, deletedFile: Boolean, diff: String, additions: Int, deletions: Int, commitId: String
)
