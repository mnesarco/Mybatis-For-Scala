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

package org.fdmtech.mybatis.scala

import scala.collection.mutable.ListBuffer
import scala.xml.Node

/** Base class for all mappings
 */
abstract class ResultMapping

/** Base class for constructor arguments
 */
abstract class BaseArg extends ResultMapping

/** Constructor formal argument mapping.
 *
 *  @param column The column name from the database, or the aliased column label.
 *    This is the same string that would normally be passed to resultSet.getString(columnName).
 *  @param javaType A fully qualified Java class name, or a type alias.
 *    MyBatis can usually figure out the type if you’re mapping to a JavaBean.
 *    However, if you are mapping to a HashMap, then you should specify the javaType
 *    explicitly to ensure the desired behaviour.
 *  @param jdbcType The JDBC Type from the list of supported types.
 *    The JDBC type is only required for nullable columns upon insert, update or delete.
 *    This is a JDBC requirement, not an MyBatis one. So even if you were coding JDBC directly,
 *    you’d need to specify this type – but only for nullable values.
 *  @param typeHandler Using this property you can override the default type handler on a mapping-by-mapping basis.
 *    The value is either a fully qualified class name of a TypeHandler implementation, or a type alias.
 */
case class Arg(
  column : String = null,
  javaType : Type[_] = null,
  jdbcType : JdbcType = JdbcType.UNDEFINED,
  typeHandler : Type[_ <: TypeHandler] = null,
  select : Statement = null,
  resultMap : ResultMap[_] = null
)
extends BaseArg

/** Constructor formal argument mapping with ID flag.
 *
 *  @param column The column name from the database, or the aliased column label.
 *    This is the same string that would normally be passed to resultSet.getString(columnName).
 *  @param javaType A fully qualified Java class name, or a type alias.
 *    MyBatis can usually figure out the type if you’re mapping to a JavaBean.
 *    However, if you are mapping to a HashMap, then you should specify the javaType
 *    explicitly to ensure the desired behaviour.
 *  @param jdbcType The JDBC Type from the list of supported types.
 *    The JDBC type is only required for nullable columns upon insert, update or delete.
 *    This is a JDBC requirement, not an MyBatis one. So even if you were coding JDBC directly,
 *    you’d need to specify this type – but only for nullable values.
 *  @param typeHandler Using this property you can override the default type handler on a mapping-by-mapping basis.
 *    The value is either a fully qualified class name of a TypeHandler implementation, or a type alias.
 */
case class IdArg(
  column : String = null,
  javaType : Type[_] = null,
  jdbcType : JdbcType = JdbcType.UNDEFINED,
  typeHandler : Type[_ <: TypeHandler] = null
)
extends BaseArg

/** These are the most basic of result mappings.
 *  Both id, and result map a single column value
 *  to a single property or field of a simple data type (String, int, double, Date, etc.)
 *
 *  @param property The field or property to map the column result to.
 *    If a matching JavaBeans property exists for the given name, then that will be used.
 *    Otherwise, MyBatis will look for a field of the given name.
 *    In both cases you can use complex property navigation using the usual dot notation.
 *    For example, you can map to something simple like: “username”,
 *    or to something more complicated like: “address.street.number”.
 *  @param column The column name from the database, or the aliased column label.
 *    This is the same string that would normally be passed to resultSet.getString(columnName).
 *  @param javaType A fully qualified Java class name, or a type alias.
 *    MyBatis can usually figure out the type if you’re mapping to a JavaBean.
 *    However, if you are mapping to a HashMap, then you should specify the javaType
 *    explicitly to ensure the desired behaviour.
 *  @param jdbcType The JDBC Type from the list of supported types.
 *    The JDBC type is only required for nullable columns upon insert, update or delete.
 *    This is a JDBC requirement, not an MyBatis one. So even if you were coding JDBC directly,
 *    you’d need to specify this type – but only for nullable values.
 *  @param typeHandler Using this property you can override the default type handler on a mapping-by-mapping basis.
 *    The value is either a fully qualified class name of a TypeHandler implementation, or a type alias.
 */
case class Result(
  property : String = null,
  column : String = null,
  javaType : Type[_] = null,
  jdbcType : JdbcType = JdbcType.UNDEFINED,
  typeHandler : Type[_ <: TypeHandler] = null
) extends ResultMapping

