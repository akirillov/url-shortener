package models

import anorm._
import anorm.SqlParser._
import play.api.db.DB
import java.util.Date
import play.api.Play.current

case class Click(id: Pk[Long] = NotAssigned, linkId: Long, date: Date, referrer: String, remoteIP: String)

object Click  {
  val parser = {
    get[Pk[Long]]("id") ~
      get[Long]("lid") ~
      get[Date]("date") ~
      get[String]("referrer") ~
      get[String]("remoteIP") map {
      case pk ~ lid ~ date ~ referrer ~ remoteIP => Click(pk, lid, date, referrer, remoteIP)
    }
  }

  def findBy(id: Pk[Long]): Click = {
    DB.withConnection {
      implicit connection =>
        SQL("select * from link where id = {id}").on("id" -> id.get).using(parser).single()
    }
  }
}
