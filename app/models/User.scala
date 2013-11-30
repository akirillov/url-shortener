package models

import anorm.{NotAssigned, Pk}
import play.api.db._
import anorm._
import anorm.SqlParser._
import play.api.Play.current
import models.Helper._


case class User(id: Pk[Long] = NotAssigned, uid: String, token: String)

object User  {
  val parser = {
      get[Pk[Long]]("id") ~
      get[String]("uid") ~
      get[Option[String]]("token") map {
      case pk ~ secret ~ token => User(pk, secret, token.getOrElse(null))
    }
  }

  def findByUid(uid: String): Option[User] = {
    DB.withConnection {
      implicit connection =>
        val users = SQL("select * from user where uid = {uid}").on("uid" -> uid).using(parser).list()
        checkAndReturn(users, "user ID")
    }
  }

  def findByToken(token: String): Option[User] = {
    DB.withConnection {
      implicit connection =>
        val users = SQL("select * from user where token = {token}").on("token" -> token).using(parser).list()
        checkAndReturn(users, "token")
    }
  }



  def createWithSecret(uid: String, token: String): User = {
    DB.withConnection {
      implicit connection =>
        SQL("insert into user(uid, token) values ({uid}, {token});").on(
          'uid -> uid,
          'token -> token
        ).executeUpdate()
        val id = SQL("SELECT SCOPE_IDENTITY()")().collect {
          case Row(id: Int) => id
        }.head

        User(new Id(id), uid, token)
    }
  }
}