/** These are the most basic of result mappings.
 *  Both id, and result map a single column value
 *  to a single property or field of a simple data type (String, int, double, Date, etc.)
 *
 *  The only difference between the two is that id will flag the result as an identifier
 *  property to be used when comparing object instances. This helps to improve general performance,
 *  but especially performance of caching and nested result mapping (i.e. join mapping).
 *
 *  @param property The field or property to map the column result to.
 *    If a matching JavaBeans property exists for the given name, then that will be used.
 *    Otherwise, MyBatis will look for a field of the given name.
 *    In both cases you can use complex property navigation using the usual dot notation.
 *    For example, you can map to something simple like: “username”,
 *    or to something more complicated like: “address.street.number”.
 *  @param column The column name from the database, or the aliased column label.
 *    This is the same string that would normally be passed to resultSet.getString(columnName).
 *  @param javaType A fully qualified Java class name, or a type alias.
 *    MyBatis can usually figure out the type if you’re mapping to a JavaBean.
 *    However, if you are mapping to a HashMap, then you should specify the javaType
 *    explicitly to ensure the desired behaviour.
 *  @param jdbcType The JDBC Type from the list of supported types.
 *    The JDBC type is only required for nullable columns upon insert, update or delete.
 *    This is a JDBC requirement, not an MyBatis one. So even if you were coding JDBC directly,
 *    you’d need to specify this type – but only for nullable values.
 *  @param typeHandler Using this property you can override the default type handler on a mapping-by-mapping basis.
 *    The value is either a fully qualified class name of a TypeHandler implementation, or a type alias.
 */
case class Id(
  property : String = null,
  column : String = null,
  javaType : Type[_] = null,
  jdbcType : JdbcType = JdbcType.UNDEFINED,
  typeHandler : Type[_ <: TypeHandler] = null
) extends ResultMapping

/** The association element deals with a "has-one" type relationship.
 *  You specify the target property, the column to retrieve the value from, the javaType of the property
 *  (which MyBatis can figure out most of the time), the jdbcType if necessary and a typeHandler
 *  if you want to override the retrieval of the result values.
 *  Where the association differs is that you need to tell MyBatis how to load the association.
 *
 *  MyBatis can do so in two different ways:
 *    - Nested Select: By executing another mapped SQL statement that returns the complex type desired.
 *    - Nested Results: By using nested result mappings to deal with repeating subsets of joined results.
 *
 *  @param property The field or property to map the column result to.
 *    If a matching JavaBeans property exists for the given name, then that will be used.
 *    Otherwise, MyBatis will look for a field of the given name.
 *    In both cases you can use complex property navigation using the usual dot notation.
 *    For example, you can map to something simple like: “username”,
 *    or to something more complicated like: “address.street.number”.
 *  @param column The column name from the database, or the aliased column label.
 *    This is the same string that would normally be passed to resultSet.getString(columnName).
 *  @param javaType A fully qualified Java class name, or a type alias.
 *    MyBatis can usually figure out the type if you’re mapping to a JavaBean.
 *    However, if you are mapping to a HashMap, then you should specify the javaType
 *    explicitly to ensure the desired behaviour.
 *  @param jdbcType The JDBC Type from the list of supported types.
 *    The JDBC type is only required for nullable columns upon insert, update or delete.
 *    This is a JDBC requirement, not an MyBatis one. So even if you were coding JDBC directly,
 *    you’d need to specify this type – but only for nullable values.
 *  @param typeHandler Using this property you can override the default type handler on a mapping-by-mapping basis.
 *    The value is either a fully qualified class name of a TypeHandler implementation, or a type alias.
 *  @param select The reference to another mapped statement that will load the complex type required by this property mapping.
 *    The values retrieved from columns specified in the column attribute will be passed to the target select
 *    statement as parameters.
 *    Note: To deal with composite keys, you can specify multiple column names to pass to the nested select statement
 *    by using the syntax column=”{prop1=col1,prop2=col2}”.
 *    This will cause prop1 and prop2 to be set against the parameter object for the target nested select statement.
 *  @param resultMap This is the reference to a ResultMap that can map the nested results of this association into
 *    an appropriate object graph. This is an alternative to using a call to another select statement.
 *    It allows you to join multiple tables together into a single ResultSet.
 *    Such a ResultSet will contain duplicated, repeating groups of data that needs to be decomposed
 *    and mapped properly to a nested object graph. To facilitate this, MyBatis lets you “chain” result maps together,
 *    to deal with the nested results.
 */
case class Association(
  property : String = null,
  column : String = null,
  javaType : Type[_] = null,
  jdbcType : JdbcType = JdbcType.UNDEFINED,
  typeHandler : Type[_ <: TypeHandler] = null,
  select : Statement = null,
  resultMap : ResultMap[_] = null,
  notNullColumn : String = null
) extends ResultMapping

