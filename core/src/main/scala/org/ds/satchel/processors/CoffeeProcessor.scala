/**
 ---------------------------------------------------------------------------

 Copyright (c) 2011 Dan Simpson

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in
 all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 THE SOFTWARE.

 ---------------------------------------------------------------------------
 **/

package org.ds.satchel.processors

import java.io._
import org.mozilla.javascript._
import scala.sys.process._

//TODO: Log errors
class CoffeeProcessor extends SatchelProcessor with RhinoSupport {
  lazy val global = interpret("coffee-script.js")

  lazy val hasNative = {
    try {
     ("coffee -v " !) == 0
    } catch {
      case e: IOException => false
    }
  }

  override def process(content: String): String = {
    if(hasNative) {
      processNative(content)
    } else {
      processRhino(content)
    }
  }
  def processNative(content: String): String = {
    val tempFile = File.createTempFile("temp", ".coffee")
    val fileOut = new FileOutputStream(tempFile)
    fileOut.write(content.getBytes("UTF-8"))

    val out = new StringBuilder
    val err = new StringBuilder
    val logger = ProcessLogger((o: String) => out.append(o), (e: String) => err.append(e))

    val result = ("coffee -cp " + tempFile.getPath ! logger)
    tempFile.delete()
    if(result != 0) {
      sys.error(err.toString())
    }
    out.toString
  }

  def processRhino(content: String): String = {
    val context = Context.enter
    context.setOptimizationLevel(-1)
    context.setLanguageVersion(Context.VERSION_1_6)
    val scope = context.newObject(global)
    scope.setParentScope(global)
    scope.put("source", scope, content)
    var result = context.evaluateString(scope, "CoffeeScript.compile(source);", "coffee.js", 0, null).toString
    Context.exit
    result.replaceAll("\\\\n", "\n")
  }

}
