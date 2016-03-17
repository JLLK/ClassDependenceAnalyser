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

import java.util

import com.android.multidex.JLLKClassReferenceListBuilder
import org.objectweb.asm.ClassReader
import org.objectweb.asm.tree.{ClassNode, InnerClassNode, MethodInsnNode, MethodNode}

import scala.collection.JavaConversions._

/**
  * @author chentaov5@gmail.com
  *
  */
object AnalyserV2 {
  val METHOD_CLINIT = "<clinit>"

  @throws[Exception]
  def anylsisClinitDependence(toKeep: util.Set[String], fullClassName: String) {
    if (!toKeep.contains(fullClassName) && !Analyser.notCareClass(fullClassName)) {
      val classReader = new ClassReader(fullClassName)
      val classNode = new ClassNode()
      classReader.accept(classNode, 0)
      classNode.methods.asInstanceOf[util.List[MethodNode]].foreach(m => {
        if (METHOD_CLINIT.equals(m.name)) {
          val it = m.instructions.iterator()
          while (it.hasNext) {
            it.next() match {
              case insn: MethodInsnNode =>
                println(s"[DO ADD in clint] ${insn.owner}")
                JLLKClassReferenceListBuilder.getDefault.addClassWithHierachy(insn.owner)
              case _ =>
            }
          }
        }
      })
    }
  }

  @throws[Exception]
  def anylsisInnerClassDependence(toKeep: util.Set[String], fullClassName: String) {
    if (!toKeep.contains(fullClassName) && !Analyser.notCareClass(fullClassName)) {
      val classReader = new ClassReader(fullClassName)
      val classNode = new ClassNode()
      classReader.accept(classNode, 0)
      classNode.innerClasses.asInstanceOf[util.List[InnerClassNode]].foreach(c => {
        println(s"[DO ADD in innerClass] ${c.name}")
        JLLKClassReferenceListBuilder.getDefault.addClassWithHierachy(c.name)
      })
    }
  }

  @throws[Exception]
  def anylsisExtDependence(toKeep: util.Set[String], fullClassName: String) {
    if (!toKeep.contains(fullClassName) && !Analyser.notCareClass(fullClassName)) {
      anylsisClinitDependence(toKeep, fullClassName)
      anylsisInnerClassDependence(toKeep, fullClassName)
    }
  }
}
