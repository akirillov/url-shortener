package logic

import org.specs2.mutable.{After, Specification}
import org.specs2.specification.Scope
import play.api.test.Helpers._
import helpers.DBHelper._
import models.User
import controllers._
import play.api.test.FakeApplication
import scala.Some

class FoldersListRequestTest extends Specification {

  implicit val context = new Scope with After {
    def after = running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
      clearAll
    }
  }

  "Folders list request logic in Service DAO" should {
    "throw exception if user token incorrect" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {

        ServiceDAO.getFolderLinks("folder", GetDataRequest("token", None, None)) must throwA(new Exception("No user with such token! Incident will be reported."))
      }
    }
    "throw exception if folder id is not presented" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
        createUser(User(null, "uid", "token"))

        ServiceDAO.getFolderLinks("folder", GetDataRequest("token", None, None)) must throwA(new Exception("No folder with such ID!"))
      }
    }
    "provide proper link data" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {

        createUser(User(null, "uid", "token"))
        createLink("token", "folder", "awesome.org", "short")

        val link = ServiceDAO.getFolderLinks("folder", GetDataRequest("token", None, None)).head

        link.url mustEqual "awesome.org"
        link.code mustEqual "short"
      }
    }
    "provide list of links without pagination" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {

        createUser(User(null, "uid", "token"))
        createLink("token", "folder", "awesome.org", "short")
        createLink("token", "folder", "awesome.org", "short1")
        createLink("token", "folder", "awesome.org", "short2")
        createLink("token", "folder", "awesome.org", "short3")

        ServiceDAO.getFolderLinks("folder", GetDataRequest("token", None, None)).size mustEqual 4
      }
    }
    "provide list of links with offset only" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
        createUser(User(null, "uid", "token"))
        createLink("token", "folder", "awesome.org", "short")
        createLink("token", "folder", "awesome.org", "short1")
        createLink("token", "folder", "awesome.org", "short2")
        createLink("token", "folder", "awesome.org", "xxx")

        val links = ServiceDAO.getFolderLinks("folder", GetDataRequest("token", Some(3), None))

        links.size mustEqual 1
        links.head.code mustEqual "xxx"
      }
    }
    "provide list of links with limit only" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
        createUser(User(null, "uid", "token"))
        createLink("token", "folder", "awesome.org", "xxx")
        createLink("token", "folder", "awesome.org", "short1")
        createLink("token", "folder", "awesome.org", "short2")
        createLink("token", "folder", "awesome.org", "short3")

        val links = ServiceDAO.getFolderLinks("folder", GetDataRequest("token", None, Some(1)))

        links.size mustEqual 1
        links.head.code mustEqual "xxx"

      }
    }
    "provide list of links with offset and limit" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
        createUser(User(null, "uid", "token"))
        createLink("token", "folder", "awesome.org", "short")
        createLink("token", "folder", "awesome.org", "short1")
        createLink("token", "folder", "awesome.org", "xxx")
        createLink("token", "folder", "awesome.org", "short3")

        val links = ServiceDAO.getFolderLinks("folder", GetDataRequest("token", Some(2), Some(1)))

        links.size mustEqual 1
        links.head.code mustEqual "xxx"

      }
    }
  }

}
