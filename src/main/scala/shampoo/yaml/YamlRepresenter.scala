/*
 * Copyright 2023 Carlos Conyers
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package shampoo.yaml

/**
 * Defines YAML representer.
 *
 * {{{
 * import scala.language.implicitConversions
 * 
 * import shampoo.yaml.{ *, given }
 * 
 * case class User(id: Int, name: String, groups: Seq[String])
 * 
 * // Define how to represent YAML from User
 * given YamlRepresenter[User] =
 *    user => Yaml.map("id" -> user.id, "name" -> user.name)
 *
 * val lupita = User(1000, "lupita")
 * 
 * // Represent and verify
 * val yaml = Yaml.toYaml(lupita)
 * assert(yaml.getInt("id") == 1000)
 * assert(yaml.getString("name") == "lupita")
 * }}}
 *
 * @see [[YamlConstructor]]
 */
@FunctionalInterface
trait YamlRepresenter[T]:
  /**
   * Represents data.
   *
   * @param data native data 
   */
  def represent(data: T): YamlNode
