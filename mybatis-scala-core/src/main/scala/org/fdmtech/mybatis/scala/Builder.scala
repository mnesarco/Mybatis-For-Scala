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

import scala.collection.mutable.{HashSet,ListBuffer}
import scala.xml._
import java.io.InputStream

class ConfigSpace(val name : String = "_DEFAULT_") {

  private var cache : Cache = null

  private val resultMaps = new HashSet[ResultMap[_]]

  private val statements = new HashSet[Statement]

  private implicit def posint2string( i : Int ) : String = if (i > -1) i.toString else null

  private implicit def boolean2string( b : Boolean ) : String = if (b) "true" else "false"

  private implicit def jdbctype2string( t : JdbcType ) : String = t match {
    case null => null
    case JdbcType.UNDEFINED => null
    case _ => t.toString
  }

  private implicit def anyref2string( ref : AnyRef ) : String = ref match {
    case null => null
    case r => r.toString
  }

  private def resolve(r : { var fqi : FQI }) : String = {
    r match {
      case null => null
      case subject => if (subject.fqi.contextId == name) subject.fqi.localId else subject.fqi.absoluteId
    }
  }

  private def cacheNode = {
    cache match {
      case null =>
        Comment("No Cache")
      case Cache(eviction,flushInterval,size,readOnly,owner) =>
        if (owner == name)
          <cache eviction={eviction} flushInterval={flushInterval} size={size} readOnly={readOnly} />
        else
          <cache-ref namespace={owner} />
    }
  }

  private def addResultMap(rm : ResultMap[_]) : this.type = {
    if (rm != null && rm.fqi == null) {
      rm.fqi = ConfigSpace.generateFQI(rm, name)
      resultMaps add rm
      for (mapping <- rm.mappings) {
        mapping match {
          case Arg(_,_,_,_,_,resultMap) =>
            addResultMap(resultMap)
          case Association(_,_,_,_,_,_,resultMap,_) =>
            addResultMap(resultMap)
          case Collection(_,_,_,_,_,_,_,resultMap,_) =>
            addResultMap(resultMap)
          case _ =>
            // Skip
        }
      }
    }
    this
  }

  private def addStatement(stmt : Statement) = {
    stmt match {
      case select : Select =>
         if (select.fqi == null) {
            select.fqi = ConfigSpace.generateFQI(select, name)
            statements add select
            addResultMap(select.resultMap)
         }
      case other : Statement =>
        if (other.fqi == null) {
          other.fqi = ConfigSpace.generateFQI(other, name)
          statements add other
        }
    }
    this
  }

  private def resultMapNodes : Seq[Node] = {
    val list = new ListBuffer[Node]
    for (rm <- resultMaps) {
      list +=
      <resultMap
          id={rm.fqi.localId}
          type={rm.resultType}
          extends={resolve(rm.parent)}>
          {constructorNode(rm)}
          {mappingNodes(rm.mappings)}
          {discriminatorNode(rm)}
      </resultMap>
    }
    list
  }

