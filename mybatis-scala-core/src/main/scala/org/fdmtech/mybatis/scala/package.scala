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

package org.fdmtech.mybatis

package object scala {

  object ResultSetType extends Enumeration {
    val FORWARD_ONLY, SCROLL_INSENSITIVE, SCROLL_SENSITIVE = Value
  }

  object StatementType extends Enumeration {
    val STATEMENT, PREPARED, CALLABLE = Value
  }

  object JdbcType extends Enumeration {
      val ARRAY, BIT, TINYINT, SMALLINT, INTEGER, BIGINT, FLOAT, REAL, DOUBLE,
        NUMERIC, DECIMAL, CHAR, VARCHAR, LONGVARCHAR, DATE, TIME, TIMESTAMP,
        BINARY, VARBINARY, LONGVARBINARY, NULL, OTHER, BLOB, CLOB, BOOLEAN,
        CURSOR, UNDEFINED, NVARCHAR, NCHAR, NCLOB, STRUCT = Value
  }

  // Type aliases and enums
  type TypeHandler = org.apache.ibatis.`type`.TypeHandler
  type XSQL = _root_.scala.xml.Node
  type ResultSetType = ResultSetType.Value
  type StatementType = StatementType.Value
  type JdbcType = JdbcType.Value
  type CacheEviction = CacheEviction.Value
  type Session = org.apache.ibatis.session.SqlSession
  type RowBounds = org.apache.ibatis.session.RowBounds

  class DynamicSQL {
    var xsql : XSQL = _
    def as (xsql : XSQL) : Unit = {
      this.xsql = xsql
    }
  }

  class Type[T](implicit val m : Manifest[T]) {
    override def toString : String = m.erasure.getName match {
      case null => null
      case "byte" => "_byte"
      case "long" => "_long"
      case "short" => "_short"
      case "int" => "_integer"
      case "double" => "_double"
      case "float" => "_float"
      case "boolean" => "_boolean"
      case "java.lang.Object" => null
      case other => other
    }
  }

  object Type {
    def apply[T](implicit m : Manifest[T]) = new Type[T]
  }

  case class FQI(val contextId : String, val localId : String) {
    val absoluteId = contextId + "." + localId
  }

  val DefaultRowBounds = org.apache.ibatis.session.RowBounds.DEFAULT

  abstract class ConfigElement

  implicit def StringToXSQL( s : String ) : XSQL = <xsql>{s}</xsql>

}