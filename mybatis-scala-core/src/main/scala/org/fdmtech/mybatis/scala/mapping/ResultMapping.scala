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

import org.apache.ibatis.`type`.JdbcType
import org.apache.ibatis.`type`.TypeHandler
import org.apache.ibatis.mapping.ResultFlag
import scala.collection.mutable.ListBuffer
import scala.xml.Node

abstract class ResultMapping
abstract class BaseArg extends ResultMapping

case class Arg(
  column : String = null,
  javaType : Class[_] = null,
  jdbcType : JdbcType = JdbcType.UNDEFINED,
  typeHandler : Class[_ <: TypeHandler] = null,
  select : Statement[_] = null,
  resultMap : ResultMap = null
)
extends BaseArg

case class IdArg(
  column : String = null,
  javaType : Class[_] = null,
  jdbcType : JdbcType = JdbcType.UNDEFINED,
  typeHandler : Class[_ <: TypeHandler] = null
)
extends BaseArg

case class Result(
  property : String = null,
  column : String = null,
  javaType : Class[_] = null,
  jdbcType : JdbcType = JdbcType.UNDEFINED,
  typeHandler : Class[_ <: TypeHandler] = null
) extends ResultMapping

case class Id(
  property : String = null,
  column : String = null,
  javaType : Class[_] = null,
  jdbcType : JdbcType = JdbcType.UNDEFINED,
  typeHandler : Class[_ <: TypeHandler] = null
) extends ResultMapping

case class Association(
  property : String = null,
  column : String = null,
  javaType : Class[_] = null,
  jdbcType : JdbcType = JdbcType.UNDEFINED,
  typeHandler : Class[_ <: TypeHandler] = null,
  select : Statement[_] = null,
  resultMap : ResultMap = null,
  notNullColumn : String = null
) extends ResultMapping

case class Collection(
  property : String = null,
  column : String = null,
  javaType : Class[_] = null,
  jdbcType : JdbcType = JdbcType.UNDEFINED,
  ofType : Class[_] = null,
  typeHandler : Class[_ <: TypeHandler] = null,
  select : Statement[_] = null,
  resultMap : ResultMap = null,
  notNullColumn : String = null
) extends ResultMapping

case class Case(value : String, resultMap : ResultMap = null, resultType : Class[_] = null)

case class Discriminator(
  column : String = null,
  javaType : Class[_] = null,
  jdbcType : JdbcType = JdbcType.UNDEFINED,
  typeHandler : Class[_ <: TypeHandler] = null,
  cases : List[Case] = List()
)

case class Constructor(val args : BaseArg*)

class ResultMap(var resultType : Class[_], var parent : ResultMap = null) {

  val mappings = new ListBuffer[ResultMapping]
  var constructor : Constructor = null
  var discriminator : Discriminator = null

  def result(
    property : String = null,
    column : String = null,
    javaType : Class[_] = null,
    jdbcType : JdbcType = JdbcType.UNDEFINED,
    typeHandler : Class[_ <: TypeHandler] = null) = mappings += Result(property,column,javaType,jdbcType,typeHandler)

  def id(
    property : String = null,
    column : String = null,
    javaType : Class[_] = null,
    jdbcType : JdbcType = JdbcType.UNDEFINED,
    typeHandler : Class[_ <: TypeHandler] = null) = mappings += Id(property,column,javaType,jdbcType,typeHandler)

  def association(
    property : String = null,
    column : String = null,
    javaType : Class[_] = null,
    jdbcType : JdbcType = JdbcType.UNDEFINED,
    typeHandler : Class[_ <: TypeHandler] = null,
    select : Statement[_] = null,
    resultMap : ResultMap = null,
    notNullColumn : String = null) = mappings += Association(property,column,javaType,jdbcType,typeHandler,select,resultMap,notNullColumn)

  def collection(
    property : String = null,
    column : String = null,
    javaType : Class[_] = null,
    jdbcType : JdbcType = JdbcType.UNDEFINED,
    ofType : Class[_] = null,
    typeHandler : Class[_ <: TypeHandler] = null,
    select : Statement[_] = null,
    resultMap : ResultMap = null,
    notNullColumn : String = null) = mappings += Collection(property,column,javaType,jdbcType,ofType,typeHandler,select,resultMap,notNullColumn)

  def constructor(args : BaseArg*) : Unit = constructor = Constructor(args : _*)

  def discriminator(
    column : String = null,
    javaType : Class[_] = null,
    jdbcType : JdbcType = JdbcType.UNDEFINED,
    typeHandler : Class[_ <: TypeHandler] = null,
    cases : List[Case] = List()) : Unit = discriminator = Discriminator(column,javaType,jdbcType,typeHandler,cases)

}

object ResultMappingOrder {
  def apply(mapping : Node) : Int = {
    mapping match {
      case <id/> => 0
      case <idArg/> => 0
      case <result/> => 1
      case <arg/> => 1
      case <association/> => 2
      case <collection/> => 3
      case _ => 9
    }
  }
}