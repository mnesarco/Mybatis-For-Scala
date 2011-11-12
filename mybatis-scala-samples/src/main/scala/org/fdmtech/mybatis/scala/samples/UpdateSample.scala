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

object UpdateSample {

  // Simple Person POJO
  class Person {
    var id : Int = _
    var firstName : String = _
    var lastName : String = _
  }

  // Simple update method
  val updatePerson = new Update[Person] {
    defined as
      <xsql>
        UPDATE person
        SET
          first_name_ = #{{firstName}},
          last_name_ = #{{lastName}}
        WHERE
          id_ = #{{id}}
      </xsql>
  }

  // Simple select method
  val findPerson = new SelectOne[Int,Person] {
    defined as
      <xsql>
        SELECT id_ as id, first_name_ as firstName, last_name_ as lastName
        FROM person
        WHERE id_ = #{{id}}
      </xsql>
  }


  // Load datasource configuration
  val config = Configuration("mybatis.xml")

  // Create a configuration space, add the data access method
  config.addSpace("ns1") { space =>
    space << updatePerson << findPerson
  }

  // Build the session manager
  val db = config.build

  // Do the Magic ...
  def main(args : Array[String]) : Unit = {

    db.transaction { implicit session =>

      var p = findPerson(1)
      println( "Before> Person(%d): %s %s".format(p.id, p.firstName, p.lastName) )

      p.firstName = "Sun (Modified " + java.lang.System.currentTimeMillis + ")"
      updatePerson(p)

      // Reload to verify
      p = findPerson(1)
      println( "After> Person(%d): %s %s".format(p.id, p.firstName, p.lastName) )

    }

  }


}
