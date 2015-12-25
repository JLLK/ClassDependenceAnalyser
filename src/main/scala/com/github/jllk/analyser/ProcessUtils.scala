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

import java.io._

/**
  * @author chentaov5@gmail.com
  *
  */
object ProcessUtils {

  def exec(cmd: String): String = {
    try {
      val fos = new ByteArrayOutputStream
      val rt = Runtime.getRuntime
      val proc = rt.exec(cmd)
      val errorGobbler = new StreamGobbler(proc.getErrorStream(), "ERROR")
      val outputGobbler = new StreamGobbler(proc.getInputStream(), "OUTPUT", fos)
      errorGobbler.start()
      outputGobbler.start()
      val exitVal = proc.waitFor
      System.out.println(s"ExitValue: $exitVal")
      fos.flush()
      fos.close()
      new String(fos.toByteArray)
    }
    catch {
      case t: Throwable => {
        t.printStackTrace()
      }
    }
    ""
  }
}

class StreamGobbler(private val is: InputStream, private val ty: String, private val os: OutputStream) extends Thread {

  def this(is: InputStream, ty: String)  = this(is, ty, null)

  override def run() {
    try {
      var pw: PrintWriter = null
      if (os != null) {
        pw = new PrintWriter(os)
      }
      val isr = new InputStreamReader(is)
      val br = new BufferedReader(isr)
      var line: String = null
      while ( {
        line = br.readLine
        line != null
      }) {
        if (pw != null) {
          pw.println(line)
        }
      }
      if (pw != null) pw.flush()
    }
    catch {
      case ioe: IOException => {
        ioe.printStackTrace()
      }
    }
  }
}
