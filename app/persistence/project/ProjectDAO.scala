package persistence.project

import java.time.ZonedDateTime

import javax.inject.Inject
import persistence.commit.CommitTable.commitsdb
import persistence.commit.{CommitDAO, CommitRecord}
import persistence.diff.DiffTable.diffsdb
import persistence.diff.{DiffDAO, DiffRecord}
import persistence.issue.IssueTable.issuesdb
import persistence.pr.PRTable.prsdb
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

case class ProjectRecord(
  id: Int, description: String, name: String, nameWithNamespace: String, path: String, pathWithNamespace: String, createdAt: ZonedDateTime, defaultBranch: String,
  sshUrlToRepo: String, httpUrlToRepo: String, webUrl: String, groupId: Int, updating: Boolean = false
)

class ProjectDAO @Inject() (protected val dbConfigProvider: DatabaseConfigProvider, commitDAO: CommitDAO, diffDAO: DiffDAO)(implicit ec: ExecutionContext)
  extends HasDatabaseConfigProvider[JdbcProfile] {

  import ProjectTable._
  import profile.api._

  def findByID(id: Int): Future[Option[ProjectRecord]] = db.run {
    projectsdb.filter(_.id === id).result.headOption
  }

  def getProjectsByGroup(groupId: Int): Future[Seq[ProjectRecord]] = db.run {
    projectsdb.filter(_.groupId === groupId).result
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

  def deleteInfoProject(projectId: Int): Future[Option[ProjectRecord]] = {
    findByID(projectId)
      .flatMap(option  =>
        option.map(p => deleteProject(p.id).map(r => Option(p)))
          .getOrElse(Future.successful(None)))
  }

  private def deleteProject(projectId: Int): Future[Int] = db.run {
    val project = projectsdb.filter(_.id === projectId)
    val commits = commitsdb.filter(_.projectId === projectId)
    val diffs = diffsdb.filter(_.commitId in commits.map(_.id))
    val issues = issuesdb.filter(_.projectId === projectId)
    val prs = prsdb.filter(_.projectId === projectId)

    (prs.delete andThen issues.delete andThen diffs.delete
      andThen commits.delete andThen project.delete).transactionally
  }
}
