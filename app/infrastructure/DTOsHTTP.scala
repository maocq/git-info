package infrastructure

//Request
case class ProjectIDDTO(id: Int, groupId: Int)
case class GroupDTO(name: String)

//Response
case class MessageDTO(message: String)
case class InfoUserDTO(user: String, commits: Int, additions: Int, deletions: Int)
