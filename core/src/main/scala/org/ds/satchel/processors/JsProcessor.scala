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

import com.yahoo.platform.yui.compressor._
import org.mozilla.javascript._

import java.io._

class JsProcessor extends SatchelProcessor {

  val reporter = new ErrorReporter {
    def error(message: String, sourceName: String, line: Int, lineSource: String, lineOffset: Int) {

      if (line < 0) {
        //logger_.warn("[MINIMIZOR.WARNING] " + message);
      } else {
        //logger_.warn(String.format("[MINIMIZOR.WARNING] %1$s: %2$s: %3$s", line, lineOffset, message));
      }
    }
    def runtimeError(message: String, sourceName: String, line: Int, lineSource: String, lineOffset: Int): EvaluatorException = { null }
    def warning(message: String, sourceName: String, line: Int, lineSource: String, lineOffset: Int) {}
  }

  override def process(content: String): String = {

    val reader = new StringReader(content)
    val writer = new StringWriter()

    new JavaScriptCompressor(reader, reporter)
      .compress(writer, -1, true, false, true, false)

    reader.close
    writer.close

    writer.toString
  }

}