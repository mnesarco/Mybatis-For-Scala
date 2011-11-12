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

package org.fdmtech.mybatis.scala

trait Statement {

  var fqi             : FQI = null
  var statementType   : StatementType = StatementType.PREPARED
  var timeout         : Int = -1
  var flushCache      : Boolean = true
  var databaseId      : String = null
  val defined         : DynamicSQL = new DynamicSQL

}

trait Select extends Statement {

  var resultMap       : ResultMap[_] = null
  var resultSetType   : ResultSetType = ResultSetType.FORWARD_ONLY
  var fetchSize       : Int = -1
  var useCache        : Boolean = true

}

class SelectOne[Parameter : Manifest, Result : Manifest] extends Select {

  val parameterType = Type[Parameter]
  val resultType = Type[Result]

  flushCache = false

  def apply(param : Parameter)(implicit s : Session) : Result = {
    s.selectOne(fqi.absoluteId, param).asInstanceOf[Result]
  }

  def apply()(implicit s : Session) : Result = {
    s.selectOne(fqi.absoluteId).asInstanceOf[Result]
  }

}

class SelectList[Parameter : Manifest, Result : Manifest] extends Select {

  val parameterType = Type[Parameter]
  val resultType = Type[Result]
  val NO_PARAM = null.asInstanceOf[Parameter]

  flushCache = false

  def apply(param : Parameter = NO_PARAM, bounds : RowBounds = DefaultRowBounds)(implicit s : Session) : List[Result] = {
    import scala.collection.JavaConversions._
    s.selectList(fqi.absoluteId, param, bounds).toList.asInstanceOf[List[Result]]
  }

}

class SelectMap[Parameter : Manifest, Result : Manifest, Key] extends Select {

  val parameterType = Type[Parameter]
  val resultType = Type[Result]
  val NO_PARAM = null.asInstanceOf[Parameter]
  var keyProperty : String = null

  flushCache = false

  def apply(param : Parameter = NO_PARAM, bounds : RowBounds = DefaultRowBounds)(implicit s : Session) : Map[Key,Result] = {
    import scala.collection.JavaConversions._
    s.selectMap(fqi.absoluteId, param, keyProperty, bounds).toMap.asInstanceOf[Map[Key,Result]]
  }

}

class Update[Parameter : Manifest] extends Statement {

  val parameterType = Type[Parameter]
  val NO_PARAM = null.asInstanceOf[Parameter]

  def apply(param : Parameter = NO_PARAM)(implicit s : Session) : Int = {
    s.update(fqi.absoluteId, param)
  }

}

class Insert[Parameter : Manifest] extends Statement {

  val parameterType = Type[Parameter]
  val NO_PARAM = null.asInstanceOf[Parameter]
  var keyProperty : String = null
  var keyColumn : String = null
  var useGeneratedKeys : Boolean = true

  def apply(param : Parameter = NO_PARAM)(implicit s : Session) : Int = {
    s.insert(fqi.absoluteId, param)
  }

}

class Delete[Parameter : Manifest] extends Statement {

  val parameterType = Type[Parameter]
  val NO_PARAM = null.asInstanceOf[Parameter]

  def apply(param : Parameter = NO_PARAM)(implicit s : Session) : Int = {
    s.delete(fqi.absoluteId, param)
  }

}