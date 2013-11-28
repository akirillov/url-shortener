package helpers

import models.User
import play.api.db.DB
import anorm._
import play.api.Play.current


object DBHelper {

  def createUser(user: User){
    DB.withConnection { implicit connection =>
      SQL("insert into user (secret, token) values ({secret}, {token})")
      .on(
        'secret -> user.secret,
        'token -> user.token
      ).executeUpdate()
    }
  }

  def dropUserBy(id: Pk[Long]) =
    DB.withConnection { implicit connection =>
      SQL("delete from user where id = {id}").on('id -> id).executeUpdate()
    }

  def dropAllUsers{
    DB.withConnection { implicit connection =>
      SQL("delete from user").executeUpdate()
    }
  }



}
