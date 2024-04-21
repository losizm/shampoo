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

import java.io.{ IOException, Writer }
import org.snakeyaml.engine.v2.api.StreamDataWriter

private class SnakeYamlWriter(out: Writer) extends StreamDataWriter:
  override def flush() =
    tryIO { out.flush() }

  def write(str: String): Unit =
    tryIO { out.write(str) }

  def write(str: String, off: Int, len: Int): Unit =
    tryIO { out.write(str, off, len) }

  private def tryIO(io: => Unit): Unit =
    try
      io
    catch case cause: IOException =>
      throw YamlIoException(cause)
