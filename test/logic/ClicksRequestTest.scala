package logic

import org.specs2.mutable.{After, Specification}
import org.specs2.specification.Scope
import play.api.test.Helpers._
import helpers.DBHelper._
import models.{Click, User}
import controllers._
import play.api.test.FakeApplication
import scala.Some

class ClicksRequestTest extends Specification {

  implicit val context = new Scope with After {
    def after = running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
      clearAll
    }
  }

  "Clicks list request logic in Service DAO" should {
    "throw exception if user token incorrect" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {

        ServiceDAO.getClicks("short", GetDataRequest("token", None, None)) must throwA(new Exception("No user with such token! Incident will be reported."))
      }
    }
    "throw exception if link code is not presented" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
        createUser(User(null, "uid", "token"))

        ServiceDAO.getClicks("short", GetDataRequest("token", None, None)) must throwA(new Exception("Code does not exist!"))
      }
    }
    "provide proper click data" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {

        createUser(User(null, "uid", "token"))
        val link = createLink("token", "folder", "awesome.org", "short")

        Click.postClick(link.id.get, "referrer", "127.0.0.1")

        val click = ServiceDAO.getClicks("short", GetDataRequest("token", None, None)).head

        click.referrer mustEqual "referrer"
        click.remoteIp mustEqual "127.0.0.1"
      }
    }
    "provide list of clicks without pagination" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {

        createUser(User(null, "uid", "token"))
        val link = createLink("token", "folder", "awesome.org", "short")

        1 to 10 foreach ( _ => Click.postClick(link.id.get, "referrer", "127.0.0.1"))

        ServiceDAO.getClicks("short", GetDataRequest("token", None, None)).size mustEqual 10
      }
    }
    "provide list of clicks with offset only" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
        createUser(User(null, "uid", "token"))
        val link = createLink("token", "folder", "awesome.org", "short")

        Click.postClick(link.id.get, "referrer", "127.0.0.1")
        Click.postClick(link.id.get, "referrer", "127.0.0.1")
        Click.postClick(link.id.get, "referrer", "127.0.0.1")
        Click.postClick(link.id.get, "referrer", "xxx")

        val clicks = ServiceDAO.getClicks("short", GetDataRequest("token", Some(3), None))

        clicks.size mustEqual 1
        clicks.head.remoteIp mustEqual "xxx"
      }
    }
    "provide list of clicks with limit only" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
        createUser(User(null, "uid", "token"))
        val link = createLink("token", "folder", "awesome.org", "short")

        Click.postClick(link.id.get, "referrer", "xxx")
        Click.postClick(link.id.get, "referrer", "127.0.0.1")
        Click.postClick(link.id.get, "referrer", "127.0.0.1")
        Click.postClick(link.id.get, "referrer", "127.0.0.1")


        val clicks = ServiceDAO.getClicks("short", GetDataRequest("token", None, Some(1)))

        clicks.size mustEqual 1
        clicks.head.remoteIp mustEqual "xxx"
      }
    }
    "provide list of clicks with offset and limit" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {

        createUser(User(null, "uid", "token"))
        val link = createLink("token", "folder", "awesome.org", "short")


        Click.postClick(link.id.get, "referrer", "127.0.0.1")
        Click.postClick(link.id.get, "referrer", "127.0.0.1")
        Click.postClick(link.id.get, "referrer", "xxx")
        Click.postClick(link.id.get, "referrer", "127.0.0.1")


        val clicks = ServiceDAO.getClicks("short", GetDataRequest("token", Some(2), Some(1)))

        clicks.size mustEqual 1
        clicks.head.remoteIp mustEqual "xxx"
      }
    }
  }

}
