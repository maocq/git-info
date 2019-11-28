package persistence.diff

import fabrica.MockData
import org.scalatestplus.play.PlaySpec

import scala.concurrent.Await
import scala.concurrent.duration.Duration

class DiffDAOTest extends PlaySpec with MockData {

  "DiffDAO" should {
    "Insert all" in {
      val future = diffDAO.insertAll(List(diff))
      val result = Await.result(future, Duration.Inf)

      result.size mustBe 1
      result.head.commitId mustBe diff.commitId
    }

    "Insert" in {
      val future = diffDAO.insert(diff)
      val result = Await.result(future, Duration.Inf)

      result.commitId mustBe diff.commitId
      result.diff mustBe diff.diff
      result.newPath mustBe diff.newPath
    }
  }

}
