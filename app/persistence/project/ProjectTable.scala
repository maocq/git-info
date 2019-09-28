package persistence.project

import java.time.ZonedDateTime

import implicits.SlickImplicits

object ProjectTable extends SlickImplicits {

  import slick.jdbc.PostgresProfile.api._

  val projectsdb = TableQuery[ProjectsRecord]

  class ProjectsRecord(tag: Tag)  extends Table[ProjectRecord](tag, "projects") {
    def id = column[Int]("id", O.PrimaryKey)

    def description = column[String]("description")
    def name = column[String]("name")
    def nameWithNamespace = column[String]("name_with_namespace")
    def path = column[String]("path")
    def pathWithNamespace = column[String]("path_with_namespace")
    def createdAt = column[ZonedDateTime]("created_at")
    def defaultBranch = column[String]("default_branch")
    def sshUrlToRepo = column[String]("ssh_url_to_repo")
    def httpUrlToRepo = column[String]("http_url_to_repo")
    def webUrl = column[String]("web_url")
    def groupId = column[Int]("group_id")
    def updating = column[Boolean]("updating")

    def * = (id, description, name, nameWithNamespace, path, pathWithNamespace, createdAt, defaultBranch, sshUrlToRepo, httpUrlToRepo, webUrl, groupId, updating) <> (ProjectRecord.tupled, ProjectRecord.unapply)
  }
}
