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
 * Defines YAML constructor.
 *
 * {{{
 * import scala.language.implicitConversions
 *
 * import shampoo.yaml.{ *, given }
 * 
 * case class User(id: Int, name: String)
 * 
 * // Define how to construct User from YAML
 * given YamlConstructor[User] =
 *    yaml => User(yaml("id"), yaml("name"))
 * 
 * val yaml = Yaml.load("{ id: 1000, name: lupita }")
 * 
 * // Construct and verify
 * val user = yaml.as[User]
 * assert(user.id == 1000)
 * assert(user.name == "lupita")
 * }}}
 *
 * @see [[YamlRepresenter]]
 */
@FunctionalInterface
trait YamlConstructor[T]:
  /**
   * Constructs data.
   *
   * @param node YAML node
   */
  def construct(node: YamlNode): T
