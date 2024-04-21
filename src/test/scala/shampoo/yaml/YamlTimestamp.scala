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

import java.time.*

given localDateConstructor: YamlConstructor[LocalDate] =
  case node: YamlString => LocalDate.parse(node.value)
  case node             => throw YamlException("Expected string node")

given localDateTimeConstructor: YamlConstructor[LocalDateTime] =
  case node: YamlString => LocalDateTime.parse(node.value)
  case node             => throw YamlException("Expected string node")

given offsetDateTimeConstructor: YamlConstructor[OffsetDateTime] =
  case node: YamlString => OffsetDateTime.parse(node.value)
  case node             => throw YamlException("Expected string node")

given instantConstructor: YamlConstructor[Instant] =
  case node: YamlString => Instant.parse(node.value)
  case node             => throw YamlException("Expected string node")

given localDateRepresenter: YamlRepresenter[LocalDate] =
  value => YamlString(value.toString)

given localDateTimeRepresenter: YamlRepresenter[LocalDateTime] =
  value => YamlString(value.toString)

given offsetDateTimeRepresenter: YamlRepresenter[OffsetDateTime] =
  value => YamlString(value.toString)

given instantRepresenter: YamlRepresenter[Instant] =
  value => YamlString(value.toString)
