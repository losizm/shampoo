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
import java.time.temporal.Temporal

import org.yaml.snakeyaml.DumperOptions
import org.yaml.snakeyaml.nodes.{ Node, ScalarNode, Tag }
import org.yaml.snakeyaml.representer.{ Represent, Representer }

private class SnakeYamlRepresenter(dumperOptions: DumperOptions) extends Representer(dumperOptions):
  // Represents float only
  private object FloatRepresenter extends Represent:
    def representData(data: AnyRef): ScalarNode =
      ScalarNode(
        Tag.FLOAT,
        data.asInstanceOf[JBigDecimal].toString,
        null, // Omit start mark
        null, // Omit end mark
        DumperOptions.ScalarStyle.PLAIN
      )

  // Represents timestamp only
  private object TimestampRepresenter extends Represent:
    def representData(data: AnyRef): ScalarNode =
      ScalarNode(
        Tag.TIMESTAMP,
        data.asInstanceOf[Temporal].toString,
        null, // Omit start mark
        null, // Omit end mark
        DumperOptions.ScalarStyle.PLAIN
      )

  representers.put(classOf[JBigDecimal], FloatRepresenter)
  representers.put(classOf[Instant], TimestampRepresenter)
  representers.put(classOf[LocalDate], TimestampRepresenter)
  representers.put(classOf[LocalDateTime], TimestampRepresenter)
  representers.put(classOf[OffsetDateTime], TimestampRepresenter)

  addClassTag(classOf[JBigDecimal], Tag.FLOAT)
  addClassTag(classOf[Instant], Tag.TIMESTAMP)
  addClassTag(classOf[LocalDate], Tag.TIMESTAMP)
  addClassTag(classOf[LocalDateTime], Tag.TIMESTAMP)
  addClassTag(classOf[OffsetDateTime], Tag.TIMESTAMP)
