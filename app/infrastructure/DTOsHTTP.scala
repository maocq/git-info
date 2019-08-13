package infrastructure

//Request
case class ProjectIDDTO(id: Int)

//Response
case class InfoUserDTO(user: String, commits: Int, additions: Int, deletions: Int)