/** The collection element works almost identically to the association, but it deals with "has-many" type relationship.
 *
 *  @param property The field or property to map the column result to.
 *    If a matching JavaBeans property exists for the given name, then that will be used.
 *    Otherwise, MyBatis will look for a field of the given name.
 *    In both cases you can use complex property navigation using the usual dot notation.
 *    For example, you can map to something simple like: “username”,
 *    or to something more complicated like: “address.street.number”.
 *  @param column The column name from the database, or the aliased column label.
 *    This is the same string that would normally be passed to resultSet.getString(columnName).
 *  @param javaType A fully qualified Java class name, or a type alias.
 *    MyBatis can usually figure out the type if you’re mapping to a JavaBean.
 *    However, if you are mapping to a HashMap, then you should specify the javaType
 *    explicitly to ensure the desired behaviour.
 *  @param jdbcType The JDBC Type from the list of supported types.
 *    The JDBC type is only required for nullable columns upon insert, update or delete.
 *    This is a JDBC requirement, not an MyBatis one. So even if you were coding JDBC directly,
 *    you’d need to specify this type – but only for nullable values.
 *  @param typeHandler Using this property you can override the default type handler on a mapping-by-mapping basis.
 *    The value is either a fully qualified class name of a TypeHandler implementation, or a type alias.
 *  @param select The reference to another mapped statement that will load the complex type required by this property mapping.
 *    The values retrieved from columns specified in the column attribute will be passed to the target select
 *    statement as parameters.
 *    Note: To deal with composite keys, you can specify multiple column names to pass to the nested select statement
 *    by using the syntax column=”{prop1=col1,prop2=col2}”.
 *    This will cause prop1 and prop2 to be set against the parameter object for the target nested select statement.
 *  @param resultMap This is the reference to a ResultMap that can map the nested results of this association into
 *    an appropriate object graph. This is an alternative to using a call to another select statement.
 *    It allows you to join multiple tables together into a single ResultSet.
 *    Such a ResultSet will contain duplicated, repeating groups of data that needs to be decomposed
 *    and mapped properly to a nested object graph. To facilitate this, MyBatis lets you “chain” result maps together,
 *    to deal with the nested results.
 *  @param ofType This attribute is necessary to distinguish between the JavaBean (or field) property type
 *    and the type that the collection contains.
 */
case class Collection(
  property : String = null,
  column : String = null,
  javaType : Type[_] = null,
  jdbcType : JdbcType = JdbcType.UNDEFINED,
  ofType : Type[_] = null,
  typeHandler : Type[_ <: TypeHandler] = null,
  select : Statement = null,
  resultMap : ResultMap[_] = null,
  notNullColumn : String = null
) extends ResultMapping

/** A concrete mapping used by a discriminator case.
 */
case class Case(value : String, resultMap : ResultMap[_] = null, resultType : Type[_] = null)

/** Sometimes a single database query might return result sets of many different (but hopefully somewhat related)
 *  data types. The discriminator element was designed to deal with this situation, and others, including class
 *  inheritance hierarchies. The discriminator is pretty simple to understand, as it behaves much like a switch
 *  statement in Java. A discriminator definition specifies column and javaType attributes.
 *  The column is where MyBatis will look for the value to compare. The javaType is required to ensure the proper
 *  kind of equality test is performed (although String would probably work for almost any situation).
 *
 *  @param property The field or property to map the column result to.
 *    If a matching JavaBeans property exists for the given name, then that will be used.
 *    Otherwise, MyBatis will look for a field of the given name.
 *    In both cases you can use complex property navigation using the usual dot notation.
 *    For example, you can map to something simple like: “username”,
 *    or to something more complicated like: “address.street.number”.
 *  @param column The column name from the database, or the aliased column label.
 *    This is the same string that would normally be passed to resultSet.getString(columnName).
 *  @param javaType A fully qualified Java class name, or a type alias.
 *    MyBatis can usually figure out the type if you’re mapping to a JavaBean.
 *    However, if you are mapping to a HashMap, then you should specify the javaType
 *    explicitly to ensure the desired behaviour.
 *  @param jdbcType The JDBC Type from the list of supported types.
 *    The JDBC type is only required for nullable columns upon insert, update or delete.
 *    This is a JDBC requirement, not an MyBatis one. So even if you were coding JDBC directly,
 *    you’d need to specify this type – but only for nullable values.
 *  @param typeHandler Using this property you can override the default type handler on a mapping-by-mapping basis.
 *    The value is either a fully qualified class name of a TypeHandler implementation, or a type alias.
 *  @param cases A list of mapping cases
 */
case class Discriminator(
  column : String = null,
  javaType : Type[_] = null,
  jdbcType : JdbcType = JdbcType.UNDEFINED,
  typeHandler : Type[_ <: TypeHandler] = null,
  cases : List[Case] = List()
)

