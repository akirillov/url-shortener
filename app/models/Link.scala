package models

import anorm._
import anorm.SqlParser._
import play.api.db.DB
import play.api.Play.current

case class Link(id: Pk[Long] = NotAssigned, userId: Long, folderId: Long, userToken: String, url: String, code: String)

object Link  {
  val parser = {
    get[Pk[Long]]("id") ~
      get[Long]("uid") ~
      get[Long]("fid") ~
      get[String]("token") ~
      get[String]("url") ~
      get[String]("code") map {
      case pk ~ uid ~ fid ~ token ~ url ~ code => Link(pk, uid, fid, token, url, code)
    }
  }

  def findBy(id: Pk[Long]): Link = {
    DB.withConnection {
      implicit connection =>
        SQL("select * from link where id = {id}").on("id" -> id.get).using(parser).single()
    }
  }
}