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

package org.fdmtech.mybatis.scala.builder

import org.fdmtech.mybatis.scala.mapping._
import scala.xml._
import scala.xml.dtd.{DocType, PublicID}
import org.apache.ibatis.`type`._
import scala.collection.mutable.{HashSet,ListBuffer}
import org.apache.ibatis.session.Configuration
import java.io.{StringWriter, ByteArrayInputStream}
import org.apache.ibatis.builder.xml.{XMLMapperBuilder, XMLMapperEntityResolver}
import org.apache.ibatis.parsing.XPathParser

class MappingBuilder(configuration : Configuration) {

  val resultMaps = new HashSet[ResultMap]
  val resultMapNodes = new ListBuffer[Node]
  val statements = new ListBuffer[Node]

  def build : Unit = {

    // Build XML Representation
    val writer = new StringWriter
    val docType = DocType("mapper", PublicID("-//mybatis.org//DTD Mapper 3.0//EN", "http://mybatis.org/dtd/mybatis-3-mapper.dtd"), Nil)
    XML.write(writer, mapperNode, "UTF-8", xmlDecl=false, doctype=docType)

    // Load XML into MyBatis
    val inputStream = new ByteArrayInputStream(writer.toString.getBytes)
    val builder = new XMLMapperBuilder(inputStream, configuration, "SCALA", configuration.getSqlFragments())
    builder.parse

  }

  def addResultMap(rm : ResultMap) : Unit = {
    if (!resultMaps.contains(rm)) {
      resultMapNodes += <resultMap
          id={className(rm)}
          type={className(rm.resultType)}
          extends={className(rm.parent)}>
          {addConstructor(rm)}
          {addMappings(rm.mappings)}
      </resultMap>;
      resultMaps.add(rm)
    }
  }

  def addMapper(m : Mapper) : Unit = {
    for (s <- m.bind) addStatement(s)
  }

  def addStatement(s : Statement[_]) : Unit = {
    s match {
      case select : Select[_,_] =>
        statements +=
          <select
            id={select.getStatementId}
            resultMap={select.getResultMapId}
            resultType={select.getResultType}
            resultSetType={select.getResultSetType}
            statementType={select.getStatementType}
            fetchSize={select.getFetchSize}
            timeout={select.getTimeout}
            flushCache={select.getFlushCache}
            useCache={select.getUseCache}
            databaseId={select.getDatabaseId}
            parameterType={select.getParameterType}>
            {getSql(select)}
          </select>;
        if (select.getResultMap != null)
          addResultMap(select.getResultMap);
      case update : Update[_] =>
        statements +=
          <update
            id={update.getStatementId}
            statementType={update.getStatementType}
            timeout={update.getTimeout}
            flushCache={update.getFlushCache}
            databaseId={update.getDatabaseId}
            parameterType={update.getParameterType}>
            {getSql(update)}
          </update>;
      case delete : Delete[_] =>
        statements +=
          <delete
            id={delete.getStatementId}
            statementType={delete.getStatementType}
            timeout={delete.getTimeout}
            flushCache={delete.getFlushCache}
            databaseId={delete.getDatabaseId}
            parameterType={delete.getParameterType}>
            {getSql(delete)}
          </delete>;
      case insert : Insert[_] =>
        statements +=
          <insert
            id={insert.getStatementId}
            statementType={insert.getStatementType}
            timeout={insert.getTimeout}
            flushCache={insert.getFlushCache}
            databaseId={insert.getDatabaseId}
            parameterType={insert.getParameterType}
            keyProperty={insert.getKeyProperty}
            keyColumn={insert.getKeyColumn}
            useGeneratedKeys={insert.getUseGeneratedKeys}>
            {getSql(insert)}
          </insert>;
      case _ => error("Invalid bound method")
    }
  }

  protected def getSql(stmt : Statement[_]) = {
    for (n <- stmt.sql.child) yield
      n match {
        case <sql>{_*}</sql> => n.child
        case _ => n
      }
  }

  protected def addConstructor(rm : ResultMap) = {
    rm.constructor match {
      case c:Constructor =>
        <constructor>
          {addMappings(for (a <- c.args) yield a)}
        </constructor>
      case null => Seq[Node]()
    }
  }

  protected def addMappings(mappings : Seq[ResultMapping]) = {
    val nodes = for (m <- mappings) yield
      m match {
        case Id(property,column,javaType,jdbcType,typeHandler) =>
          <id
            property={property}
            column={column}
            javaType={className(javaType)}
            jdbcType={jdbcTypeString(jdbcType)}
            typeHandler={className(typeHandler)} />;
        case IdArg(column,javaType,jdbcType,typeHandler) =>
          <idArg
            column={column}
            javaType={className(javaType)}
            jdbcType={jdbcTypeString(jdbcType)}
            typeHandler={className(typeHandler)} />;
        case Result(property,column,javaType,jdbcType,typeHandler) =>
          <result
            property={property}
            column={column}
            javaType={className(javaType)}
            jdbcType={jdbcTypeString(jdbcType)}
            typeHandler={className(typeHandler)} />;
        case Arg(column,javaType,jdbcType,typeHandler,select,resultMap) =>
          if (resultMap != null)
            addResultMap(resultMap);
          <arg
            column={column}
            javaType={className(javaType)}
            jdbcType={jdbcTypeString(jdbcType)}
            typeHandler={className(typeHandler)}
            select={className(select)}
            resultMap={className(resultMap)} />;
        case Association(property,column,javaType,jdbcType,typeHandler,select,resultMap,notNullColumn) =>
          if (resultMap != null)
            addResultMap(resultMap);
          <association
            property={property}
            column={column}
            javaType={className(javaType)}
            jdbcType={jdbcTypeString(jdbcType)}
            typeHandler={className(typeHandler)}
            select={className(select)}
            resultMap={className(resultMap)}
            notNullColumn={notNullColumn}/>;
        case Collection(property,column,javaType,jdbcType,ofType,typeHandler,select,resultMap,notNullColumn) =>
          if (resultMap != null)
            addResultMap(resultMap);
          <collection
            property={property}
            column={column}
            javaType={className(javaType)}
            jdbcType={jdbcTypeString(jdbcType)}
            ofType={className(ofType)}
            typeHandler={className(typeHandler)}
            select={className(select)}
            resultMap={className(resultMap)}
            notNullColumn={notNullColumn}/>;
      }
      nodes.sortWith((a,b) => ResultMappingOrder(a) < ResultMappingOrder(b))
  }

  protected def className(a : AnyRef) = {
    val name = a match {
      case null => null
      case c:Class[_] => c.getName
      case _ => a.getClass.getName
    }
    // Translate to mybatis aliases
    name match {
      case null => null
      case "byte" => "_byte"
      case "long" => "_long"
      case "short" => "_short"
      case "int" => "_integer"
      case "double" => "_double"
      case "float" => "_float"
      case "boolean" => "_boolean"
      case other => other
    }
  }

  protected def jdbcTypeString(t : JdbcType) = t match {
    case null => null
    case JdbcType.UNDEFINED => null
    case _ => t.toString
  }

  protected def mapperNode =
    <mapper namespace="SCALA">
      {resultMapNodes}
      {statements}
    </mapper>

}
