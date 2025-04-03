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

import scala.annotation.targetName
import scala.util.Try

extension (yaml: YamlNode)
  /**
   * Gets value in YAML mapping.
   *
   * @param key mapping key
   *
   * @throws ClassCastException if not [[YamlMapping]]
   */
  @targetName("at")
  def \(key: String): YamlNode =
    expect[YamlMapping](yaml)(key)

  /**
   * Gets value in YAML sequence.
   *
   * @param index sequence index
   *
   * @throws ClassCastException if not [[YamlSequence]]
   */
  @targetName("at")
  def \(index: Int): YamlNode =
    expect[YamlSequence](yaml)(index)

  /**
   * Collects values with given key in traversed collections.
   *
   * {{{
   * import shampoo.yaml.{ Yaml, \\, given }
   *
   * val yaml = Yaml.load("""
   *   node:
   *     name: localhost
   *     users:
   *       - id: 0
   *         name: root
   *       - id: 1000
   *         name: lupita
   * """)
   *
   * val names = (yaml \\ "name").map(_.as[String])
   *
   * assert { names == Seq("localhost", "root", "lupita") }
   * }}}
   *
   * @param key mappings key
   */
  @targetName("collect")
  def \\(key: String): Seq[YamlNode] =
    yaml match
      case yaml: YamlMapping  => yaml.get(key).toSeq ++ yaml.toMap.values.flatMap(_ \\ key).toSeq
      case yaml: YamlSequence => yaml.toSeq.flatMap(_ \\ key).toSeq
      case _                  => Nil
