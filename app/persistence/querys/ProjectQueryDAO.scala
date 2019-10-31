package persistence.querys

import java.time.ZonedDateTime

import domain.model.Project
import domain.repositories.project.ProjectAdapter
import javax.inject.Inject
import persistence.commit.CommitTable.commitsdb
import persistence.diff.DiffTable.diffsdb
import persistence.user.UserTable.usersdb
import persistence.group.GroupRecord
import persistence.group.GroupTable.groupsdb
import persistence.issue.IssueTable.issuesdb
import persistence.pr.PRTable.prsdb
import persistence.project.ProjectTable
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

//Borrar
case class CommitsUser(email: String, commit: String, additions: Int, deletions: Int)
case class DiffsUser(project: String, commiter: String, additions: Int, deletions: Int)
case class CommitsForUser(project: String, commiter: String, commits: Int)
case class FilesWithCommits(project: String, path: String, commits: Int)
//Borrar end

case class NumbersGroupDTO(numberCommits: Int, numberAuthors: Int, numberIssues: Int, numberPrs: Int)
case class NumberFileDTO(name: String, weight: Int)
case class LinesGroupDTO(additions: Int, deletions: Int, total: Int)
case class InfoGroupDTO(
  projects: Seq[Project], firstCommit: ZonedDateTime, lastCommit: ZonedDateTime,
  numbers: NumbersGroupDTO, lines: LinesGroupDTO, files: List[NumberFileDTO]
)
case class ImpactGroupDTO(mounth: String, count: Int)
case class CategoryValueDTO(category: String, value: Int)

class ProjectQueryDAO @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext)
  extends HasDatabaseConfigProvider[JdbcProfile] with TransformerQuery with ProjectAdapter{

  import ProjectTable._
  import profile.api._

  val toChar = SimpleFunction.binary[ZonedDateTime, String, String]("to_char")

  def getGroups(): Future[Seq[GroupRecord]] = db.run {
    groupsdb.result
  }

  def getImpact(groupId: Int): Future[Seq[ImpactGroupDTO]] = db.run {
    (for {
      g <- groupsdb.filter(_.id === groupId)
      p <- projectsdb if g.id === p.groupId
      c <- commitsdb if p.id === c.projectId
    } yield c).groupBy(c => toChar(c.createdAt, "Mon yyyy"))
      .map{ case(mount, commits) => mount -> commits.length }.sortBy(_._1).map(_.mapTo[ImpactGroupDTO]).result
  }

  def getIssuesClosed(groupId: Int): Future[Seq[CategoryValueDTO]] = db.run {
    (for {
      g <- groupsdb.filter(_.id === groupId)
      p <- projectsdb if g.id === p.groupId
      i <- issuesdb if p.id === i.projectId
    } yield i)
      .filter(i => i.closedAt.isDefined)
      .groupBy(i => toChar(i.closedAt.getOrElse(ZonedDateTime.now()), "dd Mon yyyy"))
      .map{ case(date, issues) => date -> issues.length }
      .sortBy(_._1).map(_.mapTo[CategoryValueDTO]).result
  }

  def getNumberIssuesUsers(groupId: Int): Future[Seq[CategoryValueDTO]] = db.run {
    (for {
      g <- groupsdb.filter(_.id === groupId)
      p <- projectsdb if g.id === p.groupId
      i <- issuesdb if p.id === i.projectId
      u <- usersdb if i.closedBy === u.id
    } yield u).groupBy(u => u.name)
      .map{ case(name, users) => name -> users.length }.sortBy(_._2).map(_.mapTo[CategoryValueDTO]).result
  }

  def getAllInfoProject(groupId: Int): Future[InfoGroupDTO] = {
    val projects = getProjectsPerGroup(groupId)
    val dates = getDatesGroup(groupId)
    val commits = getNumberCommits(groupId)
    val authors = getNumberAuthors(groupId)
    val issues = getNumberIssues(groupId)
    val prs = getNumberPRs(groupId)
    val lines = getLines(groupId)
    val files = getFiles(groupId).map(res => res.map(getExtension).groupBy(identity).mapValues(_.size))

    for {
      p <- projects
      d <- dates
      c <- commits
      a <- authors
      i <- issues
      r <- prs
      l <- lines
      f <- files
    } yield
      InfoGroupDTO(p, d.map(_._2).getOrElse(ZonedDateTime.now()), d.map(_._1).getOrElse(ZonedDateTime.now()), NumbersGroupDTO(c, a, i, r),
      LinesGroupDTO(l._1, l._2, l._1 - l._2), f.map{case (f, n) => NumberFileDTO(f, n)}.toList)
  }

  private def getProjectsPerGroup(groupId: Int): Future[Seq[Project]] = db.run {
    (for {
      g <- groupsdb.filter(_.id === groupId)
      p <- projectsdb if g.id === p.groupId
    } yield p).result
  }.map(_ map transform)

  private def getDatesGroup(groupId: Int): Future[Option[(ZonedDateTime, ZonedDateTime)]] = db.run {
    (for {
      g <- groupsdb.filter(_.id === groupId)
      p <- projectsdb if g.id === p.groupId
      c <- commitsdb if p.id === c.projectId
    } yield c).groupBy(_ => true)
      .map{ case (_, group) => (group.map(_.committedDate).max.get, group.map(_.committedDate).min.get)}
      .result.headOption
  }

  private def getNumberCommits(groupId: Int): Future[Int] = db.run {
    (for {
      g <- groupsdb.filter(_.id === groupId)
      p <- projectsdb if g.id === p.groupId
      c <- commitsdb if p.id === c.projectId
    } yield c).length.result
  }

  private def getNumberIssues(groupId: Int): Future[Int] = db.run {
    (for {
      g <- groupsdb.filter(_.id === groupId)
      p <- projectsdb if g.id === p.groupId
      i <- issuesdb if p.id === i.projectId
    } yield i).length.result
  }

  private def getNumberPRs(groupId: Int): Future[Int] = db.run {
    (for {
      g <- groupsdb.filter(_.id === groupId)
      p <- projectsdb if g.id === p.groupId
      i <- prsdb if p.id === i.projectId
    } yield i).length.result
  }

  private def getNumberAuthors(groupId: Int): Future[Int] = db.run {
    (for {
      g <- groupsdb.filter(_.id === groupId)
      p <- projectsdb if g.id === p.groupId
      c <- commitsdb if p.id === c.projectId
    } yield c.committerEmail).distinct.length.result
  }

  private def getFiles(groupId: Int): Future[Seq[String]] = db.run {
    (for {
      g <- groupsdb.filter(_.id === groupId)
      p <- projectsdb if g.id === p.groupId
      c <- commitsdb if p.id === c.projectId
      d <- diffsdb if c.id === d.commitId
    } yield d.newPath).distinct.result
  }

  private def getLines(groupId: Int): Future[(Int, Int)] = db.run {
    (for {
      g <- groupsdb.filter(_.id === groupId)
      p <- projectsdb if g.id === p.groupId
      c <- commitsdb if p.id === c.projectId
      d <- diffsdb if c.id === d.commitId
    } yield d)
      .groupBy(_ => true)
      .map{ case (_, group) => (group.map(_.additions).sum.getOrElse(0), group.map(_.deletions).sum.getOrElse(0))}.result
  }.map(_.headOption.getOrElse((0,0)))

  private def getExtension(file: String): String = if(file.contains(".")) file.substring(file.lastIndexOf(".") + 1) else "plain"



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
