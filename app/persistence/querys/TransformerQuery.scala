package persistence.querys

import persistence.project.IssueState
import slick.jdbc.GetResult

trait TransformerQuery {

  implicit val GetCommitsUser = GetResult(r => CommitsUser(email  = r.<<, commit = r.<<, additions = r.<<, deletions = r.<<))
  implicit val GetDiffsUser = GetResult(r => DiffsUser(project  = r.<<, commiter = r.<<, additions = r.<<, deletions = r.<<))
  implicit val GetCommitsForUser = GetResult(r => CommitsForUser(project  = r.<<, commiter = r.<<, commits = r.<<))
  implicit val GetFilesWithCommits = GetResult(r => FilesWithCommits(project  = r.<<, path = r.<<, commits = r.<<))


  implicit val GetIssueState = GetResult(r => IssueState(state  = r.<<, date = r.nextTimestamp().toLocalDateTime.toLocalDate, count = r.<<))

}