  private def mappingNodes(mappings : Seq[ResultMapping]) = {
    val nodes = for (m <- mappings) yield
      m match {
        case Id(property,column,javaType,jdbcType,typeHandler) =>
          <id
            property={property}
            column={column}
            javaType={javaType}
            jdbcType={jdbcType}
            typeHandler={typeHandler} />;
        case IdArg(column,javaType,jdbcType,typeHandler) =>
          <idArg
            column={column}
            javaType={javaType}
            jdbcType={jdbcType}
            typeHandler={typeHandler} />;
        case Result(property,column,javaType,jdbcType,typeHandler) =>
          <result
            property={property}
            column={column}
            javaType={javaType}
            jdbcType={jdbcType}
            typeHandler={typeHandler} />;
        case Arg(column,javaType,jdbcType,typeHandler,select,resultMap) =>
          <arg
            column={column}
            javaType={javaType}
            jdbcType={jdbcType}
            typeHandler={typeHandler}
            select={resolve(select)}
            resultMap={resolve(resultMap)} />;
        case Association(property,column,javaType,jdbcType,typeHandler,select,resultMap,notNullColumn) =>
          <association
            property={property}
            column={column}
            javaType={javaType}
            jdbcType={jdbcType}
            typeHandler={typeHandler}
            select={resolve(select)}
            resultMap={resolve(resultMap)}
            notNullColumn={notNullColumn}/>;
        case Collection(property,column,javaType,jdbcType,ofType,typeHandler,select,resultMap,notNullColumn) =>
          <collection
            property={property}
            column={column}
            javaType={javaType}
            jdbcType={jdbcType}
            ofType={ofType}
            typeHandler={typeHandler}
            select={resolve(select)}
            resultMap={resolve(resultMap)}
            notNullColumn={notNullColumn}/>;
      }
      nodes.sortWith((a,b) => tagOrdinal(a) < tagOrdinal(b))
  }

  private def constructorNode(rm : ResultMap[_]) = {
    rm.constructor match {
      case c : Constructor =>
        <constructor>
          {mappingNodes(for (a <- c.args) yield a)}
        </constructor>
      case null => Seq[Node]()
    }
  }

  private def discriminatorNode(rm : ResultMap[_]) = {
    rm.discriminator match {
      case Discriminator(column,javaType,jdbcType,typeHandler,cases) =>
        val caseNodes = for (c <- cases) yield {
          c match {
            case Case(value, resultMap, resultType) =>
              <case value={value} resultMap={resolve(resultMap)} resultType={resultType} />;
          }
        }
        <discriminator
            column={column}
            javaType={javaType}
            jdbcType={jdbcType}
            typeHandler={typeHandler}>
          {caseNodes}
        </discriminator>;
      case null => Seq[Node]()
    }
  }

  private def selectNode(select : Select) : Node = {
    select match {
      case stmt : SelectOne[_,_] =>
        <select
          id={stmt.fqi.localId}
          resultMap={resolve(stmt.resultMap)}
          resultType={if (stmt.resultMap == null) stmt.resultType else null}
          resultSetType={stmt.resultSetType}
          statementType={stmt.statementType}
          fetchSize={stmt.fetchSize}
          timeout={stmt.timeout}
          flushCache={stmt.flushCache}
          useCache={stmt.useCache}
          databaseId={stmt.databaseId}
          parameterType={stmt.parameterType}>
          {getSql(stmt.defined)}
        </select>;
      case stmt : SelectList[_,_] =>
        <select
          id={stmt.fqi.localId}
          resultMap={resolve(stmt.resultMap)}
          resultType={if (stmt.resultMap == null) stmt.resultType else null}
          resultSetType={stmt.resultSetType}
          statementType={stmt.statementType}
          fetchSize={stmt.fetchSize}
          timeout={stmt.timeout}
          flushCache={stmt.flushCache}
          useCache={stmt.useCache}
          databaseId={stmt.databaseId}
          parameterType={stmt.parameterType}>
          {getSql(stmt.defined)}
        </select>;
      case stmt : SelectMap[_,_,_] =>
        <select
          id={stmt.fqi.localId}
          resultMap={resolve(stmt.resultMap)}
          resultType={if (stmt.resultMap == null) stmt.resultType else null}
          resultSetType={stmt.resultSetType}
          statementType={stmt.statementType}
          fetchSize={stmt.fetchSize}
          timeout={stmt.timeout}
          flushCache={stmt.flushCache}
          useCache={stmt.useCache}
          databaseId={stmt.databaseId}
          parameterType={stmt.parameterType}>
          {getSql(stmt.defined)}
        </select>;
      case _ => error("Unsupported statement type")
    }
  }

  private def getSql(dsql : DynamicSQL) = {
    for (n <- dsql.xsql.child) yield
      n match {
        case <xsql>{_*}</xsql> => n.child
        case _ => n
      }
  }

