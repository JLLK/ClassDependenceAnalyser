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

/**
  * @author chentaov5@gmail.com
  *
  */
object Entry extends App {

  printArgs(args)
  handleArgs(args)

  def handleArgs(args: Array[String]) = {
    if (args == null || args.length <= 0) {
      handleError(args)
    } else {
      args(0) match {
        case "--class"    | "-c" => execClassAnalysis(args)
        case "--help"     | "-h" => showHelpInfo()
        case "--version"  | "-v" => showVersionInfo()
        case _                   => handleError(args)
      }
    }
  }

  def printArgs(args: Array[String]) = {
    println(s"args len: ${args.length}")
    if (args.length > 0) {
      var i = 0
      args.foreach(arg => {
        println(s"args($i): $arg")
        i = i + 1
      })
    }
  }

  def execClassAnalysis(args: Array[String]) = {
    if (args.length < 2) {
      handleError(args)
    } else {
      val argsList = args.toList
      val argsLen = args.length
      val fullClassName = argsList(argsLen - 1)
      val targetClassPath = new File(argsList(1))
      val dependenceJarPath = remove(remove(remove(argsList, argsLen -1), 0), 0)
        .map(arg => new File(arg))
      val analyser = new Analyser(targetClassPath, dependenceJarPath)
      val result = analyser.analysis(fullClassName)
      IOUtils.writeToMainDexList(result.filterNot(notCareClass))
    }
  }

  def handleError(args: Array[String]) = {
    args match {
      case null => showHelpInfo()
      case _    => println("error in args, please check again,  --help for more info.")
    }
  }

  def showHelpInfo() = {
    println("Usage: jda [arguments] targetClassPath [dependenceJarPath]... fullClassName")
    println("find and print the input class dependence.")
    println("Example: jda -c /workspace/build/android.jar android.app.Application")
    println("")
    println("The arguments are:")
    println("")
    println("   -c, --class    analysis class dependence from single class file or from jar path")
    println("   -h, --help     print jda help info")
    println("   -v, --version  print jda version")
    println("")
  }

  def showVersionInfo() = {
    println("===========================================================")
    println("  ClassDependenceAnalyser 0.5beta <chentaov5@gmail.com>")
    println("===========================================================")
  }
}
