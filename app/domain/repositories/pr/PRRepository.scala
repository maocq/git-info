package domain.repositories.pr

import java.time.ZonedDateTime

import domain.model.PR
import javax.inject.Inject
import monix.eval.Task
import persistence.pr.PRDAO

class PRRepository @Inject()(prDAO: PRDAO) extends PRAdapter {

  def getLastDatePRs(projectId: Int): Task[Option[ZonedDateTime]] = Task.deferFuture  {
    prDAO getLastDatePRs projectId
  }

  def insertOrUpdateAll(prs: List[PR]): Task[List[PR]] = Task.deferFuture {
    prDAO insertOrUpdateAll prs.map(transform)
  }.map( _ map transform)
}
