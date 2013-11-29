package db

import org.specs2.mutable.{After, Specification}
import play.api.test.Helpers._
import play.api.db.DB
import play.api.test.FakeApplication
import helpers.DBHelper._
import models.User
import play.api.Play.current
import org.specs2.specification.Scope

class UserTest extends Specification {

  implicit val context = new Scope with After {
    def after = running(FakeApplication(additionalConfiguration = inMemoryDatabase())) { dropAllUsers }
  }

  "User Model" should {
    "find exactly one user by his uid" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {

          createUser(User(null, "uid", "token"))

          val token = User.findByUid("uid") match {
            case Some(user) => user.token
            case _ => ""
          }

          token mustEqual "token"

      }
    }

    "create user with id" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {

        val user = User.createWithSecret("uid", "token")

        user.id mustNotEqual null
        user.uid mustEqual "uid"
        user.token mustEqual "token"
      }
    }
  }


}