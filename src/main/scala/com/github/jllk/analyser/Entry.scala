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
package com.github.jllk.analyser

import java.io.File

import com.android.multidex.JLLKClassReferenceListBuilder


/**
  * @author chentaov5@gmail.com
  *
  */
object Entry extends App {

  handleArgs(args)

  def handleArgs(args: Array[String]) = {
    if (args == null || args.length <= 0) {
      handleError(args)
    } else {
      args(0) match {
        case "--external" | "-e" => execClassAnalysisV2(args)
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

  def execClassAnalysisV2(args: Array[String]): Unit = {
    val in = remove(args.toList, 0)
    JLLKClassReferenceListBuilder.main(in.toArray)
  }

  def execClassAnalysis(args: Array[String]) = {
    if (args.length < 2) {
      handleError(args)
    } else {
      val argsList = args.toList
      val argsLen = args.length
      val fullClassName = argsList(1)
      val targetClassPath = new File(argsList(1))
      val dependenceJarPath = remove(remove(argsList, 0), 0).map(arg => new File(arg))
      println(s"tom targetClassPath: $targetClassPath, dependenceJarPath: $dependenceJarPath")
      val analyser = new Analyser(dependenceJarPath)
      val result = analyser.analysis(fullClassName)
      IOUtils.writeToMainDexList(result.filterNot(Analyser.notCareClass))
    }
  }

  def handleError(args: Array[String]) = {
    args match {
      case null => showHelpInfo()
      case _    => println("error in args, please check again,  --help for more info.")
    }
  }

  def showHelpInfo() = {
    println("Usage: jllk-cda [arguments] targetClass [dependenceJarPath]...")
    println("find and print the input class dependence.")
    println("Example: jllk-cda -c,yourFQCN,/your_path/android.jar")
    println("         jllk-cda -e,/your_path/rootclass.jar,/your_path/allclasses.jar")
    println("")
    println("The arguments are:")
    println("")
    println("   -c, --class    analysis class dependence from single class file or from jar path")
    println("   -e, --external analysis class dependence external from <clinit> and innerClass")
    println("   -h, --help     print jllk-cda help info")
    println("   -v, --version  print jllk-cda version")
    println("")
  }

  def showVersionInfo() = {
    println("===========================================================")
    println("  ClassDependenceAnalyser 1.0.0 <chentaov5@gmail.com>")
    println("===========================================================")
  }
}
