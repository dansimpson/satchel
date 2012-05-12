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

/**
 * Base class for bundle processors.
 * 
 
 * 
 * The responsibility of the bundle processor is to process
 * individual items in the bundle, as well as the entire
 * concatenated bundle itself.
 */
abstract class SatchelProcessor {
  
	/**
	 * Process the entire concatenated file
	 * Useful for compression, validation, etc
	 */
	def process(content:String):String = content
	
	/**
	 * Process an individual file.  Useful for wrapping
	 * the content with a function, or for wrapping
	 * template files with js variables.
	 */
	def process(path:String, content:String):String = content
}