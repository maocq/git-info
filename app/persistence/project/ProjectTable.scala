package persistence.project

import java.sql.Timestamp
import java.time.{ZoneOffset, ZonedDateTime}

object ProjectTable {

  import slick.jdbc.PostgresProfile.api._

  val projectsdb = TableQuery[ProjectsRecord]

  implicit val JavaZonedDateTimeMapper = MappedColumnType.base[ZonedDateTime, Timestamp](
    l => Timestamp.from(l.toInstant),
    t => ZonedDateTime.ofInstant(t.toInstant, ZoneOffset.UTC)
  )

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
    def updating = column[Boolean]("updating")

    def * = (id, description, name, nameWithNamespace, path, pathWithNamespace, createdAt, defaultBranch, sshUrlToRepo, httpUrlToRepo, webUrl, updating) <> (ProjectRecord.tupled, ProjectRecord.unapply)
  }
}
