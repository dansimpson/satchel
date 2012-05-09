package org.ds.satchel.util

import java.io.File
import scala.io.Source

class RichFile(val file: File) {
 
  def children:Iterable[File] = {
	if(file.isDirectory) {
	  return file.listFiles
	}
	Array[File]()
  }
 
  def tree : Iterable[File] = {
    (Seq(file) ++ children.flatMap(child => new RichFile(child).tree))
  }
  
}


object FileUtil {

  def eglob(path:String):List[String] = {
        
    val base = path.replace(seperator * 2, seperator)
    val root = slap(base.split(seperator)) 

    val regex = base
    	.replace("**/", """[[\w\d\-\_]+""" + seperator + "]~~~")
    	.replace("*.", """.+\.""")
    	.replace("*", ".+")
    	.replace("~~~", "*").r

    val result = new RichFile(new File(root)).tree.map(_.toString)
    
    result.filter { p =>
      val itr = regex.findAllIn(p.replace('\\','/'))
      itr.size > 0
    }.toList.reverse
  }
  
  private def seperator = "/"
    
  private def slap(parts:Array[String]) = {
    val pure = parts.takeWhile { s =>
      !s.contains("*")
    }
    pure.reduceLeft(_ + seperator + _)
  }
  
}