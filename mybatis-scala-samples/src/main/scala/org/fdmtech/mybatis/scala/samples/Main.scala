/*
 * Copyright 2011 Frank David Martinez M.
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
import org.fdmtech.mybatis.scala.builder._
import org.fdmtech.mybatis.scala.mapping._
import org.fdmtech.mybatis.scala.session._
import org.fdmtech.mybatis.scala.samples.dao._
import org.fdmtech.mybatis.scala.samples.model._

object Main {

  def main(args : Array[String]) : Unit = {

    // Create a session factory builder ...
    val builder = new PersistenceSessionFactoryBuilder

    // Load main configuration from xml (datasource ...) ....
    builder config "mybatis.xml"

    // Add mappers
    builder << PersonDAO << GroupDAO

    // Build a factory
    val factory = builder.build

    // Do the magic ...
    factory.createSession.transaction({implicit session =>

      // Cleanup
      PersonDAO.deleteAll()
      GroupDAO.deleteAll()

      // Create some people groups
      val groups = for (name <- List("Customers", "Suppliers", "Employees")) yield {
        val g = new Group
        g.name = name
        GroupDAO.insert(g)
        g
      }

      // Create some people
      val names = List(("Andrew", "Smith"),("Mary", "Anderson"),("John", "Doe"),("Peter", "Gabriel"))
      for (name <- names) name match {
        case (first, last) =>
          val person = new Person
          person.firstName = first
          person.lastName = last
          person.group = groups.head
          PersonDAO.insert(person)
      }

      // Find some people
      for (p <- PersonDAO.find("%a%"))
        println( "Person(%d): %s, %s is in group %s".format(p.id, p.lastName, p.firstName, p.group.name) )

    })


  }

}
