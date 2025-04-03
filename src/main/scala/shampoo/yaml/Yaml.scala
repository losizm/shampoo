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

import java.io.*
import java.nio.file.Path

/**
 * Provides YAML utilities.
 *
 * {{{
 * import scala.language.implicitConversions
 * 
 * import shampoo.yaml.{ *, given }
 * 
 * // Create YAML mapping
 * val user = Yaml.map("id" -> 1000, "name" -> "lupita")
 * 
 * // Create YAML sequence
 * val info = Yaml.seq(user, "/home/lupita", 1024)
 * 
 * // Load YAML
 * val root = Yaml.load("{ id: 0, name: root }")
 * 
 * case class User(id: Int, name: String)
 * 
 * given userRepresenter: YamlRepresenter[User] =
 *    user => Yaml.map("id" -> user.id, "name" -> user.name)
 * 
 * // Represent value to YAML
 * val nobody = Yaml.toYaml(User(65534, "nobody"))
 *
 * // Dump YAML to stdout
 * Yaml.dump(nobody, System.out)
 * }}}
 */
object Yaml:
  private val snakeYaml = new ThreadLocal[SnakeYaml]:
    override def initialValue = SnakeYaml()

  /** Creates YAML mapping from supplied key-node pairs.  */
  def map(pairs: (String, YamlNode)*): YamlMapping =
    pairs.foldLeft(YamlMappingBuilder()) {
      case (mapping, (key, node)) => mapping.add(key, node)
    }.toYamlMapping()

  /** Creates YAML sequence from supplied nodes.  */
  def seq(nodes: YamlNode*): YamlSequence =
    nodes.foldLeft(YamlSequenceBuilder()) {
      case (sequence, node) => sequence.add(node)
    }.toYamlSequence()

  /**
   * Represents data.
   *
   * @param data native data
   * @param representer data representer
   */
  def toYaml[T](data: T)(using representer: YamlRepresenter[T]): YamlNode =
    representer.represent(data)

  /**
   * Loads YAML.
   *
   * @throws YamlException if YAML cannot be loaded from input
   */
  def load(input: String): YamlNode =
    load(StringReader(input))

  /**
   * Loads YAML.
   *
   * @throws YamlException if YAML cannot be loaded from input
   */
  def load(input: Reader): YamlNode =
    snakeYaml.get.load(input)

  /**
   * Loads YAML.
   *
   * @throws YamlException if YAML cannot be loaded from input
   */
  def load(input: InputStream): YamlNode =
    load(InputStreamReader(input))

  /**
   * Loads YAML.
   *
   * @throws YamlException if YAML cannot be loaded from input
   */
  def load(input: File): YamlNode =
    val reader = FileReader(input)
    try load(reader) finally reader.close()

  /**
   * Loads YAML.
   *
   * @throws YamlException if YAML cannot be loaded from input
   */
  def load(input: Path): YamlNode =
    load(input.toFile)

  /** Dumps YAML. */
  def dump(yaml: YamlNode): String =
    val out = StringWriter()
    dump(yaml, out)
    out.toString

  /** Dumps YAML to supplied output. */
  def dump(yaml: YamlNode, output: Writer): Unit =
    snakeYaml.get.dump(yaml, output)

  /** Dumps YAML to supplied output. */
  def dump(yaml: YamlNode, output: OutputStream): Unit =
    dump(yaml, OutputStreamWriter(output))

  /** Dumps YAML to supplied output. */
  def dump(yaml: YamlNode, output: File): Unit =
    val writer = FileWriter(output)
    try dump(yaml, writer) finally writer.close()

  /** Dumps YAML to supplied output. */
  def dump(yaml: YamlNode, output: Path): Unit =
    dump(yaml, output.toFile)
