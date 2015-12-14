/*
 * ClassDependenceAnalyser - A tool for java classes dependence analysis
 * Copyright (C) 2016 <chentaov5@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 *
 *                            ___====-_  _-====___
 *                      _--^^^#####//      \\#####^^^--_
 *                   _-^##########// (    ) \\##########^-_
 *                  -############//  |\^^/|  \\############-
 *                _/############//   (@::@)   \\############\_
 *               /#############((     \\//     ))#############\
 *              -###############\\    (oo)    //###############-
 *             -#################\\  / VV \  //#################-
 *            -###################\\/      \//###################-
 *           _#/|##########/\######(   /\   )######/\##########|\#_
 *           |/ |#/\#/\#/\/  \#/\##\  |  |  /##/\#/  \/\#/\#/\#| \|
 *           `  |/  V  V  `   V  \#\| |  | |/#/  V   '  V  V  \|  '
 *              `   `  `      `   / | |  | | \   '      '  '   '
 *                               (  | |  | |  )
 *                              __\ | |  | | /__
 *                             (vvv(VVV)(VVV)vvv)
 *
 *                              HERE BE DRAGONS
 *
 */
package com.jllk.analyser

import java.io.File
import java.net.{URLClassLoader, URL}

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

/**
  * @author chentaov5@gmail.com
  *
  */
object Analyser {
  val MODE_CLASS_PATH = 0x00
  val MODE_JAR_PATH   = 0x01
}

class Analyser(private val mode: Int, private val path: File, private val dependenceJarPath: List[File]) {
  import Analyser._

  def analysis(fullClassName: String): mutable.Set[String] = {
    print(s"[analysis] className: $fullClassName")
    val dependentClasses = mutable.Set[String]()
    val importDependence = analysisImportDependence(fullClassName)
    importDependence.foreach(e => {
      dependentClasses += e
      dependentClasses ++ analysisInheritDependence(e)
    })
    dependentClasses
  }

  private def analysisImportDependence(fullClassName: String): List[String] = {
    print(s"analysisImportDependence className: $fullClassName")
    val dependentClasses = ListBuffer[String]()
    val classReport = mode match {
      case MODE_CLASS_PATH => ProcessUtils.exec(s"javap -verbose $path ${fullClassName.replace('.', '/')}")
      case MODE_JAR_PATH   => ProcessUtils.exec(s"javap -verbose -classpath $path ${fullClassName.replace('.', '/')}")
    }
    val lines = classReport.split('\n')
    lines
    .withFilter(l => l.contains("= Class") && l.contains("\"[Ljava/lang/Object;\""))
    .foreach(l => dependentClasses += l.substring(l.indexOf("//") + 2).replaceAll(" ", "").replaceAll("/", "\\.").trim())
    dependentClasses.toList
  }

  private def analysisInheritDependence(fullClassName: String): List[String] = {
    print(s"[analysisInheritDependence] className: $fullClassName")
    val urls = ListBuffer[URL]()
    urls += path.toURI.toURL
    dependenceJarPath.foreach(f => urls += f.toURI.toURL)
    val classLoader = new URLClassLoader(urls.toArray)
    doClassInheritSearch(fullClassName, classLoader)
  }

  private def doClassInheritSearch(fullClassName: String, classLoader: URLClassLoader): List[String] = {
    print(s"[doClassInheritSearch] className: $fullClassName")
    val dependentClasses = ListBuffer[String]()
    dependentClasses += fullClassName
    val targetClass = classLoader.loadClass(fullClassName)
    val superclass = targetClass.getSuperclass
    if (superclass != null) {
      dependentClasses ++ doClassInheritSearch(superclass.getName, classLoader)
    }
    targetClass.getInterfaces.foreach(i => dependentClasses ++ doClassInheritSearch(i.getName, classLoader))
    dependentClasses.toList
  }
}
