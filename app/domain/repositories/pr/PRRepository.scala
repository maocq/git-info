package domain.repositories.pr

import java.time.ZonedDateTime

import javax.inject.Inject
import monix.eval.Task
import persistence.pr.PRDAO

class PRRepository @Inject()(prDAO: PRDAO) {

  def getLastDatePRs(projectId: Int): Task[Option[ZonedDateTime]] = Task.deferFuture  {
    prDAO getLastDatePRs projectId
  }
}
