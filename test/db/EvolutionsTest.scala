package db

import play.api.test.FakeApplication
import play.api.test.Helpers._
import play.api.db.DB
import anorm._
import play.api.Play.current
import org.specs2.mutable.Specification

class EvolutionsTest extends Specification {

  "Evolutions" should {
    "be applied without errors" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
        DB.withConnection {
          implicit connection =>
            SQL("select count(1) from user").execute()
            SQL("select count(1) from folder").execute()
            SQL("select count(1) from link").execute()
            SQL("select count(1) from click").execute()
        }
      }
    }
  }
}
