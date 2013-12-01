package models

import anorm._
import anorm.SqlParser._
import play.api.db.DB
import play.api.Play.current
import models.Helper._

case class Folder(id: Pk[Long] = NotAssigned, userId: Long, textId: String, title: String)

object Folder  {
  val parser = {
    get[Pk[Long]]("id") ~
      get[Long]("uid") ~
      get[String]("text_id") ~
      get[String]("title") map {
      case pk ~ uid ~ textId ~ title => Folder(pk, uid, textId, title)
    }
  }

  def checkExists(textID: String, token: String): Boolean = {
    DB.withConnection {
      implicit connection =>
        SQL("select count(*) from folder join user on folder.uid = user.id where text_id = {folderID} and user.token = {token}").on("token" -> token, "folderID" -> textID).as(scalar[Long].single) > 0
    }
  }

  def getRootFolderForUser(token: String): Folder = {
    val uid = User.findByToken(token).get.id

    getByTextId("root", token) match {
      case Some(folder) => folder

      case None =>{
        val fid = "root"
        val title = "root folder"

        DB.withConnection { implicit connection =>
          SQL("insert into folder (uid, text_id, title) values ({uid}, {text_id}, {title})")
            .on(
            'uid -> uid,
            'text_id -> fid,
            'title -> title
          ).executeUpdate()

          val id = SQL("SELECT SCOPE_IDENTITY()")().collect {
            case Row(id: Int) => id
          }.head

          Folder(new Id(id), uid.get, fid, title)
        }
      }
    }
  }

  def getByTextId(textID: String, token: String): Option[Folder] = {
    DB.withConnection {
      implicit connection =>
        val folders = SQL("select folder.id, folder.uid, folder.text_id, folder.title from folder join user on folder.uid = user.id where text_id = {folderID} and user.token={token}").on("folderID" -> textID, "token" -> token).using(parser).list()

        checkAndReturn(folders, "Text ID")
    }
  }

  def getFoldersByUserId(id: Long): Seq[Folder] = {
    DB.withConnection {
      implicit connection =>
        SQL("select * from folder where uid = {uid}").on("uid" -> id).using(parser).list()
    }
  }

  def createFolder(uid: Long, folderID: String, title: String): Folder  = {
    DB.withConnection {
      implicit connection =>
        SQL("insert into folder(uid, text_id, title) values ({uid}, {text_id}, {title});").on(
          'uid -> uid,
          'text_id -> folderID,
          'title -> title
        ).executeUpdate()
        val id = SQL("SELECT SCOPE_IDENTITY()")().collect {
          case Row(id: Int) => id
        }.head

        Folder(new Id(id), uid, folderID, title)
    }
  }

  def getById(id: Long): Option[Folder] = {
    DB.withConnection {
      implicit connection =>
        val folders = SQL("select * from folder where id = {id}").on("id" -> id).using(parser).list()

        checkAndReturn(folders, "ID")
    }
  }
}