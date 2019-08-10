package domain.repositories.diff

import domain.model.Diff
import persistence.diff.DiffRecord

trait DiffAdapter {

  def transform(d: DiffRecord): Diff = {
    Diff(d.id, d.oldPath, d.newPath, d.aMode, d.bMode, d.newFile, d.renamedFile, d.deletedFile, d.diff, d.additions, d.deletions, d.commitId)
  }

  def transform(d: Diff): DiffRecord = {
    DiffRecord(d.id, d.oldPath, d.newPath, d.aMode, d.bMode, d.newFile, d.renamedFile, d.deletedFile, d.diff, d.additions, d.deletions, d.commitId)
  }

}