  private def statementNodes : Seq[Node] = {
    val list = new ListBuffer[Node]
    for(s <- statements) s match {
      case select : Select =>
        list += selectNode(select)
      case update : Update[_] =>
        list +=
          <update
            id={update.fqi.localId}
            statementType={update.statementType}
            timeout={update.timeout}
            flushCache={update.flushCache}
            databaseId={update.databaseId}
            parameterType={update.parameterType}>
            {getSql(update.defined)}
          </update>;
      case delete : Delete[_] =>
        list +=
          <delete
            id={delete.fqi.localId}
            statementType={delete.statementType}
            timeout={delete.timeout}
            flushCache={delete.flushCache}
            databaseId={delete.databaseId}
            parameterType={delete.parameterType}>
            {getSql(delete.defined)}
          </delete>;
      case insert : Insert[_] =>
        list +=
          <insert
            id={insert.fqi.localId}
            statementType={insert.statementType}
            timeout={insert.timeout}
            flushCache={insert.flushCache}
            databaseId={insert.databaseId}
            parameterType={insert.parameterType}
            keyProperty={insert.keyProperty}
            keyColumn={insert.keyColumn}
            useGeneratedKeys={insert.useGeneratedKeys}>
            {getSql(insert.defined)}
          </insert>;
      case _ => error("Unsupported statement type")
    }
    list
  }

  private def tagOrdinal(mapping : Node) : Int = {
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

  // Start of public API ==================================

  def <<(stmt : Statement) = addStatement(stmt)

  def <<(stmts: Seq[Statement]) = {
    stmts foreach {stmt => addStatement(stmt)}
    this
  }

  def <<(mapper : { val bind : Seq[Statement] }) = {
    mapper.bind foreach {stmt => addStatement(stmt)}
    this
  }

  def <<(c : Cache) = {
    if (c != null && c.owner == null) {
      c.owner = name
    }
    cache = c
    this
  }

  def toXML =
    <mapper namespace={name}>
      {cacheNode}
      {resultMapNodes}
      {statementNodes}
    </mapper>;

  override def toString : String = {
    import java.io.StringWriter
    import scala.xml.dtd.{DocType, PublicID}
    val writer = new StringWriter
    val docType = DocType("mapper", PublicID("-//mybatis.org//DTD Mapper 3.0//EN", "http://mybatis.org/dtd/mybatis-3-mapper.dtd"), Nil)
    XML.write(writer, toXML, "UTF-8", xmlDecl=false, doctype=docType)
    writer.toString
  }

  def toInputStream : InputStream =
    new java.io.ByteArrayInputStream(toString.getBytes)

  // End of public API =================================

}

object ConfigSpace {

  private var anonymousCount : Int = 0

  private def next(prefix : String) : String = { anonymousCount += 1; prefix + "-" + anonymousCount}

  private def generateFQI(obj : AnyRef, spaceId : String ) : FQI = {
    obj match {
      case null => null
      case subject =>
        val localId = subject.getClass.getName.replace('.', '-')
        localId match {
          case "org-fdmtech-mybatis-scala-ResultMap"  => FQI(spaceId, next(localId))
          case "org-fdmtech-mybatis-scala-SelectOne"  => FQI(spaceId, next(localId))
          case "org-fdmtech-mybatis-scala-SelectList" => FQI(spaceId, next(localId))
          case "org-fdmtech-mybatis-scala-SelectMap"  => FQI(spaceId, next(localId))
          case "org-fdmtech-mybatis-scala-Insert"     => FQI(spaceId, next(localId))
          case "org-fdmtech-mybatis-scala-Update"     => FQI(spaceId, next(localId))
          case "org-fdmtech-mybatis-scala-Delete"     => FQI(spaceId, next(localId))
          case _ => FQI(spaceId, localId)
        }
    }
  }

  def apply(name: String = "_DEFAULT_") = new ConfigSpace(name)

}