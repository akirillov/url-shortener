package controllers


//Requests

case class TokenRequest(userId: String, secret: String)

case class GetDataRequest(token: String, offset: Option[Long], limit: Option[Long])

case class PostStatsRequest(referrer: String, remote_ip: String)

case class PostLinkRequest(token: String, url: String, code: Option[String], folderId: Option[String])

//Responses

case class LinkResponse(url: String, code: String)

case class CodeStatsResponse(link: LinkResponse, folder_id: String, clicks: Long)

case class FolderResponse(id: String, title: String)

case class ClickResponse(referrer: String, remoteIp: String)