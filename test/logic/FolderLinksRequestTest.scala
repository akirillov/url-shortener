package logic

import org.specs2.mutable.{After, Specification}
import org.specs2.specification.Scope
import play.api.test.Helpers._
import play.api.test.FakeApplication
import helpers.DBHelper._
import play.api.Play.current
import models.{Link, User}
import controllers._
import play.api.db.DB
import anorm._
import play.api.test.FakeApplication
import scala.Some
import anorm.SqlParser._
import anorm.~
import play.api.test.FakeApplication
import scala.Some
import controllers.PostLinkRequest
import play.api.test.FakeApplication
import scala.Some
import controllers.TokenRequest

class FolderLinksRequestTest extends Specification {

  implicit val context = new Scope with After {
    def after = running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
      clearAll
    }
  }

  "Folder links request logic in Service DAO" should {
    "throw exception if user token incorrect" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {

        ServiceDAO.getFolders("token") must throwA(new Exception("No user with such token! Incident will be reported."))
      }
    }
    "provide proper folder data" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {

        createUser(User(null, "uid", "token"))
        createFolder("token", "text_id", "title")

        val folders = ServiceDAO.getFolders("token")

        folders.size mustEqual 1
        folders.head.id mustEqual "text_id"
        folders.head.title mustEqual "title"
      }
    }
    "provide proper amount of folders for user" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {

        createUser(User(null, "uid", "token"))

        (1 to 10).foreach(x => createFolder("token", "id"+x, "title" + x))

        ServiceDAO.getFolders("token").size mustEqual 10
      }
    }
  }
}
