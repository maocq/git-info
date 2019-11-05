package infrastructure

//Request
case class ProjectIDDTO(id: Int, groupId: Int)
case class GroupIDDTO(id: Int)
case class GroupDTO(name: String)
case class GroupUpdateDTO(id: Int, name: String)

//Response
case class MessageDTO(message: String)
case class InfoUserDTO(user: String, commits: Int, additions: Int, deletions: Int)
