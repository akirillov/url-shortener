package logic

import org.specs2.mutable.{After, Specification}
import org.specs2.specification.Scope
import play.api.test.Helpers._
import play.api.test.FakeApplication
import helpers.DBHelper._
import play.api.test.FakeApplication
import models.User
import controllers.TokenRequest

class LogicTests extends Specification {

  implicit val context = new Scope with After {
    def after = running(FakeApplication(additionalConfiguration = inMemoryDatabase())) { dropAllUsers }
  }

  "Service DAO" should {
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
}