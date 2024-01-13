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

import java.lang.{ Boolean as JBoolean, Double as JDouble, Integer as JInteger, Long as JLong }
import java.math.BigDecimal as JBigDecimal
import java.time.*
import java.time.temporal.Temporal

import scala.jdk.javaapi.CollectionConverters.asScala

private object YamlValues:
  def wrap(value: AnyRef): YamlNode =
    value match
      case null                  => YamlNull
      case value: String         => YamlStringImpl(value)
      case value: JBoolean       => YamlBooleanImpl(value)
      case value: JInteger       => YamlNumberImpl(BigDecimal(value))
      case value: JLong          => YamlNumberImpl(BigDecimal(value))
      case value: JDouble        => YamlNumberImpl(BigDecimal(value))
      case value: JBigInteger    => YamlNumberImpl(BigDecimal(value))
      case value: JBigDecimal    => YamlNumberImpl(BigDecimal(value))
      case value: LocalDate      => YamlTimestampImpl(value)
      case value: LocalDateTime  => YamlTimestampImpl(value)
      case value: OffsetDateTime => YamlTimestampImpl(value)
      case value: Instant        => YamlTimestampImpl(value)
      case value: JList[?]       => YamlSequenceImpl(value.asInstanceOf)
      case value: JMap[?, ?]     => YamlMappingImpl(value.asInstanceOf)
      case _                     => throw YamlException(s"Unsupported representation: ${value.getClass.getName}")

  def unwrap(yaml: YamlNode): AnyRef =
    yaml match
      case YamlNull             => null
      case node: YamlString     => node.value
      case node: YamlBoolean    => valueOf(node.value)
      case node: YamlNumber     => valueOf(node.value)
      case node: YamlTimestamp  => valueOf(node.value)
      case node: YamlCollection => node.value

  def valueOf(boolean: Boolean): JBoolean = JBoolean.valueOf(boolean)
  def valueOf(int: Int): Number = JInteger.valueOf(int)
  def valueOf(long: Long): Number = JLong.valueOf(long)
  def valueOf(float: Float): Number = JBigDecimal(float)
  def valueOf(double: Double): Number = JBigDecimal(double)
  def valueOf(bigInt: BigInt): Number = bigInt.bigInteger

  def valueOf(bigDecimal: BigDecimal): Number =
    bigDecimal.isWhole match
      case true  => valueOf(bigDecimal.toBigInt)
      case false => bigDecimal.bigDecimal

  def valueOf(temporal: Temporal): Temporal =
    temporal
