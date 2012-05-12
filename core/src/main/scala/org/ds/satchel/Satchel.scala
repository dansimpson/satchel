/**
 * ---------------------------------------------------------------------------
 *
 * Copyright (c) 2011 Dan Simpson
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 * ---------------------------------------------------------------------------
 */

package org.ds.satchel

import org.ds.satchel.util.FileUtil

import java.io.{ ByteArrayOutputStream, FileInputStream, IOException, File }
import scala.util.control.Breaks._
import scala.xml.Elem
import java.util.concurrent.ConcurrentHashMap

//import grizzled.file.util
//import grizzled.file.GrizzledFile._
import org.ds.satchel.processors._

object Satchel {

  private val map = new ConcurrentHashMap[String, Satchel]
  private def register(satchel: Satchel) = {
    map.put(satchel.name, satchel)
  }
  
  /**
   * Development mode flag, disables compression and
   * caching when set to true.
   */
  var devmode = true
  
  /**
   * Find a satchel by name
   *
   * @param name	the name of the satchel
   * @return		the satchel object
   */
  def find(name: String) = map.get(name)

  /**
   * Determine if a satchel exists
   *
   * @param name	the name of the satchel
   * @return		true if the satchel exists
   */
  def exists(name: String) = map.containsKey(name)

  def apply(name: String) = { new Satchel(name) }

}

/**
 * Represents a concatenated collection of web files.
 *
 * @param name the name of the satchel
 *
 * @param cacheable is the satchel cacheable?  Greatly improves
 * performance
 *
 * @param root the directory path in which your files live.
 * This is used to help generate script tags, etc.
 *
 * @param mime The type of file it is, js, css, less, coffee, jst
 *
 * @param patterns The list of path patterns to match for finding files
 * to include in the satchel.
 *
 * @param processors Advanced.  Add a series of processors to the pipeline, to
 * apply mutative actions to the content of each file or the satchel in it's entirety.
 */
class Satchel(val name: String,
              val cacheable: Boolean = !Satchel.devmode,
              val compress: Boolean = !Satchel.devmode,
              val root: String = "",
              val mime: String = "js",
              var patterns: List[String] = List(),
              var processors: List[SatchelProcessor] = List()) {

  private var _cache: String = null

  /**
   * Auto add processors for certain types
   */
  mime match {
    case "jst"      => processors = processors :+ new JstProcessor
    case "ejs"      => processors = processors :+ new JstProcessor
    case "mustache" => processors = processors :+ new JstProcessor
    case "js" => {
      if (compress) {
        processors = processors :+ new JsProcessor
      }
    }
    case "coffee" => {
      processors = processors :+ new CoffeeProcessor
      if (compress) {
        processors = processors :+ new JsProcessor
      }
    }
    case "css" => {
      if (compress) {
        processors = processors :+ new CssProcessor
      }
    }
    case "less" => {
      processors = processors :+ new LessProcessor
      if (compress) {
        processors = processors :+ new CssProcessor
      }
    }
    case _ =>
  }
  processors = processors.removeDuplicates

  // register this satchel with the object
  // for retrieval
  Satchel.register(this)

  /**
   * Add a glob pattern to the search tree
   * @param pattern	an eglob pattern to find files for
   * 				inclusion
   */
  def add(pattern: String) = {
    if (!patterns.contains(pattern)) {
      patterns = patterns :+ pattern
    }
  }

  /**
   * Get the entire list of files in the satchel
   *
   * @return the list of files in the satchel
   */
  def list: List[String] = {
    glob(patterns)
  }

  /**
   * Get the entire list of files in the satchel, with
   * the root directory removed.
   *
   * @return the list of files in the satchel
   */
  def assetlist: List[String] = {
    list.map(filename(_))
  }

  /**
   * Purge the cache
   */
  def purge = _cache = null

  /**
   * List of include tags for a document
   *
   * @return XML tags to include in your DOM for development
   * purposes
   */
  def tags: List[Elem] = {
    list.map { src =>
      tag(filename(src))
    }
  }

  /**
   * Bundled include tag
   *
   * @return XML include tag for the bundle
   */
  def bundleTag: Elem = {
    tag("/bundles/%s.%s".format(name, mime))
  }

  /**
   * Combine all source files into a big string
   *
   * @return the concatenated, compiled, and compressed satchel
   * source code
   */
  def processed: String = {
    if (cacheable) {
      if (_cache == null) {
        _cache = process(generate)
      }
      return _cache
    }
    process(generate)
  }

  /**
   * Generate a script tag for a given path
   */
  private def tag(path: String): Elem = {
    <script src={ path } type="text/javascript"></script>
  }

  /**
   * Concatenate all files, processing each individually if needed
   */
  private def generate: String = {
    val contents = list.map { file =>
      val inStream = new FileInputStream(file)
      val outStream = new ByteArrayOutputStream
      try {
        var reading = true
        while (reading) {
          inStream.read() match {
            case -1 => reading = false
            case c  => outStream.write(c)
          }
        }
        outStream.flush()
      } finally {
        inStream.close()
      }

      process(filename(file), new String(outStream.toByteArray(), "UTF-8"))
    }

    contents.reduceLeft(_ + _)
  }

  /**
   * Process an individual file
   */
  private def process(path: String, content: String): String = {
    var result = content
    processors.foreach { p =>
      result = p.process(path, result)
    }
    result
  }

  /**
   * Process a concatenated result
   */
  private def process(content: String): String = {
    var result = content
    processors.foreach { p =>
      result = p.process(result)
    }
    result
  }

  /**
   * Strip the root from the file path
   */
  private def filename(path: String): String = {
    path.replace('\\','/').replaceFirst(root, "")
  }

  private def glob(patterns: List[String]): List[String] = {
    patterns.map { pattern: String =>
      FileUtil.eglob(root + "/" + pattern)
    }.flatten.removeDuplicates
  }
  
  

}