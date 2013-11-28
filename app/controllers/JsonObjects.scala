package controllers


//Requests

case class TokenRequest(userId: String, secret: String)

case class GetDataRequest(token: String, offset: Option[Long], limit: Option[Long])

case class PostStatsRequest(referrer: String, remote_ip: String)

case class PostLinkRequest(token: String, url: String, code: Option[String], folderId: Option[String])

//Responses

case class Link(url: String, code: String)

case class CodeStats(link: Link, folder_id: String, clicks: Int)

case class Folder(id: String, title: String)