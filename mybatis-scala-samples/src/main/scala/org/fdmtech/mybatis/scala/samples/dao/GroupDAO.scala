/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fdmtech.mybatis.scala.samples.dao

import org.fdmtech.mybatis.scala.mapping._
import org.fdmtech.mybatis.scala.samples.model._

object GroupDAO extends Mapper {

  def bind = List(insert,deleteAll)

  def insert = new Insert[Group](keyProperty="id", keyColumn="id_", useGeneratedKeys=true) {
    sql =
      <sql>
        INSERT INTO people_group(name_) VALUES (#{{name}})
      </sql>
  }

  def deleteAll = new Delete[Nothing] {
    sql =
      <sql>
        DELETE FROM people_group
      </sql>
  }

}
