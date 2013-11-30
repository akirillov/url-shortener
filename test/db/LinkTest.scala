package db

import org.specs2.mutable.{After, Specification}
import org.specs2.specification.Scope
import play.api.test.Helpers._
import scala.Some
import play.api.test.FakeApplication
import helpers.DBHelper._
import play.api.test.FakeApplication
import scala.Some
import models.{Link, User}

class LinkTest extends Specification {

  implicit val context = new Scope with After {
    def after = running(FakeApplication(additionalConfiguration = inMemoryDatabase())) { clearAll }
  }

  "Link Model" should {
    "find exactly one link by its code" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {

        val user = createUser(User(null, "uid", "token"))

        createLink("token", "folder", "awesome.org", "short")

        val link = Link.findByCode("short").get

        link.userId mustEqual user.id.get

        link.code mustEqual "short"
      }
    }
    "increment clicks count" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {

        createUser(User(null, "uid", "token"))

        val link = createLink("token", "folder", "awesome.org", "short")

        Link.incClicksCount(link.id.get)

        val lut = Link.findByCode("short").get

        lut.clicks mustEqual 1
      }
    }
  }


}