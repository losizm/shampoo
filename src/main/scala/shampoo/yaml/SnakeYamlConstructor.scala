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

import java.math.BigDecimal as JBigDecimal
import java.time.*

import scala.util.Try

import org.yaml.snakeyaml.LoaderOptions
import org.yaml.snakeyaml.constructor.Constructor
import org.yaml.snakeyaml.nodes.{ Node, ScalarNode, Tag }

private class SnakeYamlConstructor(loaderOptions: LoaderOptions) extends Constructor(loaderOptions):
  // Constructs float and timestamp only
  override def constructObject(node: Node): AnyRef =
    node.getTag match
      case Tag.FLOAT     => getNumber(node.asInstanceOf)
      case Tag.TIMESTAMP => getTimestamp(node.asInstanceOf)
      case _             => super.constructObject(node)

  private def getNumber(node: ScalarNode): AnyRef =
    JBigDecimal(node.getValue)

  private def getTimestamp(node: ScalarNode): AnyRef =
    Try(Instant.parse(node.getValue))
      .orElse(Try(OffsetDateTime.parse(node.getValue)))
      .orElse(Try(LocalDateTime.parse(node.getValue)))
      .orElse(Try(LocalDate.parse(node.getValue)))
      .get
