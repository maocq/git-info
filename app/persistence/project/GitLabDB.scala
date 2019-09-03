package persistence.project

import javax.inject.Inject
import persistence.commit.CommitDAO
import persistence.diff.DiffDAO
import persistence.querys.{CommitsUser, TransformerQuery}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.db.NamedDatabase
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

class GitLabDB @Inject() (@NamedDatabase( "gitlab" ) protected val dbConfigProvider: DatabaseConfigProvider, commitDAO: CommitDAO, diffDAO: DiffDAO)(implicit ec: ExecutionContext)
  extends HasDatabaseConfigProvider[JdbcProfile] with  TransformerQuery {

  import profile.api._

  def b(): Future[Vector[String]] = db.run {
    sql"""
          select title from issues;
       """.as[String]
  }

}
