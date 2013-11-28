package models

import anorm.{NotAssigned, Pk}
import play.api.db._
import anorm._
import anorm.SqlParser._


case class User(id: Pk[Long] = NotAssigned, secret: String, token: String)

object User  {
  val parser = {
      get[Pk[Long]]("id") ~
      get[String]("secret") ~
      get[Option[String]]("token") map {
      case pk ~ secret ~ token => User(pk, secret, token.getOrElse(null))
    }
  }

  def findBy(id: Pk[Long]): User = {
    DB.withConnection {
      implicit connection =>
        SQL("select * from user where id = {id}").on("id" -> id.get).using(parser).single()
    }
  }
}
