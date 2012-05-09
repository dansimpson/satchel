package org.ds.satchel.scalatra
import org.scalatra.ScalatraKernel
import org.ds.satchel.Satchel

trait SatchelSupport { self: ScalatraKernel =>

  private val js = "text/javascript"
  private val css = "text/css"

  def serveCompiled(path: String) = {
    get(path + "/:satchel.:ext") {
      if (!Satchel.exists(params("satchel"))) {
        pass()
      }

      inferContentType(params("ext"))
      Satchel.find(params("satchel")).processed
    }
  }

  private def inferContentType(ext: String) = {
    contentType = ext match {
      case "js" => js
      case "coffee" => js
      case "jst" => js
      case "mustache" => js
      case "css" => css
      case "less" => css
      case _ => "text/plain"
    }
  }
}