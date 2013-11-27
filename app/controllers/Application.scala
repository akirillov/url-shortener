package controllers

import play.api._
import play.api.mvc._

object Application extends Controller {
  
  def index = Action {
    Ok(views.html.index())
  }

  def getToken() = Action {
    Ok("got getToken request")
  }

  def postLink(code: String) = Action {
    Ok("got postLink request "+code)
  }

  def getLink(code: String) = Action {
    Ok("got getLink request "+code)
  }

  def getFolder(id: String) = Action {
    Ok("got getFolder request "+id)
  }

  def getClicks(code: String) = Action {
    Ok("got getClicks request "+code)
  }
}