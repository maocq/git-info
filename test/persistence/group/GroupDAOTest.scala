package persistence.group

import fabrica.MockData
import org.scalatestplus.play.PlaySpec

import scala.concurrent.Await
import scala.concurrent.duration.Duration

class GroupDAOTest extends PlaySpec with MockData {

  "GrupoDAO" should {
    "findById" in {
      val future = groupDAO.findByID(group.id)
      val result = Await.result(future, Duration.Inf)

      result.isDefined mustBe true
      result.get.name mustBe group.name
    }

    "Insert" in {
      val future = groupDAO.insert(group)
      val result = Await.result(future, Duration.Inf)

      result.id mustBe group.id
      result.name mustBe group.name
    }

    "Delete" in {
      val future = groupDAO.deleteGroup(group.id)
      val result = Await.result(future, Duration.Inf)

      result.isDefined mustBe true
      result.get.id mustBe group.id
      result.get.name mustBe group.name
    }

    "Update" in {
      val future = groupDAO.update(group)
      val result = Await.result(future, Duration.Inf)

      result.isDefined mustBe true
      result.get.id mustBe group.id
      result.get.name mustBe group.name
    }
  }

}
