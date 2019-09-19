package persistence.querys

import javax.inject.Inject
import persistence.commit.CommitDAO
import persistence.diff.DiffDAO
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

case class CommitsUser(email: String, commit: String, additions: Int, deletions: Int)
case class DiffsUser(project: String, commiter: String, additions: Int, deletions: Int)
case class CommitsForUser(project: String, commiter: String, commits: Int)
case class FilesWithCommits(project: String, path: String, commits: Int)



class ProjectQueryDAO @Inject() (protected val dbConfigProvider: DatabaseConfigProvider, commitDAO: CommitDAO, diffDAO: DiffDAO)(implicit ec: ExecutionContext)
  extends HasDatabaseConfigProvider[JdbcProfile] with  TransformerQuery {

  import profile.api._

  def commitUser(): Future[Vector[CommitsUser]] = db.run {
    sql"""
          SELECT cm.committer_email, cm.id, sum(df.additions) AS additions, sum(df.deletions) AS deletions
            FROM diffs df
            INNER JOIN commits cm ON df.commit_id = cm.id
            INNER JOIN projects pj ON cm.project_id = pj.id
            GROUP BY cm.id, cm.committer_email
            ORDER BY cm.committer_email, cm.id;
       """.as[CommitsUser]
  }

  def diffsUsers(): Future[Vector[DiffsUser]] = db.run {
    sql"""
          SELECT pj.name, cm.committer_email as path, sum(df.additions) AS additions, sum(df.deletions) AS deletions
            FROM diffs df
            INNER JOIN commits cm ON df.commit_id = cm.id
            INNER JOIN projects pj ON cm.project_id = pj.id
            GROUP BY cm.committer_email, pj.name
            ORDER BY additions DESC;
      """.as[DiffsUser]
  }

  def commitsForUser(): Future[Vector[CommitsForUser]] = db.run {
    sql"""
          select pj.name, cm.committer_email, count(*) as num from commits cm
          inner join projects pj on cm.project_id = pj.id
          group by cm.committer_email, pj.name
          order by num desc;
       """.as[CommitsForUser]
  }

  def filesWithCommits(): Future[Vector[FilesWithCommits]] = db.run {
    sql"""
          select pj.name, df.new_path, count(*) as num from diffs df
          inner join commits cm on df.commit_id = cm.id
          inner join projects pj on cm.project_id = pj.id
          group by pj.name, df.new_path
          order by num desc;
      """.as[FilesWithCommits]
  }

  /*
  Actividad commits
  select date(committed_date) as date_commit, count(*) from commits
  group by date_commit order by date_commit;
   */
  
  /*
  Actividad impacto
  select date(committed_date) as date_commit, sum(additions) + sum(deletions) as modifidies from diffs df
inner join commits cm on cm.id = df.commit_id
group by date_commit order by date_commit;
   */

}
