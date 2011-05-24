package org.ds.satchel.scalatra

import org.scalatra._
import org.ds.satchel._

class SatchelServlet extends ScalatraServlet {

  private val js = "text/javascript"
  private val css = "text/css"
      
  get("/:satchel.:ext") {
    
    if (!Satchel.exists(params("satchel"))) {
      halt(404)
    }
        
    inferContentType(params("ext"))
    Satchel.find(params("satchel")).processed
  }

  private def inferContentType(ext:String) = {
    contentType = ext match {
      case "js"       => js
      case "coffee"   => js
      case "jst"      => js
      case "mustache" => js
      case "css"      => css
      case "less"     => css
      case _          => "text/plain"
    }
  }

}