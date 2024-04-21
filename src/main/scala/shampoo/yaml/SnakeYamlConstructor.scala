/*
 * Copyright 2024 Carlos Conyers
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

import org.snakeyaml.engine.v2.api.LoadSettings
import org.snakeyaml.engine.v2.constructor.StandardConstructor
import org.snakeyaml.engine.v2.nodes.{ Node, ScalarNode, Tag }

private class SnakeYamlConstructor(val loadSettings: LoadSettings) extends StandardConstructor(loadSettings):
  // Overrides float construct
  override def constructObject(node: Node): AnyRef =
    node.getTag match
      case Tag.FLOAT => getNumber(node.asInstanceOf)
      case _         => super.constructObject(node)

  private def getNumber(node: ScalarNode): AnyRef =
    JBigDecimal(node.getValue)
