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
case class CategoryValueDTO(category: String, value: Int)
case class InfoIssuesDTO(issuesClosed: Seq[CategoryValueDTO], users: Seq[CategoryValueDTO])
case class LinesFile(project: String, file: String, lines: Int)
case class InfoUser(user: String, commits: Int, additions: Int, deletions: Int, total: Int, firstCommit: ZonedDateTime, lastCommit: ZonedDateTime)
case class UpdatingGroup(updating: Boolean)
case class ActivityGroup(hours: Seq[CategoryValueDTO], daysOfWeak: Seq[CategoryValueDTO])
case class RelationPR(from: String, to: String, weight: Int)
case class ProjectWeight(project: String, author: String, number: Int)
case class ProjectWeightAuthors(project: String, number: Int, authors: Seq[DetailWeightAuthor])
case class DetailWeightAuthor(author: String, number: Int, percentage: Double)

class ProjectQueryDAO @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext)
  extends HasDatabaseConfigProvider[JdbcProfile] with TransformerQuery with ProjectAdapter{

  import ProjectTable._
  import profile.api._

  val toChar = SimpleFunction.binary[ZonedDateTime, String, String]("to_char")
  val date_part = SimpleFunction.binary[String, ZonedDateTime, String]("date_part")
  val timezone = SimpleFunction.binary[String, ZonedDateTime, ZonedDateTime]("timezone")

  def getGroups(): Future[Seq[GroupRecord]] = db.run {
    groupsdb.result
  }

  def getImpact(groupId: Int): Future[Seq[CategoryValueDTO]] = db.run {
    (for {
      g <- groupsdb.filter(_.id === groupId)
      p <- projectsdb if g.id === p.groupId
      c <- commitsdb if p.id === c.projectId
    } yield c).groupBy(c => toChar(c.createdAt, "yyyy-mm"))
      .map{ case(mount, commits) => mount -> commits.length }.sortBy(_._1).map(_.mapTo[CategoryValueDTO]).result
  }

  def getInfoIssues(groupId: Int): Future[InfoIssuesDTO] = {
    val issues = getIssuesClosed(groupId)
    val users = getNumberIssuesUsers(groupId)
    for {
      i <- issues
      u <- users
    } yield InfoIssuesDTO(i, u)
  }

  private def getIssuesClosed(groupId: Int): Future[Seq[CategoryValueDTO]] = db.run {
    (for {
      g <- groupsdb.filter(_.id === groupId)
      p <- projectsdb if g.id === p.groupId
      i <- issuesdb if p.id === i.projectId
    } yield i)
      .filter(i => i.closedAt.isDefined)
      .groupBy(i => toChar(i.closedAt.getOrElse(ZonedDateTime.now()), "yyyy-mm-dd"))
      .map{ case(date, issues) => date -> issues.length }
      .sortBy(_._1.desc).map(_.mapTo[CategoryValueDTO]).result
  }

  private def getNumberIssuesUsers(groupId: Int): Future[Seq[CategoryValueDTO]] = db.run {
    (for {
      g <- groupsdb.filter(_.id === groupId)
      p <- projectsdb if g.id === p.groupId
      i <- issuesdb if p.id === i.projectId
      u <- usersdb if i.closedBy === u.id
    } yield u).groupBy(u => u.name)
      .map{ case(name, users) => name -> users.length }.sortBy(_._2).map(_.mapTo[CategoryValueDTO]).result
  }

  def getFilesGroup(groupId: Int): Future[Seq[LinesFile]] = db.run{
    (for {
      g <- groupsdb.filter(_.id === groupId)
      p <- projectsdb if g.id === p.groupId
      c <- commitsdb if p.id === c.projectId
      d <- diffsdb if c.id === d.commitId
    } yield (p, d)).groupBy{ case(project, diff) => (project.name, diff.newPath)}
      .map{ case (group, tupla) => (
        group._1, group._2,
        tupla.map(t => t._2.additions).sum.getOrElse(0) + tupla.map(t => t._2.additions).sum.getOrElse(0)
      )}.sortBy(_._3.desc).map(_.mapTo[LinesFile]).result
  }

  def updatingGroup(groupId: Int): Future[UpdatingGroup] = db.run {
    (for {
      g <- groupsdb.filter(_.id === groupId)
      p <- projectsdb if g.id === p.groupId
    } yield p).result
  }.map(projects => UpdatingGroup(projects.exists(project => project.updating)))

  def getInfoUsers(groupId: Int) = db.run {
    (for {
      g <- groupsdb.filter(_.id === groupId)
      p <- projectsdb if g.id === p.groupId
      c <- commitsdb if p.id === c.projectId
      d <- diffsdb if c.id === d.commitId
    } yield (c, d)).groupBy {case(commit, _) => commit.committerEmail}
      .map{case (commiter, tupla) => (
        commiter,
        tupla.map(w => w._1.id).countDistinct,
        tupla.map(t => t._2.additions).sum.getOrElse(0),
        tupla.map(t => t._2.deletions).sum.getOrElse(0),
        tupla.map(t => t._2.additions).sum.getOrElse(0) - tupla.map(t => t._2.deletions).sum.getOrElse(0),
        tupla.map(t => t._1.createdAt).min.getOrElse(ZonedDateTime.now()),
        tupla.map(t => t._1.createdAt).max.getOrElse(ZonedDateTime.now())
      )}.sortBy(_._5.desc).map(_.mapTo[InfoUser]).result
  }

  def getActivityForDatePart(groupId: Int, datePart: String): Future[Seq[CategoryValueDTO]] = db.run {
    (for {
      g <- groupsdb.filter(_.id === groupId)
      p <- projectsdb if g.id === p.groupId
      c <- commitsdb if p.id === c.projectId
    } yield c).groupBy(c => date_part(datePart, timezone("America/Bogota", c.createdAt)))
      .map{ case(hours, commits) => hours -> commits.length }.sortBy(_._1).map(_.mapTo[CategoryValueDTO]).result
  }

  def getRelationPRs(groupId: Int): Future[Seq[RelationPR]] = db.run {
    (for {
      g <- groupsdb.filter(_.id === groupId)
      p <- projectsdb if g.id === p.groupId
      i <- prsdb if p.id === i.projectId
      a <- usersdb if i.author === a.id
      m <- usersdb if i.mergedBy === m.id
    } yield (a, m)).groupBy{ case(author, mergeBy) => (author.username, mergeBy.username)}
      .map{ case (group, tupla) => (group._1, group._2, tupla.length) }.sortBy(r => (r._1, r._2)).map(_.mapTo[RelationPR]).result
  }

  def getProjectWeight(groupId: Int): Future[Seq[ProjectWeight]] = db.run {
    (for {
      g <- groupsdb.filter(_.id === groupId)
      p <- projectsdb if g.id === p.groupId
      c <- commitsdb if p.id === c.projectId
      d <- diffsdb if c.id === d.commitId
    } yield (p, c, d)).groupBy{ case(pr, cm, _) => (pr.name, cm.committerEmail)}
      .map{ case (group, tupla) => (
        group._1, group._2,
        tupla.map(t => t._3.additions).sum.getOrElse(0) - tupla.map(t => t._3.deletions).sum.getOrElse(0)
      )}.sortBy(r => (r._1, r._3.desc)).map(_.mapTo[ProjectWeight]).result
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
