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
    "find exactly one user by his secret" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {

          createUser(User(null, "super_secret", "token"))

          User.findBySecret("super_secret").token must be equalTo "token"

      }
    }
  }


}