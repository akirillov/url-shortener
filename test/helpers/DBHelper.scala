package helpers

import models.{Link, Folder, User}
import play.api.db.DB
import anorm._
import play.api.Play.current


object DBHelper {

  def createUser(user: User) = {
    User.findByUid(user.uid) match {
      case Some(u) => u
      case None =>
        DB.withConnection { implicit connection =>
        SQL("insert into user (uid, token) values ({uid}, {token})")
          .on(
          'uid -> user.uid,
          'token -> user.token
        ).executeUpdate()
        val id = SQL("SELECT SCOPE_IDENTITY()")().collect {
          case Row(id: Int) => id
        }.head

        User(new Id(id), user.uid, user.token)
      }
    }
  }

  def createFolder(token: String, folderID: String, folderTitle: String): Folder = {
    Folder.getByTextId(folderID, token) match {
      case Some(f) => f
      case None => {
        val uid = User.findByToken(token).get.id

        DB.withConnection { implicit connection =>
          SQL("insert into folder (uid, text_id, title) values ({uid}, {text_id}, {title})")
            .on(
            'uid -> uid,
            'text_id -> folderID,
            'title -> folderTitle
          ).executeUpdate()

          val id = SQL("SELECT SCOPE_IDENTITY()")().collect {
            case Row(id: Int) => id
          }.head

          Folder(new Id(id), uid.get, folderID, folderTitle)
        }
      }
    }
  }

  def createLink(token: String, folder: String, url: String, code: String): Link = {
    val uid = User.findByToken(token).get.id.get
    val fid = createFolder(token, folder, "title").id.get


      DB.withConnection {
        implicit connection =>
          SQL("insert into link(uid, fid, url, code) values ({uid}, {fid}, {url}, {code});").on(
            'uid -> uid,
            'fid -> fid,
            'url -> url,
            'code -> code
          ).executeUpdate()
          val id = SQL("SELECT SCOPE_IDENTITY()")().collect {
            case Row(id: Int) => id
          }.head

          Link(new Id(id), uid, fid, url, code, 0)
      }

  }



  def dropUserBy(id: Pk[Long]) =
    DB.withConnection { implicit connection =>
      SQL("delete from user where id = {id}").on('id -> id).executeUpdate()
    }

  def dropAllUsers{
    DB.withConnection { implicit connection =>
      SQL("delete from user").executeUpdate()
    }
  }

  def dropAllFolders{
    DB.withConnection { implicit connection =>
      SQL("delete from folder").executeUpdate()
    }
  }


  def dropAllClicks{
    DB.withConnection { implicit connection =>
      SQL("delete from click").executeUpdate()
    }
  }



  def clearAll{
    dropAllFolders
    dropAllUsers
    dropAllClicks
  }

}
