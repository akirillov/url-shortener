package controllers

case class TokenRequest(userId: String, secret: String)

case class GetDataRequest(token: String, offset: Option[Long], limit: Option[Long])

case class PostStatsRequest(referrer: String, remote_ip: String)

case class PostLinkRequest(token: String, url: String, code: Option[String], folderId: Option[String])
