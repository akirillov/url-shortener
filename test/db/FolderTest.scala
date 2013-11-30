package db

import org.specs2.mutable.{Before, After, Specification}
import org.specs2.specification.Scope
import play.api.test.Helpers._
import scala.Some
import play.api.test.FakeApplication
import helpers.DBHelper._
import play.api.test.FakeApplication
import scala.Some
import models.{Folder, User}

class FolderTest  extends Specification {

  implicit val context = new Scope with After with Before{
    def before = running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
      dropAllUsers
      dropAllFolders
      createUser(User(null, "uid", "token"))
    }

    def after = running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
      dropAllUsers
      dropAllFolders
    }
  }

  "Folder Model" should {
    "find exactly one folder by its text id and user token" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
        createUser(User(null, "uid", "token"))

        val folder = createFolder("token", "folder", "title")

        val fut = Folder.getByTextId("folder", "token")

        fut.get mustNotEqual None
        fut.get.id mustEqual folder.id
        fut.get.title mustEqual folder.title
        fut.get.textId mustEqual folder.textId
      }
    }
    "check existence of folder" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
        createUser(User(null, "uid", "token"))

        Folder.checkExists("folder", "token") mustEqual false

        createFolder("token", "folder", "title")

        Folder.checkExists("folder", "token") mustEqual true
      }
    }
    "create root folder if it not exists" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
        createUser(User(null, "uid", "token"))

        val folder = Folder.getRootFolderForUser("token")

        folder.textId mustEqual "/"
        folder.title mustEqual "root folder"
      }
    }
    "return root folder if it exists" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
        createUser(User(null, "uid", "token"))

        createFolder("token", "/", "root folder")

        val folder = Folder.getRootFolderForUser("token")

        folder.textId mustEqual "/"
        folder.title mustEqual "root folder"
      }
    }
  }


}