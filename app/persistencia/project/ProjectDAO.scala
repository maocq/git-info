package persistencia.project

import java.sql.Timestamp
import java.time.{ZoneOffset, ZonedDateTime}

import javax.inject.Inject
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.Future

case class ProjectRecord(
  id: Int, description: String, name: String, nameWithNamespace: String, path: String, pathWithNamespace: String, createdAt: ZonedDateTime, defaultBranch: String,
  sshUrlToRepo: String, httpUrlToRepo: String, webUrl: String, readmeUrl: String
)

class ProjectDAO @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)
  extends HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._


  def insertar(projectRecord: ProjectRecord): Future[ProjectRecord] = {
    val insertar = (projectsdb returning projectsdb) += projectRecord
    db.run(insertar)
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
    def readmeUrl = column[String]("readme_url")

    def * = (id, description, name, nameWithNamespace, path, pathWithNamespace, createdAt, defaultBranch, sshUrlToRepo, httpUrlToRepo, webUrl, readmeUrl) <> (ProjectRecord.tupled, ProjectRecord.unapply)
  }

  private val projectsdb = TableQuery[ProjectsRecord]

}