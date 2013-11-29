package logic

import controllers._
import controllers.LinkResponse
import controllers.GetDataRequest
import controllers.PostLinkRequest
import controllers.PostStatsRequest
import models.User
import java.security.MessageDigest

object ServiceDAO {

  def createToken(user: String, secret: String) = MessageDigest.getInstance("SHA").digest(user.concat(secret).getBytes).mkString


  /**
   * Methods checks whether user exists in DB and returns her token.
   * Otherwise new entry created
   * @param request - [[controllers.TokenRequest]]
   * @return user token
   */
  def getToken(request: TokenRequest): String = {

    // assumed that here performed some user authorization
    // we should not store secret on our side because it may change in time

    User.findByUid(request.userId) match {
      case Some(u) => u.token
      case None => User.createWithSecret(request.userId, createToken(request.userId, request.secret)).token
    }
  }

  /**
   * Method shortens original link via XXX and stores it into DB
   * @param request - [[controllers.PostLinkRequest]] instance
   * @return [[controllers.LinkResponse]] instance
   */
  def shortenUrl(request: PostLinkRequest): LinkResponse = {
    null
  }

  /**
   * Method provides logic for posting click event stats to corresponding link
   * @param request - [[controllers.PostStatsRequest]]
   * @return original link
   */
  def getLink(request: PostStatsRequest): String = {

    //TODO: save click object (async is better here)

    //TODO: не забыть заапдейтить поле счета кликов в объекте Link
    null
  }

  def getStatsForCode(code: String, token: String): CodeStatsResponse = {


    null
  }

  def getFolderLinks(request: GetDataRequest): FolderResponse = {



    null
  }

  def getUserLinks(request: GetDataRequest): List[LinkResponse] = {
    null
  }

  def getFolders(token: String): List[FolderResponse] = {

    null
  }

  def getClicks(request: GetDataRequest): List[ClickResponse] = {

    null
  }









}
