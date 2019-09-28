package domain.repositories.project

import domain.model.Project
import persistence.project.ProjectRecord

trait ProjectAdapter {

  def transform(r: ProjectRecord): Project = {
      Project(r.id, r.description, r.name, r.nameWithNamespace, r.path, r.pathWithNamespace, r.createdAt, r.defaultBranch,
        r.sshUrlToRepo, r.httpUrlToRepo, r.webUrl, r.groupId)
  }

  def transform(d: Project): ProjectRecord = {
    ProjectRecord(d.id, d.description, d.name, d.nameWithNamespace, d.path, d.pathWithNamespace, d.createdAt, d.defaultBranch,
      d.sshUrlToRepo, d.httpUrlToRepo, d.webUrl, d.groupId)
  }

}
