package db

import org.specs2.mutable.{Before, After, Specification}
import org.specs2.specification.Scope
import play.api.test.Helpers._
import play.api.test.FakeApplication
import helpers.DBHelper._
import play.api.test.FakeApplication
import models.{Click, Folder, User}

class ClickTest extends Specification {

  implicit val context = new Scope with After with Before{
    def before = running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
      clearAll
    }

    def after = running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
      clearAll
    }
  }

  "Click Model" should {
    "store clicks in the DB" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
        createUser(User(null, "uid", "token"))
        createFolder("token", "/", "root")
        val link = createLink("token", "/", "awesome.org", "short_code")

        val click = Click.postClick(link.id.get, "referrer", "127.0.0.1")

        click.id mustNotEqual 0
      }
    }

  }
}