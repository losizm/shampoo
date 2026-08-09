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

import scala.reflect.ClassTag

/** Defines type alias for YAML node parameter. */
type YamlNodeParam = YamlNode | String | Boolean | Int | Long | Float | Double | BigInt | BigDecimal

private def ToYamlNode: PartialFunction[YamlNodeParam, YamlNode] =
  case value: YamlNode   => value
  case value: String     => YamlString(value)
  case value: Boolean    => YamlBoolean(value)
  case value: Int        => YamlNumber(value)
  case value: Long       => YamlNumber(value)
  case value: Float      => YamlNumber(value)
  case value: Double     => YamlNumber(value)
  case value: BigInt     => YamlNumber(value)
  case value: BigDecimal => YamlNumber(value)

private type JList[A]    = java.util.List[A]
private type JMap[K, V]  = java.util.Map[K, V]
private type JBoolean    = java.lang.Boolean
private type JInteger    = java.lang.Integer
private type JLong       = java.lang.Long
private type JFloat      = java.lang.Float
private type JDouble     = java.lang.Double
private type JBigInteger = java.math.BigInteger
private type JBigDecimal = java.math.BigDecimal

private inline def expect[T <: YamlNode](node: YamlNode)(using ctag: ClassTag[T]): T =
  try
    node.asInstanceOf[T]
  catch case _: ClassCastException =>
    throw YamlExpectationError(s"Expected ${ctag.runtimeClass.getSimpleName} instead of ${YamlNodeName(node)}")

private def YamlNodeName[T <: YamlNode]: PartialFunction[T, String] =
  case YamlNull        => "YamlNull"
  case _: YamlString   => "YamlString"
  case _: YamlNumber   => "YamlNumber"
  case _: YamlBoolean  => "YamlBoolean"
  case _: YamlMapping  => "YamlMapping"
  case _: YamlSequence => "YamlSequence"

private def yamlNodeType[T <: YamlNode](node: YamlNode): Class[_] =
  node match
    case YamlNull                => classOf[YamlNull.type]
    case _: YamlString           => classOf[YamlString]
    case _: YamlNumber           => classOf[YamlNumber]
    case _: YamlBoolean          => classOf[YamlBoolean]
    case _: YamlMapping          => classOf[YamlMapping]
    case _: YamlSequence         => classOf[YamlSequence]
