package persistence.project

import java.time.LocalDate

import javax.inject.Inject
import persistence.commit.CommitDAO
import persistence.diff.DiffDAO
import persistence.querys.TransformerQuery
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.db.NamedDatabase
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

case class IssueState(state: String, date: LocalDate, count: Int)
case class UserIssuesClosed(user: String, count: Int)

class GitLabDB @Inject() (@NamedDatabase( "gitlab" ) protected val dbConfigProvider: DatabaseConfigProvider, commitDAO: CommitDAO, diffDAO: DiffDAO)(implicit ec: ExecutionContext)
  extends HasDatabaseConfigProvider[JdbcProfile] with  TransformerQuery {

  import profile.api._

  def issuesState(start: LocalDate, end: LocalDate): Future[Vector[IssueState]] = db.run {
  sql"""
      select
      state,
      case when closed_at is null then DATE(created_at) else DATE(closed_at) end date_issue,
      count(*)
      from issues
      group by state, date_issue
      having case when closed_at is null then DATE(created_at) else DATE(closed_at) end between '#$start' and '#$end'
      order by date_issue;
  """.as[IssueState]
  }

  def userIssuesClosed(start: LocalDate, end: LocalDate): Future[Vector[UserIssuesClosed]] = db.run {
    sql"""
      select usr.email, count(*) from issues iss
      inner join users usr on iss.author_id = usr.id
      where iss.closed_at between '#$start' and '#$end'
      group by usr.email
      order by count desc;
      """.as[UserIssuesClosed]
  }

}
