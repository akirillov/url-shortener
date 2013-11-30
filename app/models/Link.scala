package models

import anorm._
import anorm.SqlParser._
import play.api.db.DB
import play.api.Play.current

case class Link(id: Pk[Long] = NotAssigned, userId: Long, folderId: Long, url: String, code: String, clicks: Long)

object Link  {
  val parser = {
    get[Pk[Long]]("id") ~
      get[Long]("uid") ~
      get[Long]("fid") ~
      get[String]("url") ~
      get[String]("code") ~
      get[Long]("clicks") map {
      case pk ~ uid ~ fid ~ url ~ code ~ clicks => Link(pk, uid, fid, url, code, clicks)
    }
  }

  def findByCode(code: String): Option[Link] = {
    DB.withConnection {
      implicit connection =>
        val links = SQL("select * from link where code = {code}").on("code" -> code).using(parser).list()

        links.size match {
          case 0 => None
          case 1 => Some(links.head)
          case _ => throw new Exception("Data integrity error: more than one link with same code !")
        }
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

        Link(new Id(id), uid, fid, url, code, 0)
    }
  }

  def incClicksCount(linkID: Long){
    DB.withConnection {
      implicit connection =>
        SQL("update link set clicks=clicks+1 where id = {id};").on(
          'id -> linkID
        ).executeUpdate()
    }
  }
}