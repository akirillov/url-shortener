package test

import org.specs2.mutable._

import play.api.test._
import play.api.test.Helpers._
import controllers._
import play.api.libs.json.Json
import controllers.JsonFormats._
import helpers.DBHelper._
import models.User
import controllers.LinkResponse
import play.api.test.FakeHeaders
import controllers.PostStatsRequest
import controllers.PostLinkRequest
import play.api.test.FakeApplication
import scala.Some
import controllers.TokenRequest
import logic.ServiceDAO

/**
 * Add your spec here.
 * You can mock out a whole application including requests, plugins etc.
 * For more information, consult the wiki.
 */
class ApplicationSpec extends Specification {
  
  "Application" should {

    "return token for user" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {

        val Some(result) = route(
          FakeRequest(
            GET,
            "/token",
            FakeHeaders(Seq("Content-Type" -> Seq("application/json"))),
            Json.toJson(TokenRequest("user", "secret"))
          )
        )

        status(result) must equalTo(OK)
        contentAsString(result).size must be greaterThan 0
      }
    }
    "post a link for user" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
        createUser(User(null, "uid", "token"))

        val Some(result) = route(
          FakeRequest(
            POST,
            "/link",
            FakeHeaders(Seq("Content-Type" -> Seq("application/json"))),
            Json.toJson(PostLinkRequest("token", "www.awesome.org", Some("awsm"), None))
          )
        )

        status(result) must equalTo(OK)
        contentAsString(result).size must be greaterThan 0

        val response = Json.parse(contentAsString(result)).asOpt[LinkResponse]

        response.get.url mustEqual "www.awesome.org"
        response.get.code mustEqual "awsm"

        val Some(result2) = route(
          FakeRequest(
            POST,
            "/link",
            FakeHeaders(Seq("Content-Type" -> Seq("application/json"))),
            Json.toJson(PostLinkRequest("token", "www.awesome.org", Some("awsm"), None))
          )
        )

        contentAsString(result2) mustEqual "Code unavailable, sorry"

      }
    }
    "return a link on click call" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
        createUser(User(null, "uid", "token"))
        createLink("token", "folder", "www.awesome.org", "awsm")

        val Some(result) = route(
          FakeRequest(
            POST,
            "/link/awsm",
            FakeHeaders(Seq("Content-Type" -> Seq("application/json"))),
            Json.toJson(PostStatsRequest("johndoe", "127.0.0.1"))
          )
        )

        status(result) must equalTo(OK)
        contentAsString(result).size must be greaterThan 0

        contentAsString(result) mustEqual "www.awesome.org"
      }
    }
    "return a list of links for user" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
        createUser(User(null, "uid", "token"))
        createLink("token", "folder", "www.awesome.org", "awsm")
        createLink("token", "folder", "www.awesome1.org", "awsm1")
        createLink("token", "folder", "www.awesome.org2", "awsm2")

        val Some(result) = route(
          FakeRequest(
            GET,
            "/link",
            FakeHeaders(Seq("Content-Type" -> Seq("application/json"))),
            Json.toJson(GetDataRequest("token", None, None))
          )
        )

        status(result) must equalTo(OK)
        contentAsString(result).size must be greaterThan 0

        val links = Json.parse(contentAsString(result)).asOpt[List[LinkResponse]].get

        links.size mustEqual 3
        links(0).code mustEqual "awsm"
        links(1).code mustEqual "awsm1"
        links(2).code mustEqual "awsm2"

      }
    }
    "return a list of links for given folder" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
        createUser(User(null, "uid", "token"))
        createLink("token", "root", "www.awesome.org", "awsm")
        createLink("token", "root", "www.awesome1.org", "awsm1")
        createLink("token", "root", "www.awesome.org2", "awsm2")

        val Some(result) = route(
          FakeRequest(
            GET,
            "/folder/root",
            FakeHeaders(Seq("Content-Type" -> Seq("application/json"))),
            Json.toJson(GetDataRequest("token", None, None))
          )
        )

        status(result) must equalTo(OK)
        contentAsString(result).size must be greaterThan 0

        val links = Json.parse(contentAsString(result)).asOpt[List[LinkResponse]].get

        links.size mustEqual 3
        links(0).code mustEqual "awsm"
        links(1).code mustEqual "awsm1"
        links(2).code mustEqual "awsm2"

      }
    }
    "return stats for given link" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
        createUser(User(null, "uid", "token"))
        createLink("token", "folder", "www.awesome.org", "awsm")

        1 to 10 foreach ( _ => ServiceDAO.postClick("awsm", PostStatsRequest("ref", "127.0.0.1")))

        val Some(result) = route(
          FakeRequest(
            GET,
            "/link/awsm",
            FakeHeaders(Seq("Content-Type" -> Seq("application/json"))),
            Json.toJson(GetDataRequest("token", None, None))
          )
        )

        status(result) must equalTo(OK)
        contentAsString(result).size must be greaterThan 0

        val stats = Json.parse(contentAsString(result)).asOpt[CodeStatsResponse].get

        stats.link.url mustEqual "www.awesome.org"
        stats.clicks mustEqual 10
      }
    }
    "return a list of folders for user" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
        createUser(User(null, "uid", "token"))

        createFolder("token", "/", "root folder")
        createFolder("token", "/home", "home folder")
        createFolder("token", "/etc", "default")

        val Some(result) = route(
          FakeRequest(
            GET,
            "/folder",
            FakeHeaders(Seq("Content-Type" -> Seq("application/json"))),
            Json.toJson(GetDataRequest("token", None, None))
          )
        )

        status(result) must equalTo(OK)
        contentAsString(result).size must be greaterThan 0

        val folders = Json.parse(contentAsString(result)).asOpt[List[FolderResponse]].get

        folders.size mustEqual 3
        folders(0).id mustEqual "/"
        folders(1).id mustEqual "/home"
        folders(2).id mustEqual "/etc"

      }
    }
    "return stats for given link" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
        createUser(User(null, "uid", "token"))
        createLink("token", "folder", "www.awesome.org", "awsm")

        1 to 10 foreach ( _ => ServiceDAO.postClick("awsm", PostStatsRequest("ref", "127.0.0.1")))

        val Some(result) = route(
          FakeRequest(
            GET,
            "/link/awsm",
            FakeHeaders(Seq("Content-Type" -> Seq("application/json"))),
            Json.toJson(GetDataRequest("token", None, None))
          )
        )

        status(result) must equalTo(OK)
        contentAsString(result).size must be greaterThan 0

        val stats = Json.parse(contentAsString(result)).asOpt[CodeStatsResponse].get

        stats.link.url mustEqual "www.awesome.org"
        stats.clicks mustEqual 10
      }
    }
  }
  "return clicks for given link" in {
    running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
      createUser(User(null, "uid", "token"))
      createLink("token", "folder", "www.awesome.org", "awsm")

      1 to 10 foreach ( i => ServiceDAO.postClick("awsm", PostStatsRequest("ref"+i, "127.0.0.1")))

      val Some(result) = route(
        FakeRequest(
          GET,
          "/link/awsm/clicks",
          FakeHeaders(Seq("Content-Type" -> Seq("application/json"))),
          Json.toJson(GetDataRequest("token", None, None))
        )
      )

      status(result) must equalTo(OK)
      contentAsString(result).size must be greaterThan 0

      val clicks = Json.parse(contentAsString(result)).asOpt[List[ClickResponse]].get

      clicks.size mustEqual 10
      clicks(0).referrer mustEqual "ref1"
      clicks(4).referrer mustEqual "ref5"
      clicks(9).referrer mustEqual "ref10"

    }
  }
}
