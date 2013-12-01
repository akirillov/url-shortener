package db

import org.specs2.mutable.{Before, After, Specification}
import org.specs2.specification.Scope
import play.api.test.Helpers._
import play.api.test.FakeApplication
import play.api.Play.current
import helpers.DBHelper._
import play.api.test.FakeApplication
import models.{Click, Folder, User}
import play.api.db.DB
import anorm._
import play.api.test.FakeApplication

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

        val dbClick = DB.withConnection {
          implicit connection =>
            SQL("select * from click where id = {id}").on("id" -> click.id.get).using(Click.parser).list().head
        }

        dbClick.linkId mustEqual link.id.get
        dbClick.referrer mustEqual "referrer"
        dbClick.remoteIP mustEqual "127.0.0.1"

      }
    }

  }
}