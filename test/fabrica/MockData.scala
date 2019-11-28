package fabrica

import java.time.ZonedDateTime

import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import persistence.commit.{CommitDAO, CommitRecord}
import persistence.diff.{DiffDAO, DiffRecord}
import persistence.group.{GroupDAO, GroupRecord}

import scala.concurrent.Future

trait MockData extends MockitoSugar with Data {

  val commitDAO = mock[CommitDAO]
  when(commitDAO.insertAll(any[List[CommitRecord]])) thenReturn Future.successful(List(commit))
  when(commitDAO.insert(any[CommitRecord])) thenReturn Future.successful(commit)
  when(commitDAO.getLastDateCommit(any[Int])) thenReturn Future.successful(Option(ZonedDateTime.now()))
  when(commitDAO.getExistingId(any[List[String]])) thenReturn Future.successful(List(commit))

  val diffDAO = mock[DiffDAO]
  when(diffDAO.insertAll(any[List[DiffRecord]])) thenReturn Future.successful(List(diff))
  when(diffDAO.insert(any[DiffRecord])) thenReturn Future.successful(diff)

  val groupDAO = mock[GroupDAO]
  when(groupDAO.findByID(any[Int])) thenReturn Future.successful(Option(group))
  when(groupDAO.insert(any[GroupRecord])) thenReturn Future.successful(group)
  when(groupDAO.deleteGroup(any[Int])) thenReturn Future.successful(Option(group))
  when(groupDAO.update(any[GroupRecord])) thenReturn Future.successful(Option(group))
}
