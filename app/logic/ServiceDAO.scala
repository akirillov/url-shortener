package logic

import controllers._
import controllers.LinkResponse
import controllers.GetDataRequest
import controllers.PostLinkRequest
import controllers.PostStatsRequest
import models.{Folder, Link, User}
import java.security.MessageDigest
import java.util.zip.CRC32

object ServiceDAO {

  def createToken(user: String, secret: String) = MessageDigest.getInstance("SHA").digest(user.concat(secret).getBytes).mkString

  def shorten(url: String) = {
    val crc = new CRC32()
    crc.update(url.getBytes())
    crc.getValue().toHexString
  }

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

    /*

    Это укороченный адрес.
Можно создать ссылку с желаемым кодом, тогда, если код доступен, то возвращается он же.
Если код недоступен, то, наверное, сервис говорит «ошибка, код недоступен». Это было бы самым простым вариантом.
Если код не передан, генерируется случайный свободный код.

    */

    val code = request.code match {
      case Some(c) => {
        if(Link.checkExists(c)) throw new Exception("Code unavailable, sorry")
        else c
      }
      case None => shorten(request.url)
    }

    //TODO: really, folder mechanics suck (how do we CRUD folders?)

    val folderID = request.folderId match {
      case Some(fid) => Folder.getByTextId(fid, request.token) match {
        case Some(folder) => folder.id.get
        case None => throw new Exception("Folder does not exist, sorry")
      }
      case None => Folder.getRootFolderForUser(request.token).id.get
    }

    val link = Link.createLink(User.findByToken(request.token).get.id.get, folderID, request.url, code)

    LinkResponse(request.url, code)
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
