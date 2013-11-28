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

  def findBy(id: Pk[Long]): Folder = {
    DB.withConnection {
      implicit connection =>
        SQL("select * from folder where id = {id}").on("id" -> id.get).using(parser).single()
    }
  }
}