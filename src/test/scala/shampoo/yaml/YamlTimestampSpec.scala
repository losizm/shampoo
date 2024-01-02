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

import java.time.*

import scala.collection.immutable.{ ListMap, TreeMap }

class YamlTimestampSpec extends org.scalatest.flatspec.AnyFlatSpec:
  it should "construct and represent YAML containing dates" in {
    val yaml = Yaml.load("""
      foo: 1983-03-01
      bar: 1954-12-28
      baz: 1962-07-31
      qux: 1977-01-31
    """)

    val dob = yaml.as[ListMap[String, LocalDate]]
    assert(dob.size == 4)
    assert(dob.keySet == Set("foo", "bar", "baz", "qux"))
    assert(dob("foo") == LocalDate.parse("1983-03-01"))
    assert(dob("bar") == LocalDate.parse("1954-12-28"))
    assert(dob("baz") == LocalDate.parse("1962-07-31"))
    assert(dob("qux") == LocalDate.parse("1977-01-31"))
    info(s"\n${Yaml.dump(yaml)}")
  }

  it should "construct and represent YAML containing date-times" in {
    val yaml = Yaml.load("""
      foo: 1983-03-01T12:22:11
      bar: 1954-12-28T13:33:22
      baz: 1962-07-31T14:44:33
      qux: 1977-01-31T15:55:44
    """)

    val dob = yaml.as[ListMap[String, LocalDateTime]]
    assert(dob.size == 4)
    assert(dob.keySet == Set("foo", "bar", "baz", "qux"))
    assert(dob("foo") == LocalDateTime.parse("1983-03-01T12:22:11"))
    assert(dob("bar") == LocalDateTime.parse("1954-12-28T13:33:22"))
    assert(dob("baz") == LocalDateTime.parse("1962-07-31T14:44:33"))
    assert(dob("qux") == LocalDateTime.parse("1977-01-31T15:55:44"))
    info(s"\n${Yaml.dump(yaml)}")
  }

  it should "construct and represent YAML containing offset date-times" in {
    val yaml = Yaml.load("""
      foo: 1983-03-01T12:22:11-05:00
      bar: 1954-12-28T13:33:22-06:00
      baz: 1962-07-31T14:44:33+06:30
      qux: 1977-01-31T15:55:44+00:00
    """)

    val dob = yaml.as[ListMap[String, OffsetDateTime]]
    assert(dob.size == 4)
    assert(dob.keySet == Set("foo", "bar", "baz", "qux"))
    assert(dob("foo") == OffsetDateTime.parse("1983-03-01T12:22:11-05:00"))
    assert(dob("bar") == OffsetDateTime.parse("1954-12-28T13:33:22-06:00"))
    assert(dob("baz") == OffsetDateTime.parse("1962-07-31T14:44:33+06:30"))
    assert(dob("qux") == OffsetDateTime.parse("1977-01-31T15:55:44+00:00"))
    info(s"\n${Yaml.dump(yaml)}")
  }

  it should "construct and represent YAML containing instants" in {
    val yaml = Yaml.load("""
      foo: 1983-03-01T12:22:11Z
      bar: 1954-12-28T13:33:22Z
      baz: 1962-07-31T14:44:33Z
      qux: 1977-01-31T15:55:44Z
    """)

    val dob = yaml.as[ListMap[String, Instant]]
    assert(dob.size == 4)
    assert(dob.keySet == Set("foo", "bar", "baz", "qux"))
    assert(dob("foo") == Instant.parse("1983-03-01T12:22:11Z"))
    assert(dob("bar") == Instant.parse("1954-12-28T13:33:22Z"))
    assert(dob("baz") == Instant.parse("1962-07-31T14:44:33Z"))
    assert(dob("qux") == Instant.parse("1977-01-31T15:55:44Z"))
    info(s"\n${Yaml.dump(yaml)}")
  }

  it should "build YAML containing mixed timestamps (1)" in {
    val yaml = YamlMappingBuilder()
      .add("foo", LocalDate.parse("1983-03-01"))
      .add("bar", LocalDateTime.parse("1954-12-28T13:33:22"))
      .add("baz", OffsetDateTime.parse("1962-07-31T14:44:33+06:30"))
      .add("qux", Instant.parse("1977-01-31T15:55:44Z"))
      .add("quux", YamlSequenceBuilder()
        .add(LocalDate.parse("1983-03-01"))
        .add(LocalDateTime.parse("1954-12-28T13:33:22"))
        .add(OffsetDateTime.parse("1962-07-31T14:44:33+06:30"))
        .add(Instant.parse("1977-01-31T15:55:44Z"))
        .toYamlSequence()
      ).toYamlMapping()

    info(s"\n${Yaml.dump(yaml)}")
    verify(yaml)
  }

  it should "build YAML containing mixed timestamps (2)" in {
    import scala.language.implicitConversions

    val yaml = Yaml.map(
      "foo"  -> LocalDate.parse("1983-03-01"),
      "bar"  -> LocalDateTime.parse("1954-12-28T13:33:22"),
      "baz"  -> OffsetDateTime.parse("1962-07-31T14:44:33+06:30"),
      "qux"  -> Instant.parse("1977-01-31T15:55:44Z"),
      "quux" -> Yaml.seq(
        LocalDate.parse("1983-03-01"),
        LocalDateTime.parse("1954-12-28T13:33:22"),
        OffsetDateTime.parse("1962-07-31T14:44:33+06:30"),
        Instant.parse("1977-01-31T15:55:44Z")
      )
    )

    info(s"\n${Yaml.dump(yaml)}")
    verify(yaml)
  }

  private def verify(yaml: YamlMapping): Unit =
    import scala.language.implicitConversions

    assert(yaml.size == 5)
    assert(yaml.keys == Set("foo", "bar", "baz", "qux", "quux"))

    assert(yaml("foo") == YamlTimestamp(LocalDate.parse("1983-03-01")))
    assert(yaml.getLocalDate("foo") == LocalDate.parse("1983-03-01"))
    assertThrows[DateTimeException](yaml.getLocalDateTime("foo"))
    assertThrows[DateTimeException](yaml.getOffsetDateTime("foo"))
    assertThrows[DateTimeException](yaml.getInstant("foo"))

    assert(yaml("bar") == YamlTimestamp(LocalDateTime.parse("1954-12-28T13:33:22")))
    assertThrows[DateTimeException](yaml.getLocalDate("bar"))
    assertThrows[DateTimeException](yaml.getLocalDate("bar"))
    assert(yaml.getLocalDateTime("bar") == LocalDateTime.parse("1954-12-28T13:33:22"))
    assertThrows[DateTimeException](yaml.getOffsetDateTime("bar"))
    assertThrows[DateTimeException](yaml.getInstant("bar"))

    assert(yaml("baz") == YamlTimestamp(OffsetDateTime.parse("1962-07-31T14:44:33+06:30")))
    assertThrows[DateTimeException](yaml.getLocalDate("baz"))
    assertThrows[DateTimeException](yaml.getLocalDateTime("baz"))
    assert(yaml.getOffsetDateTime("baz") == OffsetDateTime.parse("1962-07-31T14:44:33+06:30"))
    assert(yaml.getInstant("baz") == Instant.parse("1962-07-31T08:14:33Z"))

    assert(yaml("qux") == YamlTimestamp(Instant.parse("1977-01-31T15:55:44Z")))
    assertThrows[DateTimeException](yaml.getLocalDate("qux"))
    assertThrows[DateTimeException](yaml.getLocalDateTime("qux"))
    assert(yaml.getOffsetDateTime("qux") == OffsetDateTime.parse("1977-01-31T15:55:44+00:00"))
    assert(yaml.getInstant("qux") == Instant.parse("1977-01-31T15:55:44Z"))

    assert(yaml("quux")(0) == YamlTimestamp(LocalDate.parse("1983-03-01")))
    assert(yaml("quux").getLocalDate(0) == LocalDate.parse("1983-03-01"))
    assertThrows[DateTimeException](yaml("quux").getLocalDateTime(0))
    assertThrows[DateTimeException](yaml("quux").getOffsetDateTime(0))
    assertThrows[DateTimeException](yaml("quux").getInstant(0))

    assert(yaml("quux")(1) == YamlTimestamp(LocalDateTime.parse("1954-12-28T13:33:22")))
    assertThrows[DateTimeException](yaml("quux").getLocalDate(1))
    assert(yaml("quux").getLocalDateTime(1) == LocalDateTime.parse("1954-12-28T13:33:22"))
    assertThrows[DateTimeException](yaml("quux").getOffsetDateTime(1))
    assertThrows[DateTimeException](yaml("quux").getInstant(1))

    assert(yaml("quux")(2) == YamlTimestamp(OffsetDateTime.parse("1962-07-31T14:44:33+06:30")))
    assertThrows[DateTimeException](yaml("quux").getLocalDate(2))
    assertThrows[DateTimeException](yaml("quux").getLocalDateTime(2))
    assert(yaml("quux").getOffsetDateTime(2) == OffsetDateTime.parse("1962-07-31T14:44:33+06:30"))
    assert(yaml("quux").getInstant(2) == Instant.parse("1962-07-31T08:14:33Z"))

    assert(yaml("quux")(3) == YamlTimestamp(Instant.parse("1977-01-31T15:55:44Z")))
    assertThrows[DateTimeException](yaml("quux").getLocalDate(3))
    assertThrows[DateTimeException](yaml("quux").getLocalDateTime(3))
    assert(yaml("quux").getOffsetDateTime(3) == OffsetDateTime.parse("1977-01-31T15:55:44+00:00"))
    assert(yaml("quux").getInstant(3) == Instant.parse("1977-01-31T15:55:44Z"))
