package controllers

import play.api.mvc._
import play.api.libs.json.{JsValue, Reads, JsError}
import JsonFormats._

object Application extends Controller {

  def index = Action {
    Ok(views.html.index())
  }

  //curl --header "Content-type: application/json" --request GET --data '{"user_id": "uid", "secret": "aa1865139a1caceabfa45e6635aa7761"}' http://localhost:9000/token
  def getToken = Action(parse.json) { request =>
    request.body.validate[TokenRequest].map{

      case tR: TokenRequest => Ok(tR.toString())

    }.recoverTotal{
      e => BadRequest("Detected error:"+ JsError.toFlatJson(e))
    }
  }


  /*
  curl --header "Content-type: application/json" --request POST --data '{"token": "294ffba18b49dcba153f144a651aa926", "url": "http://en.wikipedia.org/"}' http://localhost:9000/link

  curl --header "Content-type: application/json" --request POST --data '{"token": "294ffba18b49dcba153f144a651aa926", "url": "http://en.wikipedia.org/", "code":"fc0227ba", "folder_id":"home"}' http://localhost:9000/link
  */
  def postLink = Action(parse.json) { request =>
    request.body.validate[PostLinkRequest].map{

      case tR: PostLinkRequest => Ok(tR.toString())

    }.recoverTotal{
      e => BadRequest("Detected error:"+ JsError.toFlatJson(e))
    }
  }

  //curl --header "Content-type: application/json" --request POST --data '{"referrer": "someone", "remote_ip": "127.0.0.1"}' http://localhost:9000/link/fc0227ba
  def postStats(code: String) = Action(parse.json) { request =>
    request.body.validate[PostStatsRequest].map{

      case tR: PostStatsRequest => Ok(tR.toString())

    }.recoverTotal{
      e => BadRequest("Detected error:"+ JsError.toFlatJson(e))
    }
  }


  //Methods below are similar in parameters set (similar JSON request body)

  /*
  curl --header "Content-type: application/json" --request GET --data '{"token": "294ffba18b49dcba153f144a651aa926"}' http://localhost:9000/link

  curl --header "Content-type: application/json" --request GET --data '{"token": "294ffba18b49dcba153f144a651aa926", "limit": 1000}' http://localhost:9000/link
  */
  def getLink(code: String) = Action(parse.json) { request =>
    request.body.validate[GetDataRequest].map{

      case tR: GetDataRequest => Ok(tR.toString())

    }.recoverTotal{
      e => BadRequest("Detected error:"+ JsError.toFlatJson(e))
    }
  }

  //curl --header "Content-type: application/json" --request GET --data '{"token": "294ffba18b49dcba153f144a651aa926", "limit": 1000}' http://localhost:9000/link
  def getFolder(id: String) = Action(parse.json) { request =>
    request.body.validate[GetDataRequest].map{

      case tR: GetDataRequest => Ok(tR.toString())

    }.recoverTotal{
      e => BadRequest("Detected error:"+ JsError.toFlatJson(e))
    }
  }

  //curl --header "Content-type: application/json" --request GET --data '{"token": "294ffba18b49dcba153f144a651aa926", "limit": 1000}' http://localhost:9000/link/23rqewfr/clicks
  def getClicks(code: String) = Action(parse.json) { request =>
    request.body.validate[GetDataRequest].map{

      case tR: GetDataRequest => Ok(tR.toString())

    }.recoverTotal{
      e => BadRequest("Detected error:"+ JsError.toFlatJson(e))
    }
  }
}