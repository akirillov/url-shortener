package models

import anorm._
import anorm.SqlParser._
import play.api.db.DB
import java.util.{GregorianCalendar, Date}
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

  def postClick(linkID: Long, referrer: String, remoteIP: String): Click = {
    val date = new GregorianCalendar().getTime

    DB.withConnection {
      implicit connection =>
        SQL("insert into click(lid, date, referrer, remote_ip) values ({lid}, {date}, {referrer}, {remoteIP});").on(
          'lid -> linkID,
          'date -> date,
          'referrer -> referrer,
          'remoteIP -> remoteIP
        ).executeUpdate()

        val id = SQL("SELECT SCOPE_IDENTITY()")().collect {
          case Row(id: Int) => id
        }.head

        Click(new Id(id), linkID, date, referrer, remoteIP)
    }
  }
}