/** While properties will work for most Data Transfer Object (DTO) type classes,
 *  and likely most of your domain model, there may be some cases where you want
 *  to use immutable classes. Often tables that contain reference or lookup data
 *  that rarely or never changes is suited to immutable classes. Constructor
 *  injection allows you to set values on a class upon instantiation, without
 *  exposing public methods. MyBatis also supports private properties and private
 *  JavaBeans properties to achieve this, but some people prefer Constructor injection.
 *  The constructor element enables this.
 *
 *  In order to inject the results into the constructor,
 *  MyBatis needs to identify the constructor by the type of its parameters.
 *  Java has no way to introspect (or reflect) on parameter names.
 *  So when creating a constructor element, ensure that the arguments are in order,
 *  and that the data types are specified.
 */
case class Constructor(val args : BaseArg*)

/** The resultMap element is the most important and powerful element in MyBatis.
 *  It’s what allows you to do away with 90% of the code that JDBC requires to retrieve data from ResultSets,
 *  and in some cases allows you to do things that JDBC does not even support.
 *  In fact, to write the equivalent code for something like a join mapping for a complex statement could probably
 *  span thousands of lines of code. The design of the ResultMaps is such that simple statements don’t
 *  require explicit result mappings at all, and more complex statements require no more than is absolutely
 *  necessary to describe the relationships.
 *
 *  @param resultType The class of the result
 *  @param parent A reference to a parent resultMap (resultMap inheritence)
 */
class ResultMap[Result : Manifest](val parent : ResultMap[_] = null) {

  var fqi : FQI = null
  val resultType = Type[Result]
  val mappings = new ListBuffer[ResultMapping]
  var constructor : Constructor = null
  var discriminator : Discriminator = null

  /** Maps a single value to a single property.
   *  For a more specific documentation @see [[org.fdmtech.mybatis.scala.Result]]
   */
  def result(
    property : String = null,
    column : String = null,
    javaType : Type[_] = null,
    jdbcType : JdbcType = JdbcType.UNDEFINED,
    typeHandler : Type[_ <: TypeHandler] = null) = {
      mappings += Result(property,column,javaType,jdbcType,typeHandler)
      this
  }

  /** Maps a single value to a single property with ID Flag.
   *  For a more specific documentation @see [[org.fdmtech.mybatis.scala.Id]]
   */
  def id(
    property : String = null,
    column : String = null,
    javaType : Type[_] = null,
    jdbcType : JdbcType = JdbcType.UNDEFINED,
    typeHandler : Type[_ <: TypeHandler] = null) = {
      mappings += Id(property,column,javaType,jdbcType,typeHandler)
      this
  }

  /** Maps a complex value to a single property, "has-one" relationship.
   *  For a more specific documentation @see [[org.fdmtech.mybatis.scala.Association]]
   */
  def association[T : Manifest](
    property : String = null,
    column : String = null,
    jdbcType : JdbcType = JdbcType.UNDEFINED,
    typeHandler : Type[_ <: TypeHandler] = null,
    select : Statement = null,
    resultMap : ResultMap[_] = null,
    notNullColumn : String = null) = {
      mappings += Association(property,column,Type[T],jdbcType,typeHandler,select,resultMap,notNullColumn)
      this
  }

  /** Maps a complex value to a collection property, "has-many" relationship.
   *  For a more specific documentation @see [[org.fdmtech.mybatis.scala.Collection]]
   */
  def collection[T : Manifest](
    property : String = null,
    column : String = null,
    collectionType : Type[_] = null,
    jdbcType : JdbcType = JdbcType.UNDEFINED,
    typeHandler : Type[_ <: TypeHandler] = null,
    select : Statement = null,
    resultMap : ResultMap[_] = null,
    notNullColumn : String = null) = {
      mappings += Collection(property,column,collectionType,jdbcType,Type[T],typeHandler,select,resultMap,notNullColumn)
      this
  }

  /** Maps results to a constructor arguments.
   *  For a more specific documentation @see [[org.fdmtech.mybatis.scala.Constructor]]
   */
  def constructor(args : BaseArg*) : this.type = {
    constructor = Constructor(args : _*)
    this
  }

  /** For a more specific documentation @see [[org.fdmtech.mybatis.scala.Discriminator]]
   */
  def discriminator(
    column : String = null,
    javaType : Type[_] = null,
    jdbcType : JdbcType = JdbcType.UNDEFINED,
    typeHandler : Type[_ <: TypeHandler] = null,
    cases : List[Case] = List()) : this.type = {
      discriminator = Discriminator(column,javaType,jdbcType,typeHandler,cases)
      this
  }

}

