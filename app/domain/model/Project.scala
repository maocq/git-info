package domain.model

import java.time.ZonedDateTime

import cats.data.{NonEmptyList, Validated, ValidatedNel}
import cats.implicits._

case class Group(
  id: Int, name: String, createdAt: ZonedDateTime
) {

  def validar = {
    val validation: ValidatedNel[String, Group] = (
      valdateId(id),
      validateName(name)
    ).mapN((i, n) => Group(i, n, createdAt))
    validation.toEither.leftMap(_.toList)
  }

  def valdateId(id: Int): Validated[NonEmptyList[String], Int] = {
    if (id < 0 )"group.id".invalidNel else id.validNel
  }

  def validateName(name: String): Validated[NonEmptyList[String], String] = {
    if (name.length < 2 )"group.name".invalidNel else name.validNel
  }
}

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
