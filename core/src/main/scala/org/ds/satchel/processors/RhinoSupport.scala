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

//TODO: Log
trait RhinoSupport {
    
  protected def interpret(name:String): ScriptableObject = {

    val stream = getClass().getClassLoader().getResourceAsStream(name)
    val reader = new InputStreamReader(stream, "UTF-8")
    val context = Context.enter
    var scope = context.initStandardObjects
    
    context.setOptimizationLevel(-1)
   	context.setLanguageVersion(Context.VERSION_1_5)
    context.evaluateReader(scope, reader, name, 0, null)
    
    Context.exit
    reader.close
    stream.close

    scope
  }
  
}