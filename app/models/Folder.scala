package models

import anorm._
import anorm.SqlParser._
import play.api.db.DB
import play.api.Play.current

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
        SQL("select count(*) from folder join user on folder.uid = user.id where text_id = {textID} and user.token = {token}").on("token" -> token, "textID" -> textID).as(scalar[Long].single) > 0
    }
  }

  def getRootFolderForUser(token: String): Folder = {
    val uid = User.findByToken(token).get.id

    getByTextId("/", token) match {
      case Some(folder) => folder

      case None =>{
        val fid = "/"
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
        val folders = SQL("select folder.id, folder.uid, folder.text_id, folder.title from folder join user on folder.uid = user.id where text_id = {textID} and user.token={token}").on("textID" -> textID, "token" -> token).using(parser).list()

        folders.size match {
          case 0 => None
          case 1 => Some(folders.head)
          case _ => throw new Exception("Data integrity error: more than one folder with same ID!")
        }
    }
  }
}