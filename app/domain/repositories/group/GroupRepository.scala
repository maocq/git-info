package domain.repositories.group

import domain.model.GError.DomainError
import domain.model.{GError, Group, Project}
import javax.inject.Inject
import monix.eval.Task
import persistence.group.GroupDAO

class GroupRepository @Inject()(groupDAO: GroupDAO) extends GroupAdapter {

  def findByID(id: Int): Task[Option[Group]] = Task.deferFuture{
    groupDAO findByID id
  }.map(_ map transform)

  def insert(group: Group): Task[Group] = Task.deferFuture {
    groupDAO insert transform(group)
  } map transform

  def validateNotExistProject(id: Int): Task[Either[GError, Int]] = {
    findByID(id)
      .map(opt => Either.cond(opt.isEmpty, id, DomainError("Group exist", "12202")))
  }

}
