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

package org.fdmtech.mybatis.scala.session

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.fdmtech.mybatis.scala.builder._
import org.fdmtech.mybatis.scala.mapping._

/**
 * Factory of PersistenceSession instances
 */
class PersistenceSessionFactory(factory : SqlSessionFactory) {
  def createSession = new PersistenceSession(factory)
}

/**
 * Disconnected Persistence Session
 */
class PersistenceSession(f : SqlSessionFactory) {

  type Callback = (SqlSession) => Unit

  /**
   * Provides a session, but does not manage any transactional code
   */
  def managed(call : Callback) : Unit = {
    val session = f.openSession
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
    val session = f.openSession
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
    val session = f.openSession
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
