package persistence.group

import java.time.ZonedDateTime

import javax.inject.Inject
import persistence.commit.CommitTable.commitsdb
import persistence.diff.DiffTable.diffsdb
import persistence.issue.IssueTable.issuesdb
import persistence.pr.PRTable.prsdb
import persistence.project.ProjectTable.projectsdb
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

case class GroupRecord(
  id: Int, name: String, createdAt: ZonedDateTime
)

class GroupDAO @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext)
  extends HasDatabaseConfigProvider[JdbcProfile] {

  import GroupTable._
  import profile.api._

  def insert(groupRecord: GroupRecord): Future[GroupRecord] = db.run {
    (groupsdb returning groupsdb) += groupRecord
  }

  def findByID(id: Int): Future[Option[GroupRecord]] = db.run {
    groupsdb.filter(_.id === id).result.headOption
  }

  def deleteGroup(projectId: Int): Future[Option[GroupRecord]] = {
    findByID(projectId)
      .flatMap(option  =>
        option.map(p => deleteInfoGroup(p.id).map(r => Option(p)))
          .getOrElse(Future.successful(None)))
  }

  private def deleteInfoGroup(groupId: Int): Future[Int] = db.run {
    val group = groupsdb.filter(_.id === groupId)
    val projects = projectsdb.filter(_.groupId === groupId)
    val commits = commitsdb.filter(_.projectId in projects.map(_.id))
    val diffs = diffsdb.filter(_.commitId in commits.map(_.id))
    val issues = issuesdb.filter(_.projectId in projects.map(_.id))
    val prs = prsdb.filter(_.projectId in projects.map(_.id))

    (prs.delete andThen issues.delete andThen diffs.delete
    andThen commits.delete andThen projects.delete andThen group.delete).transactionally
  }

}
