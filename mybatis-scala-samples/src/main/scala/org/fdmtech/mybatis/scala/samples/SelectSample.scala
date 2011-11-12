/*
 * Copyright 2011 Frank D. Martinez M. [mnesarco at gmail.com].
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.fdmtech.mybatis.scala.samples

import org.fdmtech.mybatis.scala._

object SelectSample {

  // Simple Person POJO
  class Person {
    var id : Int = _
    var firstName : String = _
    var lastName : String = _
  }

  // Simple select method
  val findAll = new SelectList[String,Person] {
    defined as
      <xsql>
        SELECT id_ as id, first_name_ as firstName, last_name_ as lastName
        FROM person
        WHERE first_name_ LIKE #{{name}}
      </xsql>
  }

  // Load datasource configuration
  val config = Configuration("mybatis.xml")

  // Create a configuration space, add the data access method
  config.addSpace("ns1") { space =>
    space << findAll
  }

  // Build the session manager
  val db = config.build

  // Do the Magic ...
  def main(args : Array[String]) : Unit = {

    db.readOnly { implicit session =>

      for (p <- findAll("%"))
        println( "Person(%d): %s %s".format(p.id, p.firstName, p.lastName) )

    }

  }


}
