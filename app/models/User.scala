package models

import anorm.{NotAssigned, Pk}
import play.api.db._
import anorm._
import anorm.SqlParser._
import play.api.Play.current


case class User(id: Pk[Long] = NotAssigned, uid: String, token: String)

object User  {
  val parser = {
      get[Pk[Long]]("id") ~
      get[String]("uid") ~
      get[Option[String]]("token") map {
      case pk ~ secret ~ token => User(pk, secret, token.getOrElse(null))
    }
  }


  //TODO: candidate for removal
  def findById(id: Pk[Long]): User = {
    DB.withConnection {
      implicit connection =>
        SQL("select * from user where id = {id}").on("id" -> id.get).using(parser).single()
    }
  }

  def findByUid(uid: String): Option[User] = {
    DB.withConnection {
      implicit connection =>
        val users = SQL("select * from user where uid = {uid}").on("uid" -> uid).using(parser).list()

        users.size match {
          case 0 => None
          case 1 => Some(users.head)
          case _ => throw new Exception("Data integrity error: more than one user with same UID!")
        }
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
        return User(new Id(id), uid, token)
    }
  }
}
