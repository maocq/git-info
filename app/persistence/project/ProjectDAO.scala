package persistence.project

import java.sql.Timestamp
import java.time.{ZoneOffset, ZonedDateTime}

import javax.inject.Inject
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.Future

case class ProjectRecord(
  id: Int, description: String, name: String, nameWithNamespace: String, path: String, pathWithNamespace: String, createdAt: ZonedDateTime, defaultBranch: String,
  sshUrlToRepo: String, httpUrlToRepo: String, webUrl: String
)

class ProjectDAO @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)
  extends HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  def findByID(id: Int): Future[Option[ProjectRecord]] = db.run {
    projectsdb.filter(_.id === id).result.headOption
  }

  def insert(projectRecord: ProjectRecord): Future[ProjectRecord] = db.run {
    (projectsdb returning projectsdb) += projectRecord
  }




  implicit val JavaZonedDateTimeMapper = MappedColumnType.base[ZonedDateTime, Timestamp](
    l => Timestamp.from(l.toInstant),
    t => ZonedDateTime.ofInstant(t.toInstant, ZoneOffset.UTC)
  )

  private class ProjectsRecord(tag: Tag)  extends Table[ProjectRecord](tag, "projects") {
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

    def * = (id, description, name, nameWithNamespace, path, pathWithNamespace, createdAt, defaultBranch, sshUrlToRepo, httpUrlToRepo, webUrl) <> (ProjectRecord.tupled, ProjectRecord.unapply)
  }

  private val projectsdb = TableQuery[ProjectsRecord]

}
