package persistence.project

import java.time.{ZonedDateTime}

import javax.inject.Inject
import persistence.commit.{CommitDAO, CommitRecord}
import persistence.diff.{DiffDAO, DiffRecord}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

case class ProjectRecord(
  id: Int, description: String, name: String, nameWithNamespace: String, path: String, pathWithNamespace: String, createdAt: ZonedDateTime, defaultBranch: String,
  sshUrlToRepo: String, httpUrlToRepo: String, webUrl: String, groupId: Int, updating: Boolean = false
)

class ProjectDAO @Inject() (protected val dbConfigProvider: DatabaseConfigProvider, commitDAO: CommitDAO, diffDAO: DiffDAO)(implicit ec: ExecutionContext)
  extends HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._
  import ProjectTable._

  def findByID(id: Int): Future[Option[ProjectRecord]] = db.run {
    projectsdb.filter(_.id === id).result.headOption
  }

  def insert(projectRecord: ProjectRecord): Future[ProjectRecord] = db.run {
    (projectsdb returning projectsdb) += projectRecord
  }

  def insertInfoCommits(commitsRecord: List[CommitRecord], diffsRecord: List[DiffRecord]): Future[(List[CommitRecord], List[DiffRecord])] = db.run {
    (for {
      x <- commitDAO.insertAllDBIO(commitsRecord)
      y <- diffDAO.insertAllDBIO(diffsRecord)
    } yield (x, y)).transactionally
  }

  def onUpdating(id: Int): Future[Int] = db.run {
    (for {
      p <- projectsdb if p.id === id && p.updating === false
    } yield p.updating).update(true)
  }

  def offUpdating(id: Int): Future[Int] = db.run {
    projectsdb.filter(_.id === id).map(_.updating).update(false)
  }


}
