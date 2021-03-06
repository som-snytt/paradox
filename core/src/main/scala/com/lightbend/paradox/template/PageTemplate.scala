/*
 * Copyright © 2015 - 2016 Lightbend, Inc. <http://www.lightbend.com>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lightbend.paradox.template

import java.io.File
import java.util.{ Map => JMap }
import org.stringtemplate.v4.misc.STMessage
import org.stringtemplate.v4.{ STErrorListener, STRawGroupDir }

/**
 * Page template writer.
 */
class PageTemplate(directory: File, startDelimiter: Char = '$', stopDelimiter: Char = '$', name: String = "page") {

  private val templates = new STRawGroupDir(directory.getAbsolutePath, startDelimiter, stopDelimiter)

  /**
   * Write a templated page to the target file.
   */
  def write(contents: PageTemplate.Contents, target: File, errorListener: STErrorListener): File = {
    val template = templates.getInstanceOf(name).add("page", contents)
    template.write(target, errorListener)
    target
  }

}

object PageTemplate {
  /**
   * All page information to give to the template.
   */
  trait Contents {
    def getTitle: String
    def getContent: String
    def getBase: String
    def getHome: Link
    def getPrev: Link
    def getNext: Link
    def getBreadcrumbs: String
    def getNavigation: String
    def hasSubheaders: Boolean
    def getToc: String
    def getProperties: JMap[String, String]
  }

  /**
   * Page link. Can be rendered as just the href or full HTML.
   */
  trait Link {
    def getHref: String
    def getHtml: String
    def getTitle: String
    def isActive: Boolean
  }

  /**
   * Error listener wrapper.
   */
  class ErrorLogger(error: String => Unit) extends STErrorListener {
    override def compileTimeError(stm: STMessage): Unit = error(stm.toString)
    override def runTimeError(stm: STMessage): Unit = error(stm.toString)
    override def IOError(stm: STMessage): Unit = error(stm.toString)
    override def internalError(stm: STMessage): Unit = error(stm.toString)
  }
}
