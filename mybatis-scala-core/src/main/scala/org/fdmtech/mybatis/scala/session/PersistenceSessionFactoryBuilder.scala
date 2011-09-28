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

import org.apache.ibatis.builder.xml.XMLConfigBuilder
import org.apache.ibatis.io.Resources
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.session.Configuration
import java.io.Reader
import org.fdmtech.mybatis.scala.mapping._
import org.fdmtech.mybatis.scala.builder._

class PersistenceSessionFactoryBuilder {

  protected var configuration : Configuration = _
  protected var mapperBuilder : MappingBuilder = _

  def config(reader : Reader) : this.type = {
    val configBuilder = new XMLConfigBuilder(reader)
    configuration = configBuilder.parse
    mapperBuilder = new MappingBuilder(configuration)
    this
  }

  def config(path : String) : this.type = config(Resources.getResourceAsReader(path))

  def << (f : Statement[_], s : Statement[_]*) : this.type = {
    mapperBuilder.addStatement(f)
    for (stmt <- s) mapperBuilder.addStatement(stmt)
    this
  }

  def << (f : Mapper, m : Mapper*) : this.type = {
    mapperBuilder.addMapper(f)
    for(mapper <- m) mapperBuilder.addMapper(mapper)
    this
  }

  def build : PersistenceSessionFactory = {
    mapperBuilder.build
    val factoryBuilder = new SqlSessionFactoryBuilder
    new PersistenceSessionFactory(factoryBuilder.build(configuration))
  }

}
