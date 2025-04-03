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

import java.util.LinkedList as JLinkedList

import YamlValues.*

/**
 * Defines YAML sequence builder.
 *
 * {{{
 * import scala.language.implicitConversions
 * 
 * import shampoo.yaml.{ *, given }
 * 
 * val user = YamlSequenceBuilder()
 *    .add(1000)
 *    .add("lupita")
 *    .add(Set("lupita", "sudoer"))
 *    .toYamlSequence()
 * 
 * assert { user(0).as[Int] == 1000 }
 * assert { user(1).as[String] == "lupita" }
 * assert { user(2).as[Set[String]] == Set("lupita", "sudoer") }
 * }}}
 *
 * @see [[YamlSequence]]
 * @see [[YamlMappingBuilder]]
 */
class YamlSequenceBuilder:
  private var node = JLinkedList[AnyRef]()

  /**
   * Adds null value to sequence.
   *
   * @return this builder
   */
  def addNull(): this.type =
    node.add(null)
    this

  /**
   * Adds value to sequence.
   *
   * @return this builder
   */
  def add(value: String): this.type =
    if value == null then
      throw NullPointerException()
    node.add(value)
    this

  /**
   * Adds value to sequence.
   *
   * @return this builder
   */
  def add(value: Boolean): this.type =
    node.add(valueOf(value))
    this

  /**
   * Adds value to sequence.
   *
   * @return this builder
   */
  def add(value: Int): this.type =
    node.add(valueOf(value))
    this

  /**
   * Adds value to sequence.
   *
   * @return this builder
   */
  def add(value: Long): this.type =
    node.add(valueOf(value))
    this

  /**
   * Adds value to sequence.
   *
   * @return this builder
   */
  def add(value: Float): this.type =
    node.add(valueOf(value))
    this

  /**
   * Adds value to sequence.
   *
   * @return this builder
   */
  def add(value: Double): this.type =
    node.add(valueOf(value))
    this

  /**
   * Adds value to sequence.
   *
   * @return this builder
   */
  def add(value: BigInt): this.type =
    node.add(valueOf(value))
    this

  /**
   * Adds value to sequence.
   *
   * @return this builder
   */
  def add(value: BigDecimal): this.type =
    node.add(valueOf(value))
    this

  /**
   * Adds value to sequence.
   *
   * @return this builder
   */
  def add(value: YamlNode): this.type =
    if value == null then throw NullPointerException()
    node.add(unwrap(value))
    this

  /** Creates sequence. */
  def toYamlSequence(): YamlSequence =
    val arr = YamlSequenceImpl(node)
    node = JLinkedList[AnyRef]()
    arr
