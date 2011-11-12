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

object InsertSample {

  // Simple Group POJO
  class Group {
    var id : Int = _
    var name : String = _
  }

  // Simple Person POJO
  class Person {
    var id : Int = _
    var firstName : String = _
    var lastName : String = _
    var group : Group = _
  }

  // Simple insert method
  val insertPerson = new Insert[Person] {
    keyProperty = "id"
    defined as
      <xsql>
        INSERT INTO person (first_name_, last_name_, group_id_)
        VALUES (#{{firstName}}, #{{lastName}}, #{{group.id}})
      </xsql>
  }

  // Simple insert method
  val insertGroup = new Insert[Group] {
    keyProperty = "id"
    defined as
      <xsql>
        INSERT INTO people_group (name_)
        VALUES (#{{name}})
      </xsql>
  }

  // Load datasource configuration
  val config = Configuration("mybatis.xml")

  // Create a configuration space, add the data access method
  config.addSpace("ns1") { space =>
    space << insertPerson
    space << insertGroup
  }

  // Build the session manager
  val db = config.build

  // Do the Magic ...
  def main(args : Array[String]) : Unit = {

    db.transaction { implicit session =>

      val g = new Group
      g.name = "New Group"

      val p = new Person
      p.firstName = "John"
      p.lastName = "Smith"
      p.group = g

      insertGroup(g)
      insertPerson(p)

      println( "Inserted Person(%d): %s %s".format(p.id, p.firstName, p.lastName) )

    }

  }


}
