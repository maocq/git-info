package persistence.querys

import javax.inject.Inject
import persistence.commit.CommitDAO
import persistence.diff.DiffDAO
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

case class DiffsUser(project: String, commiter: String, additions: Int, deletions: Int)
case class CommitsForUser(project: String, commiter: String, commits: Int)


class ProjectQueryDAO @Inject() (protected val dbConfigProvider: DatabaseConfigProvider, commitDAO: CommitDAO, diffDAO: DiffDAO)(implicit ec: ExecutionContext)
  extends HasDatabaseConfigProvider[JdbcProfile] with  TransformerQuery {

  import profile.api._

  def diffsUsers(): Future[Vector[DiffsUser]] = db.run {
    sql"""
          SELECT pj.name, cm.committer_email, sum(df.additions) AS additions, sum(df.deletions) AS deletions
            FROM diffs df
            INNER JOIN commits cm ON df.commit_id = cm.id
            INNER JOIN projects pj ON cm.project_id = pj.id
            GROUP BY cm.committer_email, pj.name
            ORDER BY additions DESC;
      """.as[DiffsUser]
  }

  def commitsForUser() = db.run {
    sql"""
          select pj.name, cm.committer_email, count(*) as num from commits cm
          inner join projects pj on cm.project_id = pj.id
          group by cm.committer_email, pj.name
          order by num desc;
       """.as[CommitsForUser]
  }

}
