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

/**
 * Defines YAML exception.
 *
 * @constructor Creates exception with message and cause.
 *
 * @param message detail message
 * @param cause   underlying cause
 */
class YamlException(message: String, cause: Throwable) extends RuntimeException(message, cause):
  /** Creates exception. */
  def this() = this(null, null)

  /**
   * Creates exception with message.
   *
   * @param message detail message
   */
  def this(message: String) = this(message, null)

  /**
   * Creates exception with cause.
   *
   * @param cause underlying cause
   */
  def this(cause: Throwable) = this(null, cause)

/**
 * Defines YAML expectation error.
 *
 * @param expected class
 * @param actual class
 */
case class YamlExpectationError(expected: Class[_], actual: Class[_])
  extends YamlException(s"Expected ${expected.getSimpleName} instead of ${actual.getSimpleName}")

/**
 * Defines YAML mapping error.
 *
 * @param key mapping key
 * @param cause underlying cause
 */
case class YamlMappingError(key: String, cause: Exception) extends YamlException(s"Error accessing key: $key", cause)

/**
 * Defines YAML sequence error.
 *
 * @param index sequence index
 * @param cause underlying cause
 */
case class YamlSequenceError(index: Int, cause: Exception) extends YamlException(s"Error accessing index: $index", cause)
