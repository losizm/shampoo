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

import java.util.Optional

import org.snakeyaml.engine.v2.api.{ DumpSettings, RepresentToNode }
import org.snakeyaml.engine.v2.common.ScalarStyle
import org.snakeyaml.engine.v2.nodes.{ ScalarNode, Tag }
import org.snakeyaml.engine.v2.representer.StandardRepresenter

private class SnakeYamlRepresenter(val dumpSettings: DumpSettings) extends StandardRepresenter(dumpSettings):
  // Represents float only
  private object FloatRepresenter extends RepresentToNode:
    def representData(data: AnyRef): ScalarNode =
      ScalarNode(
        Tag.FLOAT,                               // tag
        true,                                    // resolved
        data.asInstanceOf[JBigDecimal].toString, // value
        ScalarStyle.PLAIN,                       // style
        Optional.empty(),                        // startMark
        Optional.empty()                         // endMark
      )

  representers.put(classOf[JBigDecimal], FloatRepresenter)
