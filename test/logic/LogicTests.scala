package logic

import org.specs2.mutable.{After, Specification}
import org.specs2.specification.Scope
import play.api.test.Helpers._
import play.api.test.FakeApplication
import helpers.DBHelper._
import play.api.Play.current
import models.{Folder, Click, Link, User}
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

class LogicTests extends Specification {

  implicit val context = new Scope with After {
    def after = running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
      clearAll
    }
  }

  "User logic in Service DAO" should {
    "return token if user exists in DB" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {

        createUser(User(null, "uid", "token"))

        ServiceDAO.getToken(TokenRequest("uid", "secret")) mustEqual "token"
      }
    }

    "create token if user does not exist in DB" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {

        val token = ServiceDAO.getToken(TokenRequest("uid2", "secret"))

        //DB data check
        val dbToken = User.findByUid("uid2") match {
          case Some(user) => user.token
          case _ => ""
        }

        token mustEqual dbToken

        //no renewal of token check
        ServiceDAO.getToken(TokenRequest("uid2", "secret")) mustEqual token
      }
    }
  }

  "Link posting logic in Service DAO" should {
    "create code in case of empty optional folder id and provided code" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {

        createUser(User(null, "uid", "token"))

        val req = PostLinkRequest("token", "http://someawesomewebpage.com", None, None)

        val res = ServiceDAO.shortenUrl(req)

        res.code mustEqual ServiceDAO.shorten(req.url)
        res.url mustEqual req.url

      }
    }

    "create link with provided code if it is available" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
        createUser(User(null, "uid", "token"))

        val req = PostLinkRequest("token", "http://someawesomewebpage.com", Some("short"), None)

        val res = ServiceDAO.shortenUrl(req)

        res.code mustEqual req.code.get
        res.url mustEqual req.url
      }
    }

    "throw exception if provided code is already used" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
        createUser(User(null, "uid", "token"))

        createLink("token", "folder", "awesome.com", "short_code")

        ServiceDAO.shortenUrl(PostLinkRequest("token", "awesome.com", Some("short_code"), None)) must throwA(new Exception("Code unavailable, sorry"))

      }
    }


    "assign folder to the link if folder exists" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
        createUser(User(null, "uid", "token"))

        createFolder("token", "folder", "title")

        ServiceDAO.shortenUrl(PostLinkRequest("token", "awesome.com", None, Some("folder"))) must not throwA(new Exception("Folder does not exist, sorry"))
      }
    }


    "create folder if specified folder does not exist" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
        val uid = createUser(User(null, "uid", "token")).id.get

        DB.withConnection {
          implicit connection =>
            SQL("select * from folder where uid = {uid} and text_id = {text_id}").on("uid" -> uid, "text_id" -> "folder").using(Folder.parser).list()
        }.size mustEqual 0

        ServiceDAO.shortenUrl(PostLinkRequest("token", "awesome.com", None, Some("folder")))

        val folder = DB.withConnection {
          implicit connection =>
            SQL("select * from folder where uid = {uid} and text_id = {text_id}").on("uid" -> uid, "text_id" -> "folder").using(Folder.parser).list().head
        }

        folder.textId mustEqual "folder"
        folder.userId mustEqual uid
      }
    }


    "assign provided code and folder to the link is everything is ok" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
        createUser(User(null, "uid", "token"))

        createFolder("token", "folder", "title")

        ServiceDAO.shortenUrl(PostLinkRequest("token", "awesome.com", Some("short_code"), Some("folder"))) must not throwA(new Exception("Folder does not exist, sorry"))
        ServiceDAO.shortenUrl(PostLinkRequest("token", "awesome.com", Some("another_short_code"), Some("folder"))) must not throwA(new Exception("Code unavailable, sorry"))

      }
    }

  }

  "Click posting logic in Service DAO" should {
    "get proper url for code and increment clicks count" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {

        createUser(User(null, "uid", "token"))
        createLink("token", "folder", "awesome.org", "short")

        val req = PostStatsRequest("referrer", "127.0.0.1")

        val res = ServiceDAO.postClick("short", req)

        res mustEqual "awesome.org"
        Link.findByCode("short").get.clicks mustEqual 1
      }
    }
    "throw exception if code is not presented" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
        val req = PostStatsRequest("referrer", "127.0.0.1")

        ServiceDAO.postClick("short", req) must throwA(new Exception("No such code!"))
      }
    }
  }

  "Code stats request logic in Service DAO" should {
    "throw exception if user token incorrect" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
        ServiceDAO.getStatsForCode("code", "token") must throwA(new Exception("No user with such token! Incident will be reported."))

      }
    }
    "throw exception if code is not presented" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
        createUser(User(null, "uid", "token"))

        ServiceDAO.getStatsForCode("code", "token") must throwA(new Exception("No link with such code!"))
      }
    }
    "provide proper stats response object" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
        createUser(User(null, "uid", "token"))
        createLink("token", "folder", "awesome.org", "short")

        val response = ServiceDAO.getStatsForCode("short", "token")

        response.clicks mustEqual 0
        response.folder_id mustEqual "folder"
        response.link mustEqual LinkResponse("awesome.org", "short")

      }
    }
  }
}