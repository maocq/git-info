package persistence.project

import fabrica.MockData
import org.scalatestplus.play.PlaySpec

import scala.concurrent.Await
import scala.concurrent.duration.Duration

class ProjectDAOTest extends PlaySpec with MockData {

  "GrupoDAO" should {
    "findById" in {
      val future = projectDAO.findByID(project.id)
      val result = Await.result(future, Duration.Inf)

      result.isDefined mustBe true
      result.get.name mustBe project.name
    }

    "Insert" in {
      val future = projectDAO.insert(project)
      val result = Await.result(future, Duration.Inf)

      result.id mustBe project.id
      result.name mustBe project.name
      result.groupId mustBe project.groupId
    }

    "Get Projects" in {
      val future = projectDAO.getProjectsByGroup(project.groupId)
      val result = Await.result(future, Duration.Inf)

      result.size mustBe 1
      result.head.name mustBe project.name
      result.head.groupId mustBe project.groupId
    }

    "Insert info commits" in {
      val future = projectDAO.insertInfoCommits(List(commit), List(diff))
      val result = Await.result(future, Duration.Inf)

      result._1.size mustBe 1
      result._2.size mustBe 1
      result._1.head.title mustBe commit.title
    }

    "Delete" in {
      val future = projectDAO.deleteInfoProject(project.id)
      val result = Await.result(future, Duration.Inf)

      result.isDefined mustBe true
      result.get.name mustBe project.name
    }

  }
}
