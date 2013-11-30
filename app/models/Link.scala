package models

import anorm._
import anorm.SqlParser._
import play.api.db.DB
import play.api.Play.current

case class Link(id: Pk[Long] = NotAssigned, userId: Long, folderId: Long, url: String, code: String)

object Link  {
  val parser = {
    get[Pk[Long]]("id") ~
      get[Long]("uid") ~
      get[Long]("fid") ~
      get[String]("url") ~
      get[String]("code") map {
      case pk ~ uid ~ fid ~ url ~ code => Link(pk, uid, fid, url, code)
    }
  }

  def findBy(id: Pk[Long]): Link = {
    DB.withConnection {
      implicit connection =>
        SQL("select * from link where id = {id}").on("id" -> id.get).using(parser).single()
    }
  }

  def checkExists(code: String): Boolean = {
    DB.withConnection {
      implicit connection =>
        SQL("select count(*) from link where code = {code}").on("code" -> code).as(scalar[Long].single) > 0
    }
  }

  def createLink(uid: Long, fid: Long, url: String, code: String): Link = {
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

        Link(new Id(id), uid, fid, url, code)
    }
  }
}