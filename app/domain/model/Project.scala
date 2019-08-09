package domain.model

import java.time.ZonedDateTime

case class Project (
  id: Int, description: String, name: String, nameWithNamespace: String, path: String, pathWithNamespace: String, createdAt: ZonedDateTime, defaultBranch: String,
  sshUrlToRepo: String, httpUrlToRepo: String, webUrl: String
)
