package controllers

import infrastructure.TransformerDTOsHTTP
import javax.inject._
import persistence.project.IssueState
import play.api.mvc._

case class IssuesForStatus(status: String, infoIssue: List[IssueState])

@Singleton
class HomeController @Inject()(
  cc: ControllerComponents
  //gitLab: GitLabDB,
  //projectQueryDAO: ProjectQueryDAO,
) extends AbstractController(cc) with TransformerDTOsHTTP {

  //implicit lazy val executor: Scheduler = monix.execution.Scheduler.Implicits.global

  def index() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.index())
  }

  /*
  def userIssuesClosed() = Action.async {
    val end = LocalDate.now()
    val start = end.minusDays(90)
    gitLab.userIssuesClosed(start, end)
      .map(d => Ok(Json.toJson(d)))
  }

  def issues() = Action.async {
    val end = LocalDate.now()
    val start = end.minusDays(90)

    gitLab.issuesState(start, end)
      .map{issues =>
        val groupIssues = issues.toList.groupBy(_.state).map{case (k, v) => IssuesForStatus(k, v)}.toList
        validateAndFillDates(groupIssues, issues.headOption, issues.reverse.headOption)
      }.map(d => Ok(Json.toJson(d)))
  }

  private def validateAndFillDates(issues: List[IssuesForStatus], startIssue: Option[IssueState], endIssue: Option[IssueState]): List[IssuesForStatus] = {
    (for {
      s <- startIssue
      e <- endIssue
    } yield (s.date, e.date))
      .map(date => issues.map(fillDates(_, date._1, date._2)))
      .getOrElse(issues)
  }

  private def fillDates(issue: IssuesForStatus, start: LocalDate, end: LocalDate): IssuesForStatus = {
    val issues = (0 until ChronoUnit.DAYS.between(start, end).toInt).map{ i =>
      val date = start.plusDays(i)
      issue.infoIssue.find(_.date == date).getOrElse(IssueState(issue.status, date, 0))
    }.toList

    issue.copy(infoIssue = issues)
  }

  def traverseFold[L, R, T](elements: List[T])(f: T => Future[Either[L, R]]): Future[Either[L, List[R]]] = {
    elements.foldLeft( EitherT(Future.successful(List.empty[R].asRight[L])) ) {
      (acc, nxt) => acc.flatMap(list => EitherT(f(nxt)).map(list :+ _))
    }.value
  }

  def traverseFoldF[A, T](elements: List[T])(f: T => Future[A]): Future[List[A]] = {
    elements.foldLeft(Future.successful(List.empty[A])) { case (acc, nxt) => acc.flatMap(list => f(nxt).map(list :+ _)) }
  }

  def sequenceEither[L, R](eithers: List[Either[L, R]]): Either[L, List[R]] = {
    eithers.foldLeft(List.empty[R].asRight[L])( (acc, nxt) => acc.flatMap(list => nxt.map(list :+ _)) )
  }
   */
}
