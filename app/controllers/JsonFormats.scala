package controllers

import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._

object JsonFormats {

  //REQUESTS

  //Reads -------------------------------------------------------

  implicit val tokenRequestReads: Reads[TokenRequest] = (
    (__ \ "user_id").read[String] and
      (__ \ "secret").read[String]
    )(TokenRequest)

  implicit val postStatsRequestReads: Reads[PostStatsRequest] = (
    (__ \ "referrer").read[String] and
      (__ \ "remote_ip").read[String]
    )(PostStatsRequest)

  implicit val linkPostRequestReads: Reads[PostLinkRequest] = (
    (__ \ "token").read[String] and
      (__ \ "url").read[String] and
      (__ \ "code").readNullable[String]and
      (__ \ "folder_id").readNullable[String]
    )(PostLinkRequest)


  implicit val getDataRequestReads: Reads[GetDataRequest] = (
    (__ \ "token").read[String] and
      (__ \ "offset").readNullable[Long]and
      (__ \ "limit").readNullable[Long]
    )(GetDataRequest)

  //Writes -------------------------------------------------------
  implicit val tokenRequestWrites: Writes[TokenRequest] = (
    (__ \ "user_id").write[String] and
      (__ \ "secret").write[String]
    )(unlift(TokenRequest.unapply))

  implicit val postStatsRequestWrites: Writes[PostStatsRequest] = (
    (__ \ "referrer").write[String] and
      (__ \ "remote_ip").write[String]
    )(unlift(PostStatsRequest.unapply))

  implicit val linkPostRequestWrites: Writes[PostLinkRequest] = (
    (__ \ "token").write[String] and
      (__ \ "url").write[String] and
      (__ \ "code").writeNullable[String]and
      (__ \ "folder_id").writeNullable[String]
    )(unlift(PostLinkRequest.unapply))

  implicit val getDataRequestWrites: Writes[GetDataRequest] = (
    (__ \ "token").write[String] and
      (__ \ "offset").writeNullable[Long]and
      (__ \ "limit").writeNullable[Long]
    )(unlift(GetDataRequest.unapply))



  //RESPONSES

  //Reads ----------------------------------------------------------------------------------------
  implicit val linkReads: Reads[LinkResponse] = (
    (__ \ "url").read[String] and
      (__ \ "code").read[String]
    )(LinkResponse)

  implicit val folderReads: Reads[FolderResponse] = (
    (__ \ "id").read[String] and
      (__ \ "title").read[String]
    )(FolderResponse)

  implicit val codeStatsReads: Reads[CodeStatsResponse] = (
    (__ \ "link").read[LinkResponse] and
      (__ \ "folder_id").read[String] and
      (__ \ "clicks").read[Long]
    )(CodeStatsResponse)

  implicit val clickReads: Reads[ClickResponse] = (
    (__ \ "referrer").read[String] and
      (__ \ "remoteIP").read[String]
    )(ClickResponse)

  //Writes ----------------------------------------------------------------------------------------
  implicit val linkWrites: Writes[LinkResponse] = (
    (__ \ "url").write[String] and
      (__ \ "code").write[String]
    )(unlift(LinkResponse.unapply))

  implicit val folderWrites: Writes[FolderResponse] = (
    (__ \ "id").write[String] and
      (__ \ "title").write[String]
    )(unlift(FolderResponse.unapply))

  implicit val codeStatsWrites: Writes[CodeStatsResponse] = (
    (__ \ "link").write[LinkResponse] and
      (__ \ "folder_id").write[String] and
      (__ \ "clicks").write[Long]
    )(unlift(CodeStatsResponse.unapply))

  implicit val clickWrites: Writes[ClickResponse] = (
    (__ \ "referrer").write[String] and
      (__ \ "remoteIP").write[String]
    )(unlift(ClickResponse.unapply))
}
