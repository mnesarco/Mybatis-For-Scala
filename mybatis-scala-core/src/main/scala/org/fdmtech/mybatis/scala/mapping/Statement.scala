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

package org.fdmtech.mybatis.scala.mapping

import org.apache.ibatis.mapping.ResultSetType
import org.apache.ibatis.mapping.StatementType
import org.apache.ibatis.session.SqlSession
import scala.xml._
import org.apache.ibatis.session.RowBounds
import java.util.{List => JList}

abstract class Statement[P](statementType : StatementType, timeout : Int, flushCache : Boolean, databaseId : String)
  (implicit Pm : Manifest[P]) {

  var sql : Node = _

  def getStatementId = this.getClass.getName

  def getStatementType = {
    statementType.toString
  }

  def getParameterType = {
    Pm.erasure.getName match {
      case "void" => null
      case other => other
    }
  }

  def getTimeout = {
    if (timeout == 0) null else timeout.toString
  }

  def getFlushCache = flushCache.toString

  def getDatabaseId = databaseId

  def ::(n : Node) : this.type = {
    sql = n
    this
  }

}

/**
 * Mybatis select method
 * @type P Parameter type
 * @type R Return type
 */
abstract class Select[P <: AnyRef, R](
  resultMap : ResultMap = null,
  resultSetType : ResultSetType = ResultSetType.FORWARD_ONLY,
  statementType : StatementType = StatementType.PREPARED,
  fetchSize : Int = 0,
  timeout : Int = 0,
  flushCache : Boolean = false,
  useCache : Boolean = true,
  databaseId : String = null)
  (implicit Pm : Manifest[P], Rm : Manifest[R])
  extends Statement[P](statementType, timeout, flushCache, databaseId) {

  def apply(p : P = null.asInstanceOf[P], bounds : RowBounds = RowBounds.DEFAULT)(implicit s : SqlSession) : R = {
    import scala.collection.JavaConversions._
    p match {
      case param : P =>
        // Scala List case
        if (Rm.erasure.isAssignableFrom(classOf[List[_]])) {
          s.selectList(getStatementId, param, bounds).toList.asInstanceOf[R]
        }
        // java.util.List case
        else if (Rm.erasure.isAssignableFrom(classOf[JList[_]])) {
          s.selectList(getStatementId, param, bounds).asInstanceOf[R]
        }
        // No list case
        else {
          s.selectOne(getStatementId, param).asInstanceOf[R]
        }
      case null =>
        // Scala List case
        if (Rm.erasure.isAssignableFrom(classOf[List[_]])) {
          s.selectList(getStatementId, bounds).toList.asInstanceOf[R]
        }
        // java.util.List case
        else if (Rm.erasure.isAssignableFrom(classOf[JList[_]])) {
          s.selectList(getStatementId, bounds).asInstanceOf[R]
        }
        // No list case
        else {
          s.selectOne(getStatementId).asInstanceOf[R]
        }
    }
  }

  def getResultType = {
    if (resultMap == null) {
      // Scala List case
      if (Rm.erasure.isAssignableFrom(classOf[List[_]])) {
        Rm.typeArguments(0).erasure.getName
      }
      // java.util.List case
      else if (Rm.erasure.isAssignableFrom(classOf[JList[_]])) {
        Rm.typeArguments(0).erasure.getName
      }
      // No list case
      else {
        Rm.erasure.getName
      }
    }
    else null
  }

  def getResultSetType = {
    resultSetType.toString
  }

  def getFetchSize = {
    if (fetchSize == 0) null else fetchSize.toString
  }

  def getUseCache = useCache.toString

  def getResultMapId = {
    if (resultMap == null) null else resultMap.getClass.getName
  }

  def getResultMap = resultMap

}

/**
 * Mybatis update method
 * @type P Parameter type
 */
abstract class Update[P](
  statementType : StatementType = StatementType.PREPARED,
  timeout : Int = 0,
  flushCache : Boolean = true,
  databaseId : String = null)
  (implicit Pm : Manifest[P])
  extends Statement[P](statementType, timeout, flushCache, databaseId) {

  def apply(param : P)(implicit s : SqlSession) : Int = {
    s.update(getStatementId, param)
  }

  def apply()(implicit s : SqlSession) : Int = {
    s.update(getStatementId)
  }

}

/**
 * Mybatis delete method
 * @type P Parameter type
 */
abstract class Delete[P](
  statementType : StatementType = StatementType.PREPARED,
  timeout : Int = 0,
  flushCache : Boolean = true,
  databaseId : String = null)
  (implicit Pm : Manifest[P])
  extends Statement[P](statementType, timeout, flushCache, databaseId) {

  def apply(param : P)(implicit s : SqlSession) : Int = {
    s.delete(getStatementId, param)
  }

  def apply()(implicit s : SqlSession) : Int = {
    s.delete(getStatementId)
  }

}

/**
 * Mybatis insert method
 * @type P Parameter type
 */
abstract class Insert[P](
  statementType : StatementType = StatementType.PREPARED,
  timeout : Int = 0,
  flushCache : Boolean = true,
  keyProperty : String = null,
  keyColumn : String = null,
  useGeneratedKeys : Boolean = false,
  databaseId : String = null)
  (implicit Pm : Manifest[P])
  extends Statement[P](statementType, timeout, flushCache, databaseId) {

  def apply(param : P)(implicit s : SqlSession) : Int = {
    s.insert(getStatementId, param)
  }

  def apply()(implicit s : SqlSession) : Int = {
    s.insert(getStatementId)
  }

  def getUseGeneratedKeys = useGeneratedKeys.toString

  def getKeyProperty = keyProperty

  def getKeyColumn = keyColumn

}
