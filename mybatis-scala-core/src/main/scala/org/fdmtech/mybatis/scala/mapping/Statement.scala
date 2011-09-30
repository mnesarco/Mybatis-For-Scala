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

/** Base class for all MyBatis Methods (Select,Insert,Update,Delete)
 */
abstract class Statement[P](statementType : StatementType, timeout : Int, flushCache : Boolean, databaseId : String)
  (implicit Pm : Manifest[P]) {

  /** Mybatis dynamic SQL Node (XML)
   *  @see Mybatis User's guide'
   */
  var sql : Node = _

  /** Statement Id, a unique ID used by Mybatis to identify a mapped statement
   */
  def getStatementId = this.getClass.getName

  /** Any one of STATEMENT, PREPARED or CALLABLE.
   *  This causes MyBatis to use Statement, PreparedStatement or CallableStatement respectively. Default: PREPARED
   */
  def getStatementType = {
    statementType.toString
  }

  /** The fully qualified class name or alias for the parameter that will be passed into this statement.
   */
  def getParameterType = {
    Pm.erasure.getName match {
      case "void" => null
      case other => other
    }
  }

  /** The maximum time the driver will wait for the database to return from a request, before throwing an exception.
   *  Default is unset (driver dependent).
   */
  def getTimeout = {
    if (timeout == 0) null else timeout.toString
  }

  /** Setting this to true will cause the cache to be flushed whenever this statement is called.
   *  Default: false for select statements.
   */
  def getFlushCache = flushCache.toString

  /** Undocumented feature
   */
  def getDatabaseId = databaseId

}

/** Mybatis SELECT mapped statement
 *
 *  @tparam P The type for the parameter that will be passed into this statement.
 *  @tparam R The type for the expected type that will be returned from this statement.
 *  @param resultMap A reference to an external resultMap. Result maps are the most powerful feature of MyBatis, and with a good understanding of them, many difficult mapping cases can be solved.
 *  @param flushCache Setting this to true will cause the cache to be flushed whenever this statement is called. Default: false for select statements.
 *  @param useCache Setting this to true will cause the results of this statement to be cached. Default: true for select statements.
 *  @param timeout This sets the maximum time the driver will wait for the database to return from a request, before throwing an exception. Default is unset (driver dependent).
 *  @param fetchSize This is a driver hint that will attempt to cause the driver to return results in batches of rows numbering in size equal to this setting. Default is unset (driver dependent).
 *  @param statementType Any one of STATEMENT, PREPARED or CALLABLE. This causes MyBatis to use Statement, PreparedStatement or CallableStatement respectively. Default: PREPARED.
 *  @param resultSetType Any one of FORWARD_ONLY|SCROLL_SENSITIVE|SCROLL_INSENSITIVE. Default is unset (driver dependent).
 *  @param databaseId Undocumented feature
 */
abstract class Select[P, R](
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

  def apply()(implicit s : SqlSession) : R = apply(null.asInstanceOf[P], RowBounds.DEFAULT)

  def apply(p : P)(implicit s : SqlSession) : R = apply(p, RowBounds.DEFAULT)

  def apply(bounds : RowBounds)(implicit s : SqlSession) : R = apply(null.asInstanceOf[P], bounds)

  def apply(p : P, bounds : RowBounds)(implicit s : SqlSession) : R = {
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
      case _ =>
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

/** Mybatis UPDATE mapped statement
 *
 *  @tparam P The type for the parameter that will be passed into this statement.
 *  @param flushCache Setting this to true will cause the cache to be flushed whenever this statement is called. Default: false for select statements.
 *  @param timeout This sets the maximum time the driver will wait for the database to return from a request, before throwing an exception. Default is unset (driver dependent).
 *  @param statementType Any one of STATEMENT, PREPARED or CALLABLE. This causes MyBatis to use Statement, PreparedStatement or CallableStatement respectively. Default: PREPARED.
 *  @param databaseId Undocumented feature
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

/** Mybatis DELETE mapped statement
 *
 *  @tparam P The type for the parameter that will be passed into this statement.
 *  @param flushCache Setting this to true will cause the cache to be flushed whenever this statement is called. Default: false for select statements.
 *  @param timeout This sets the maximum time the driver will wait for the database to return from a request, before throwing an exception. Default is unset (driver dependent).
 *  @param statementType Any one of STATEMENT, PREPARED or CALLABLE. This causes MyBatis to use Statement, PreparedStatement or CallableStatement respectively. Default: PREPARED.
 *  @param databaseId Undocumented feature
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

/** Mybatis INSERT mapped statement
 *
 *  @tparam P The type for the parameter that will be passed into this statement.
 *  @param flushCache Setting this to true will cause the cache to be flushed whenever this statement is called.
 *    Default: false for select statements.
 *  @param timeout This sets the maximum time the driver will wait for the database to return from a request,
 *    before throwing an exception. Default is unset (driver dependent).
 *  @param statementType Any one of STATEMENT, PREPARED or CALLABLE. This causes MyBatis to use Statement,
 *    PreparedStatement or CallableStatement respectively. Default: PREPARED.
 *  @param databaseId Undocumented feature
 *  @param keyProperty Identifies a property into which MyBatis will set the key value returned by getGeneratedKeys,
 *    or by a selectKey child element of the insert statement. Default: unset.
 *  @param keyColumn Sets the name of the column in the table with a generated key.
 *    This is only required in certain databases (like PostgreSQL)
 *    when the key column is not the first column in the table.
 *  @param useGeneratedKeys This tells MyBatis to use the JDBC getGeneratedKeys method to retrieve keys
 *    generated internally by the database (e.g. auto increment fields in RDBMS like MySQL or SQL Server).
 *    Default: false
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
