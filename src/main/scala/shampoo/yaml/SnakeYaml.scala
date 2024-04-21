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

import java.io.{ Reader, Writer }

import org.snakeyaml.engine.v2.api.{ Dump, DumpSettings, Load, LoadSettings }
import org.snakeyaml.engine.v2.common.{ FlowStyle, ScalarStyle }

private class SnakeYaml:
  private val loadSettings = LoadSettings.builder()
    .setParseComments(false)
    .setAllowDuplicateKeys(false)
    .setAllowRecursiveKeys(false)
    .build()
  
  private val dumpSettings = DumpSettings.builder()
    .setIndent(2)
    .setDefaultScalarStyle(ScalarStyle.PLAIN)
    .setDefaultFlowStyle(FlowStyle.BLOCK)
    .build()

  private val constructor = SnakeYamlConstructor(loadSettings)
  private val representer = SnakeYamlRepresenter(dumpSettings)
  private val loader      = Load(loadSettings, constructor)
  private val dumper      = Dump(dumpSettings, representer)

  def load(in: Reader): YamlNode =
    try
      val node = loader.loadFromReader(in)
      YamlValues.wrap(node)
    catch case cause: Exception =>
      throw YamlException("Cannot load YAML", cause)

  def dump(yaml: YamlNode, out: Writer): Unit =
    try
      val node = representer.represent(YamlValues.unwrap(yaml))
      dumper.dumpNode(node, SnakeYamlWriter(out))
    catch case cause: Exception =>
      throw YamlException("Cannot dump YAML", cause)
