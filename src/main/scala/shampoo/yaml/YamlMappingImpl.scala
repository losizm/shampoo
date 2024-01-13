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

import scala.jdk.javaapi.CollectionConverters.asScala

import YamlValues.*

private class YamlMappingImpl(private[yaml] val value: JMap[String, AnyRef]) extends AbstractYamlMapping:
  val size = value.size()

  lazy val keys  = asScala(value.keySet).toSet
  lazy val toMap = asScala(value).map(_ -> wrap(_)).toMap

  def contains(key: String): Boolean =
    value.containsKey(key)

  def apply(key: String): YamlNode =
    wrap(getValue(key))

  private def getValue(key: String): AnyRef =
    value.getOrDefault(key, "b4efcad99f60423a") match
      case "b4efcad99f60423a" => throw new NoSuchElementException(key)
      case value              => value
