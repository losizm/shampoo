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

import scala.collection.immutable.ListMap

class YamlNumberSpec extends org.scalatest.flatspec.AnyFlatSpec:
  it should "construct and represent YAML containing ints" in {
    val yaml = Yaml.load("""
      foo: 1
      bar: 12
      baz: 123
      qux: 1234
    """)

    val dob = yaml.as[ListMap[String, Int]]
    assert(dob.size == 4)
    assert(dob.keySet == Set("foo", "bar", "baz", "qux"))
    assert(dob("foo") == 1)
    assert(dob("bar") == 12)
    assert(dob("baz") == 123)
    assert(dob("qux") == 1234)
    info(s"\n${Yaml.dump(yaml)}")
  }

  it should "construct and represent YAML containing longs" in {
    val yaml = Yaml.load("""
      foo: 1234
      bar: 12345678
      baz: 123456789012
      qux: 1234567890123456
    """)

    val dob = yaml.as[ListMap[String, Long]]
    assert(dob.size == 4)
    assert(dob.keySet == Set("foo", "bar", "baz", "qux"))
    assert(dob("foo") == 1234L)
    assert(dob("bar") == 12345678L)
    assert(dob("baz") == 123456789012L)
    assert(dob("qux") == 1234567890123456L)
    info(s"\n${Yaml.dump(yaml)}")
  }

  it should "construct and represent YAML containing floats" in {
    val yaml = Yaml.load("""
      foo: 1.0
      bar: 12.01
      baz: 123.012
      qux: 1234.01234
    """)

    val dob = yaml.as[ListMap[String, Float]]
    assert(dob.size == 4)
    assert(dob.keySet == Set("foo", "bar", "baz", "qux"))
    assert(dob("foo") == 1.0f)
    assert(dob("bar") == 12.01f)
    assert(dob("baz") == 123.012f)
    assert(dob("qux") == 1234.01234f)
    info(s"\n${Yaml.dump(yaml)}")
  }

  it should "construct and represent YAML containing doubles" in {
    val yaml = Yaml.load("""
      foo: 1.0
      bar: 12.01
      baz: 123.012
      qux: 1234.01234
    """)

    val dob = yaml.as[ListMap[String, Double]]
    assert(dob.size == 4)
    assert(dob.keySet == Set("foo", "bar", "baz", "qux"))
    assert(dob("foo") == 1.0)
    assert(dob("bar") == 12.01)
    assert(dob("baz") == 123.012)
    assert(dob("qux") == 1234.01234)
    info(s"\n${Yaml.dump(yaml)}")
  }

  it should "construct and represent YAML containing big ints" in {
    val yaml = Yaml.load("""
      foo: 123456789
      bar: 1234567890123456789
      baz: 12345678901234567890123456789
      qux: 123456789012345678901234567890123456789
    """)

    val dob = yaml.as[ListMap[String, BigInt]]
    assert(dob.size == 4)
    assert(dob.keySet == Set("foo", "bar", "baz", "qux"))
    assert(dob("foo") == BigInt("123456789"))
    assert(dob("bar") == BigInt("1234567890123456789"))
    assert(dob("baz") == BigInt("12345678901234567890123456789"))
    assert(dob("qux") == BigInt("123456789012345678901234567890123456789"))
    info(s"\n${Yaml.dump(yaml)}")
  }

  it should "construct and represent YAML containing big decimals" in {
    val yaml = Yaml.load("""
      foo: 123456789.123
      bar: 1234567890123456789.123456
      baz: 12345678901234567890123456789.123456789
      qux: 123456789012345678901234567890123456789.1234567890123456789
    """)

    val dob = yaml.as[ListMap[String, BigDecimal]]
    assert(dob.size == 4)
    assert(dob.keySet == Set("foo", "bar", "baz", "qux"))
    assert(dob("foo") == BigDecimal("123456789.123"))
    assert(dob("bar") == BigDecimal("1234567890123456789.123456"))
    assert(dob("baz") == BigDecimal("12345678901234567890123456789.123456789"))
    assert(dob("qux") == BigDecimal("123456789012345678901234567890123456789.1234567890123456789"))
    info(s"\n${Yaml.dump(yaml)}")
  }

  it should "build YAML containing mixed numbers (1)" in {
    val yaml = YamlMappingBuilder()
      .add("foo", 1234567890123456L)
      .add("bar", 1234.01234)
      .add("baz", BigInt("123456789012345678901234567890123456789"))
      .add("qux", BigDecimal("123456789012345678901234567890123456789.1234567890123456789"))
      .add("quux", YamlSequenceBuilder()
        .add(1234567890123456L)
        .add(1234.01234)
        .add(BigInt("123456789012345678901234567890123456789"))
        .add(BigDecimal("123456789012345678901234567890123456789.1234567890123456789"))
        .toYamlSequence()
      ).toYamlMapping()

    verify(yaml)
    info(s"\n${Yaml.dump(yaml)}")
  }

  it should "build YAML containing mixed numbers (2)" in {
    import scala.language.implicitConversions

    val yaml = Yaml.map(
      "foo"  -> 1234567890123456L,
      "bar"  -> 1234.01234,
      "baz"  -> BigInt("123456789012345678901234567890123456789"),
      "qux"  -> BigDecimal("123456789012345678901234567890123456789.1234567890123456789"),
      "quux" -> Yaml.seq(
        1234567890123456L,
        1234.01234,
        BigInt("123456789012345678901234567890123456789"),
        BigDecimal("123456789012345678901234567890123456789.1234567890123456789")
      )
    )

    verify(yaml)
    info(s"\n${Yaml.dump(yaml)}")
  }

  private def verify(yaml: YamlMapping): Unit =
    import scala.language.implicitConversions

    assert(yaml.size == 5)
    assert(yaml.keys == Set("foo", "bar", "baz", "qux", "quux"))

    assert(yaml("foo") == YamlNumber(1234567890123456L))
    assertThrows[ArithmeticException](yaml.getInt("foo"))
    assert(yaml.getLong("foo") == 1234567890123456L)
    assert(yaml.getDouble("foo") == 1234567890123456.0)
    assert(yaml.getBigInt("foo") == BigInt("1234567890123456"))
    assert(yaml.getBigDecimal("foo") == BigDecimal("1234567890123456"))

    assertThrows[ArithmeticException](yaml.getInt("bar"))
    assertThrows[ArithmeticException](yaml.getLong("bar"))
    assert(yaml.getFloat("bar") == 1234.01234f)
    assert(yaml.getDouble("bar") == 1234.01234)
    assertThrows[ArithmeticException](yaml.getBigInt("bar"))
    assert(yaml.getBigDecimal("bar").toDouble == BigDecimal(1234.01234).toDouble)

    assert(yaml("baz") == YamlNumber(BigInt("123456789012345678901234567890123456789")))
    assertThrows[ArithmeticException](yaml.getInt("baz"))
    assertThrows[ArithmeticException](yaml.getLong("baz"))
    assert(yaml.getBigInt("baz") == BigInt("123456789012345678901234567890123456789"))
    assert(yaml.getBigDecimal("baz") == BigDecimal("123456789012345678901234567890123456789"))

    assert(yaml("qux") == YamlNumber(BigDecimal("123456789012345678901234567890123456789.1234567890123456789")))
    assertThrows[ArithmeticException](yaml.getInt("qux"))
    assertThrows[ArithmeticException](yaml.getLong("qux"))
    assertThrows[ArithmeticException](yaml.getBigInt("qux"))
    assert(yaml.getBigDecimal("qux") == BigDecimal("123456789012345678901234567890123456789.1234567890123456789"))

    assert(yaml("quux")(0) == YamlNumber(1234567890123456L))
    assertThrows[ArithmeticException](yaml("quux").getInt(0))
    assert(yaml("quux").getLong(0) == 1234567890123456L)
    assert(yaml("quux").getDouble(0) == 1234567890123456.0)
    assert(yaml("quux").getBigInt(0) == BigInt("1234567890123456"))
    assert(yaml("quux").getBigDecimal(0) == BigDecimal("1234567890123456"))

    assertThrows[ArithmeticException](yaml("quux").getInt(1))
    assertThrows[ArithmeticException](yaml("quux").getLong(1))
    assert(yaml("quux").getFloat(1) == 1234.01234f)
    assert(yaml("quux").getDouble(1) == 1234.01234)
    assertThrows[ArithmeticException](yaml("quux").getBigInt(1))
    assert(yaml("quux").getBigDecimal(1).toDouble == BigDecimal(1234.01234).toDouble)

    assert(yaml("quux")(2) == YamlNumber(BigInt("123456789012345678901234567890123456789")))
    assertThrows[ArithmeticException](yaml("quux").getInt(2))
    assertThrows[ArithmeticException](yaml("quux").getLong(2))
    assert(yaml("quux").getBigInt(2) == BigInt("123456789012345678901234567890123456789"))
    assert(yaml("quux").getBigDecimal(2) == BigDecimal("123456789012345678901234567890123456789"))

    assert(yaml("quux")(3) == YamlNumber(BigDecimal("123456789012345678901234567890123456789.1234567890123456789")))
    assertThrows[ArithmeticException](yaml("quux").getInt(3))
    assertThrows[ArithmeticException](yaml("quux").getLong(3))
    assertThrows[ArithmeticException](yaml("quux").getBigInt(3))
    assert(yaml("quux").getBigDecimal(3) == BigDecimal("123456789012345678901234567890123456789.1234567890123456789"))
