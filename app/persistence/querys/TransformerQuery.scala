package persistence.querys

import slick.jdbc.GetResult

trait TransformerQuery {

  implicit val GetDiffsUser = GetResult(r => DiffsUser(project  = r.<<, commiter = r.<<, additions = r.<<, deletions = r.<<))
  implicit val GetCommitsForUser = GetResult(r => CommitsForUser(project  = r.<<, commiter = r.<<, commits = r.<<))

}
