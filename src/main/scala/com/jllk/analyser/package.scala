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
package com.jllk

import java.io.{Closeable, IOException}

/**
  * @author chentaov5@gmail.com
  *
  */
package object analyser {

  def inSafe[A](input: Closeable)(fun: => A): A = {
    require(input != null)
    try {
      fun
    } finally {
      try {
        input.close()
      } catch {
        case e: IOException => println(s"IOException happened! ${e.getMessage}")
      }
    }
  }

  def isEmpty(str: String): Boolean = str match {
    case null | "" => true
    case _ => false
  }

  def remove[A](list: List[A], index: Int) = {
    val (start, _ :: end) = list.splitAt(index)
    start ::: end
  }

  def notCareClass(fullClassName: String): Boolean =
    fullClassName.startsWith("java") ||
    fullClassName.startsWith("\"[Ljava") ||
   (fullClassName.startsWith("android") && !fullClassName.startsWith("android.support"))
}
