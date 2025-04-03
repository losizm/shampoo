/*
 * Copyright 2025 Carlos Conyers
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

trait YamlCollectionSpec extends org.scalatest.flatspec.AnyFlatSpec:
  def assertMappingError(key: String, cause: Class[?])(test: => Any): Unit =
    val error = intercept[YamlMappingError](test)
    assert(error.key == key)
    assert(cause.isAssignableFrom(cause))

  def assertSequenceError(index: Int, cause: Class[?])(test: => Any): Unit =
    val error = intercept[YamlSequenceError](test)
    assert(error.index == index)
    assert(cause.isAssignableFrom(cause))
