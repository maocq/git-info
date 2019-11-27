package fabrica

import java.time.ZonedDateTime

import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import persistence.commit.{CommitDAO, CommitRecord}

import scala.concurrent.Future

trait MockData extends MockitoSugar with Data {

  val commitDAO = mock[CommitDAO]
  when(commitDAO.insertAll(any[List[CommitRecord]])) thenReturn Future.successful(List(commit))
  when(commitDAO.insert(any[CommitRecord])) thenReturn Future.successful(commit)
  when(commitDAO.getLastDateCommit(any[Int])) thenReturn Future.successful(Option(ZonedDateTime.now()))
  when(commitDAO.getExistingId(any[List[String]])) thenReturn Future.successful(List(commit))
}
