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

import org.fdmtech.mybatis.scala.samples.model._
import org.fdmtech.mybatis.scala.mapping._

object PersonResultMap extends ResultMap(classOf[Person]) {

  id(
    property="id",
    column="id_"
  )

  result(
    property="firstName",
    column="first_name_"
  )

  result(
    property="lastName",
    column="last_name_"
  )

  association(
    property="group",
    column="group_id_",
    javaType=classOf[Group],
    resultMap = new ResultMap(classOf[Group]) {
      id (property="id", column="group_id_")
      result (property="name", column="group_name_")
    }
  )

  collection(
    property="contactInfo",
    column="contact_id_",
    ofType=classOf[ContactInfo],
    resultMap = new ResultMap(classOf[ContactInfo]) {
      id (property="id", column="contact_id_")
      result (property="streetAddress", column="contact_street_")
      result (property="phoneNumber", column="contact_phone_")
    }
  )

}
