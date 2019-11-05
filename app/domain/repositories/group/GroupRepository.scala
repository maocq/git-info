package domain.repositories.group

import domain.model.GError.DomainError
import domain.model.{GError, Group}
import javax.inject.Inject
import monix.eval.Task
import persistence.group.GroupDAO

class GroupRepository @Inject()(groupDAO: GroupDAO) extends GroupAdapter {

  def findByID(id: Int): Task[Option[Group]] = Task.deferFuture{
    groupDAO findByID id
  }.map(_ map transform)

  def findByIDEither(id: Int): Task[Either[GError, Group]] = {
    findByID(id).map(_.toRight(DomainError("Group not found", "12102")))
  }

  def insert(group: Group): Task[Group] = Task.deferFuture {
    groupDAO insert transform(group)
  } map transform

  def validateNotExistProject(id: Int): Task[Either[GError, Int]] = {
    findByID(id)
      .map(opt => Either.cond(opt.isEmpty, id, DomainError("Group exist", "12202")))
  }

  def update(group: Group): Task[Option[Group]] = Task.deferFuture {
      groupDAO update transform(group)
  }.map(_ map transform)

  def updateE(group: Group): Task[Either[GError, Group]] = {
    update(group).map(_.toRight(DomainError("Group not found", "12102")))
  }

  def deleteGroup(groupId: Int): Task[Option[Group]] = Task.deferFuture {
    groupDAO deleteGroup groupId
  }.map(_ map transform)

  def deleteGroupE(groupId: Int): Task[Either[DomainError, Group]] = {
    deleteGroup(groupId).map(_.toRight(DomainError("Group not found", "12102")))
  }

}
