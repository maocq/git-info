package persistence.commit

import fabrica.MockData
import org.scalatestplus.play.PlaySpec

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext}

class CommitDAOTest extends PlaySpec with MockData {
  implicit val ec: ExecutionContext = ExecutionContext.Implicits.global

  "CommitDAO" should {
    "Insert all" in {
      val future = commitDAO.insertAll(List(commit))
      val result = Await.result(future, Duration.Inf)

      result.size mustBe 1
      result.head.title mustBe commit.title
    }

    "Insert" in {
      val future = commitDAO.insert(commit)
      val result = Await.result(future, Duration.Inf)

      result.title mustBe commit.title
      result.projectId mustBe commit.projectId
      result.authorName mustBe commit.authorName
    }

    "Get last date commit" in {
      val future = commitDAO.getLastDateCommit(9)
      val result = Await.result(future, Duration.Inf)

      result.isDefined mustBe true
    }

    "Get Existing Id" in {
      val future = commitDAO.getExistingId(List("2fedc897"))
      val result = Await.result(future, Duration.Inf)

      result.size mustBe 1
      result.head.title mustBe commit.title
    }

  }

}
