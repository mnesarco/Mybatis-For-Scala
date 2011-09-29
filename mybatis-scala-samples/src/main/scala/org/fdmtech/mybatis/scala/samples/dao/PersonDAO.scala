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

package org.fdmtech.mybatis.scala.samples.dao

import org.fdmtech.mybatis.scala.mapping._
import org.fdmtech.mybatis.scala.samples.model._

object PersonDAO extends Mapper {

  def bind = List(insert,find,deleteAll)

  def insert = new Insert[Person] {
    sql =
      <sql>
        INSERT INTO person(first_name_, last_name_, group_id_)
        VALUES(#{{firstName}}, #{{lastName}}, #{{group.id}})
      </sql>
  }

  def find = new Select[String,List[Person]](resultMap=PersonResultMap) {
    sql =
      <sql>
        SELECT
          p.id_, p.first_name_, p.last_name_, p.group_id_,
          g.name_ as group_name_,
          c.id_ as contact_id_, c.street_address_ as contact_street_, c.phone_number_ as contact_phone_
        FROM
          person p
            LEFT JOIN contact_info c ON c.owner_id_ = p.id_
            LEFT JOIN people_group g ON p.group_id_ = g.id_
        WHERE
          upper(p.last_name_) LIKE upper(#{{name}})
      </sql>
  }

  def deleteAll = new Delete[Nothing] {
    sql =
      <sql>
        DELETE FROM person
      </sql>
  }

}
