package controllers

import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.data.validation.ValidationError
import play.api.libs.json.Reads._

object JsonFormats {

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
}
