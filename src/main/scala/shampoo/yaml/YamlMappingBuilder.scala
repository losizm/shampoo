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

import java.util.LinkedHashMap as JHashMap

import YamlValues.*

/**
 * Defines YAML mapping builder.
 *
 * {{{
 * import scala.language.implicitConversions
 * 
 * import shampoo.yaml.{ *, given }
 * 
 * val user = YamlMappingBuilder()
 *    .add("id", 1000)
 *    .add("name", "lupita")
 *    .add("groups", Set("lupita", "sudoer"))
 *    .toYamlMapping()
 * 
 * assert { user("id").as[Int] == 1000 }
 * assert { user("name").as[String] == "lupita" }
 * assert { user("groups").as[Set[String]] == Set("lupita", "sudoer") }
 * }}}
 *
 * @see [[YamlMapping]]
 * @see [[YamlSequenceBuilder]]
 */
class YamlMappingBuilder:
  private var node = JHashMap[String, AnyRef]()

  /**
   * Adds key associated with null value to mapping.
   *
   * @return this builder
   */
  def addNull(key: String): this.type =
    if key == null then throw NullPointerException()
    node.put(key, null)
    this

  /**
   * Adds key-value pair to mapping.
   *
   * @return this builder
   */
  def add(key: String, value: String): this.type =
    if key == null || value == null then throw NullPointerException()
    node.put(key, value)
    this

  /**
   * Adds key-value pair to mapping.
   *
   * @return this builder
   */
  def add(key: String, value: Boolean): this.type =
    if key == null then throw NullPointerException()
    node.put(key, valueOf(value))
    this

  /**
   * Adds key-value pair to mapping.
   *
   * @return this builder
   */
  def add(key: String, value: Int): this.type =
    if key == null then throw NullPointerException()
    node.put(key, valueOf(value))
    this

  /**
   * Adds key-value pair to mapping.
   *
   * @return this builder
   */
  def add(key: String, value: Long): this.type =
    if key == null then throw NullPointerException()
    node.put(key, valueOf(value))
    this

  /**
   * Adds key-value pair to mapping.
   *
   * @return this builder
   */
  def add(key: String, value: Float): this.type =
    if key == null then throw NullPointerException()
    node.put(key, valueOf(value))
    this

  /**
   * Adds key-value pair to mapping.
   *
   * @return this builder
   */
  def add(key: String, value: Double): this.type =
    if key == null then throw NullPointerException()
    node.put(key, valueOf(value))
    this

  /**
   * Adds key-value pair to mapping.
   *
   * @return this builder
   */
  def add(key: String, value: BigInt): this.type =
    if key == null then throw NullPointerException()
    node.put(key, valueOf(value))
    this

  /**
   * Adds key-value pair to mapping.
   *
   * @return this builder
   */
  def add(key: String, value: BigDecimal): this.type =
    if key == null then throw NullPointerException()
    node.put(key, valueOf(value))
    this

  /**
   * Adds key-value pair to mapping.
   *
   * @return this builder
   */
  def add(key: String, value: YamlNode): this.type =
    if key == null || value == null then throw NullPointerException()
    node.put(key, unwrap(value))
    this

  /** Creates mapping. */
  def toYamlMapping(): YamlMapping =
    val obj = YamlMappingImpl(node)
    node = JHashMap[String, AnyRef]()
    obj
