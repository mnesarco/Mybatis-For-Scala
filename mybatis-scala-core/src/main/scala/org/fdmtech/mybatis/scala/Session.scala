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

import org.apache.ibatis.builder.xml.{XMLConfigBuilder, XMLMapperBuilder}
import org.apache.ibatis.session.{SqlSession, SqlSessionFactoryBuilder, SqlSessionFactory, Configuration => MBConfig}
import org.apache.ibatis.io.Resources
import java.io.Reader

object Configuration {

  def apply(path : String) : ConfigurationBuilder = {
    val b = new ConfigurationBuilder
    b config path
  }

  def apply(reader : Reader) : ConfigurationBuilder = {
    val b = new ConfigurationBuilder
    b config reader
  }

}

/** Main configuration builder
 */
class ConfigurationBuilder {

  private var configuration : MBConfig = _

  /** Loads the main configuration from a Mybatis xml config.
   *  @param reader Mybatis config xml.
   */
  def config(reader : Reader) : this.type = {
    val configBuilder = new XMLConfigBuilder(reader)
    configuration = configBuilder.parse
    this
  }

  /** Loads the main configuration from a Mybatis xml config file.
   *  @param reader Mybatis config xml path (in the classpath).
   */
  def config(path : String) : this.type = config(Resources.getResourceAsReader(path))

  def addSpace(name : String)(f: ConfigSpace => Unit) : this.type = {
    val space = ConfigSpace(name)
    f(space)
    val builder = new XMLMapperBuilder(space.toInputStream, configuration, "ConfigSpace(" + name + ")", configuration.getSqlFragments(), name)
    builder.parse
    this
  }

  def build : SqlSessionManager = {
    val factoryBuilder = new SqlSessionFactoryBuilder
    new SqlSessionManager(factoryBuilder.build(configuration))
  }

}

/** Session manager.
 */
class SqlSessionManager(factory : SqlSessionFactory) {

  def createSession = factory.openSession

  type Callback = (SqlSession) => Unit

  /**
   * Provides a session, but does not manage any transactional code
   */
  def managed(call : Callback) : Unit = {
    val session = factory.openSession
    try {
      call(session)
    }
    finally {
      session.close
    }
  }

  /**
   * Provides a session and rollback at the end
   */
  def readOnly(call : Callback) : Unit = {
    val session = factory.openSession
    try {
      call(session)
      session.rollback
    }
    finally {
      session.close
    }
  }

  /**
   * Provides a session and commit at the end. Rollback if any exception.
   */
  def transaction(call : Callback) : Unit = {
    val session = factory.openSession
    try {
      call(session)
      session.commit
    }
    catch {
      case e: Exception => {
          session.rollback
          throw e
      }
    }
    finally {
      session.close
    }
  }

}
