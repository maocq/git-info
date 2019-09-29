package domain.repositories.group

import domain.model.Group
import persistence.group.GroupRecord

trait GroupAdapter {

  def transform(r: GroupRecord) = Group(r.id, r.name, r.createdAt)
  def transform(r: Group) = GroupRecord(r.id, r.name, r.createdAt)
}
