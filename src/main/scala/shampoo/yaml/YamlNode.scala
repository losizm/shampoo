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
import java.time.temporal.Temporal

/** Defines YAML node. */
sealed trait YamlNode:
  /**
   * Constructs data.
   *
   * @param constructor data constructor
   */
  def as[T](using constructor: YamlConstructor[T]) =
    constructor.construct(this)

/** Defines YAML scalar. */
sealed trait YamlScalar extends YamlNode

/** Defines YAML string. */
sealed trait YamlString extends YamlScalar:
  /** Gets value. */
  def value: String

/** Provides YAML string constructor. */
object YamlString:
  /**
   * Constructs YAML string.
   *
   * @param value string
   */
  def apply(value: String): YamlString =
    YamlStringImpl(value)

/** Defines YAML boolean. */
sealed trait YamlBoolean extends YamlScalar:
  /** Gets value. */
  def value: Boolean

/** Provides YAML boolean constructor. */
object YamlBoolean:
  /**
   * Constructs YAML boolean.
   *
   * @param value boolean
   */
  def apply(value: Boolean): YamlBoolean =
    YamlBooleanImpl(value)

/** Defines YAML null. */
case object YamlNull extends YamlScalar

/** Defines YAML number. */
sealed trait YamlNumber extends YamlScalar:
  /**
   * Gets number as `Int`.
   *
   * @throws java.lang.ArithmeticException if conversion is not exact.
   */
  def toInt: Int

  /**
   * Gets number as `Long`.
   *
   * @throws java.lang.ArithmeticException if conversion is not exact.
   */
  def toLong: Long

  /** Gets number as `Float`. */
  def toFloat: Float

  /** Gets number as `Double`. */
  def toDouble: Double

  /** Gets number as `BigInt`. */
  def toBigInt: BigInt

  /** Gets number as `BigDecimal`. */
  def toBigDecimal: BigDecimal

  private[yaml] def value: BigDecimal

/** Provides YAML number constructor. */
object YamlNumber:
  /**
   * Constructs YAML number.
   *
   * @param value int
   */
  def apply(value: Int): YamlNumber =
    YamlNumberImpl(BigDecimal(value))

  /**
   * Constructs YAML number.
   *
   * @param value long
   */
  def apply(value: Long): YamlNumber =
    YamlNumberImpl(BigDecimal(value))

  /**
   * Constructs YAML number.
   *
   * @param value float
   */
  def apply(value: Float): YamlNumber =
    YamlNumberImpl(BigDecimal(value))

  /**
   * Constructs YAML number.
   *
   * @param value double
   */
  def apply(value: Double): YamlNumber =
    YamlNumberImpl(BigDecimal(value))

  /**
   * Constructs YAML number.
   *
   * @param value big int
   */
  def apply(value: BigInt): YamlNumber =
    YamlNumberImpl(BigDecimal(value))

  /**
   * Constructs YAML number.
   *
   * @param value big decimal
   */
  def apply(value: BigDecimal): YamlNumber =
    YamlNumberImpl(value)

/** Defines YAML timestamp. */
sealed trait YamlTimestamp extends YamlScalar:
  /** Gets timestamp as `LocalDate`. */
  def toLocalDate: LocalDate

  /** Gets timestamp as `LocalDateTime`. */
  def toLocalDateTime: LocalDateTime

  /** Gets timestamp as `OffsetDateTime`. */
  def toOffsetDateTime: OffsetDateTime

  /** Gets timestamp as `Instant`. */
  def toInstant: Instant

  private[yaml] def value: Temporal

/** Provides YAML timestamp constructors. */
object YamlTimestamp:
  /**
   * Constructs YAML timestamp.
   *
   * @param value local date
   */
  def apply(value: LocalDate): YamlTimestamp =
    YamlTimestampImpl(value)

  /**
   * Constructs YAML timestamp.
   *
   * @param value local date-time
   */
  def apply(value: LocalDateTime): YamlTimestamp =
    YamlTimestampImpl(value)

  /**
   * Constructs YAML timestamp.
   *
   * @param value offset date-time
   */
  def apply(value: OffsetDateTime): YamlTimestamp =
    YamlTimestampImpl(value)

  /**
   * Constructs YAML timestamp.
   *
   * @param value instant
   */
  def apply(value: Instant): YamlTimestamp =
    YamlTimestampImpl(value)
/** Defines YAML collection. */
sealed trait YamlCollection extends YamlNode:
  /** Gets collection size. */
  def size: Int

  /** Tests for empty. */
  def isEmpty: Boolean = size == 0

  /** Tests for non-empty. */
  def nonEmpty: Boolean = size > 0

  private[yaml] def value: AnyRef

/**
 * Defines YAML mapping.
 *
 * @see [[YamlMappingBuilder]]
 */
sealed trait YamlMapping extends YamlCollection:
  /** Gets keys. */
  def keys: Set[String]

  /** Gets key-node map. */
  def toMap: Map[String, YamlNode]

  /**
   * Tests for key.
   *
   * @param key mapping key
   */
  def contains(key: String): Boolean

  /**
   * Gets node.
   *
   * @param key mapping key
   *
   * @throws java.util.NoSuchElementException if key or value does not exist
   */
  def apply(key: String): YamlNode

  /**
   * Optionally gets node.
   *
   * @param key mapping key
   */
  def get(key: String): Option[YamlNode] =
    try Some(apply(key))
    catch case _: NoSuchElementException => None

  /**
   * Gets node or returns default node.
   *
   * @param key mapping key
   * @param default default node
   */
  def getOrElse(key: String, default: => YamlNode): YamlNode =
    get(key).getOrElse(default)

  /**
   * Tests for null.
   *
   * @param key mapping key
   *
   * @throws java.util.NoSuchElementException if key does not exist
   */
  def isNull(key: String): Boolean =
    apply(key) == YamlNull

  /**
   * Gets node as `String`.
   *
   * @param key mapping key
   *
   * @throws java.util.NoSuchElementException if key does not exist
   * @throws java.lang.NullPointerException if node is null
   */
  def getString(key: String): String =
    read(key)

  /**
   * Gets node as `Boolean`.
   *
   * @param key mapping key
   *
   * @throws java.util.NoSuchElementException if key does not exist
   * @throws java.lang.NullPointerException if node is null
   */
  def getBoolean(key: String): Boolean =
    read(key)

  /**
   * Gets node as `Int`.
   *
   * @param key mapping key
   *
   * @throws java.util.NoSuchElementException if key does not exist
   * @throws java.lang.NullPointerException if node is null
   */
  def getInt(key: String): Int =
    read(key)

  /**
   * Gets node as `Long`.
   *
   * @param key mapping key
   *
   * @throws java.util.NoSuchElementException if key does not exist
   * @throws java.lang.NullPointerException if node is null
   */
  def getLong(key: String): Long =
    read(key)

  /**
   * Gets node as `Float`.
   *
   * @param key mapping key
   *
   * @throws java.util.NoSuchElementException if key does not exist
   * @throws java.lang.NullPointerException if node is null
   */
  def getFloat(key: String): Float =
    read(key)

  /**
   * Gets node as `Double`.
   *
   * @param key mapping key
   *
   * @throws java.util.NoSuchElementException if key does not exist
   * @throws java.lang.NullPointerException if node is null
   */
  def getDouble(key: String): Double =
    read(key)

  /**
   * Gets node as `BigInt`.
   *
   * @param key mapping key
   *
   * @throws java.util.NoSuchElementException if key does not exist
   * @throws java.lang.NullPointerException if node is null
   */
  def getBigInt(key: String): BigInt =
    read(key)

  /**
   * Gets node as `BigDecimal`.
   *
   * @param key mapping key
   *
   * @throws java.util.NoSuchElementException if key does not exist
   * @throws java.lang.NullPointerException if node is null
   */
  def getBigDecimal(key: String): BigDecimal =
    read(key)

  /**
   * Gets node as `LocalDate`.
   *
   * @param key mapping key
   *
   * @throws java.util.NoSuchElementException if key does not exist
   * @throws java.lang.NullPointerException if node is null
   */
  def getLocalDate(key: String): LocalDate =
    read(key)

  /**
   * Gets node as `LocalDateTime`.
   *
   * @param key mapping key
   *
   * @throws java.util.NoSuchElementException if key does not exist
   * @throws java.lang.NullPointerException if node is null
   */
  def getLocalDateTime(key: String): LocalDateTime =
    read(key)

  /**
   * Gets node as `OffsetDateTime`.
   *
   * @param key mapping key
   *
   * @throws java.util.NoSuchElementException if key does not exist
   * @throws java.lang.NullPointerException if node is null
   */
  def getOffsetDateTime(key: String): OffsetDateTime =
    read(key)

  /**
   * Gets node as `Instant`.
   *
   * @param key mapping key
   *
   * @throws java.util.NoSuchElementException if key does not exist
   * @throws java.lang.NullPointerException if node is null
   */
  def getInstant(key: String): Instant =
    read(key)

  /**
   * Gets node as `YamlMapping`.
   *
   * @param key mapping key
   *
   * @throws java.util.NoSuchElementException if key does not exist
   * @throws java.lang.NullPointerException if node is null
   */
  def getMapping(key: String): YamlMapping =
    read(key)

  /**
   * Gets node as `YamlSequence`.
   *
   * @param key mapping key
   *
   * @throws java.util.NoSuchElementException if key does not exist
   * @throws java.lang.NullPointerException if node is null
   */
  def getSequence(key: String): YamlSequence =
    read(key)

  /**
   * Reads constructed data.
   *
   * @param key mapping key
   * @param constructor data constructor
   *
   * @throws java.util.NoSuchElementException if key does not exist
   * @throws java.lang.NullPointerException if node is null
   */
  def read[T](key: String)(using constructor: YamlConstructor[T]): T =
    apply(key) match
      case YamlNull => throw NullPointerException()
      case node     => node.as[T]

  /**
   * Optionally reads constructed data.
   *
   * @param key mapping key
   * @param constructor data constructor
   *
   * @return `Some` constructed data, or `None` if key does not exists or its
   * associated node is null
   */
  def readOption[T](key: String)(using constructor: YamlConstructor[T]): Option[T] =
    get(key).filter(YamlNull.!=).map(_.as[T])

  /**
   * Reads constructed data or returns default value.
   *
   * @param key mapping key
   * @param constructor data constructor
   *
   * @return constructed data, or default value if key does not exist or its
   * associated node is null
   */
  def readOrElse[T](key: String, default: => T)(using constructor: YamlConstructor[T]): T =
    readOption(key).getOrElse(default)

/**
 * Defines YAML sequence.
 *
 * @see [[YamlSequenceBuilder]]
 */
sealed trait YamlSequence extends YamlCollection:
  /** Gets node sequence. */
  def toSeq: Seq[YamlNode]

  /**
   * Gets node.
   *
   * @param index sequence index
   *
   * @throws java.lang.IndexOutOfBoundsException if index is out of bounds
   */
  def apply(index: Int): YamlNode

  /**
   * Tests for null.
   *
   * @param index sequence index
   *
   * @throws java.lang.IndexOutOfBoundsException if index is out of bounds
   */
  def isNull(index: Int): Boolean =
    apply(index) == YamlNull

  /**
   * Gets node as `String`.
   *
   * @param index sequence index
   *
   * @throws java.lang.IndexOutOfBoundsException if index is out of bounds
   * @throws java.lang.NullPointerException if node is null
   */
  def getString(index: Int): String =
    read(index)

  /**
   * Gets node as `Boolean`.
   *
   * @param index sequence index
   *
   * @throws java.lang.IndexOutOfBoundsException if index is out of bounds
   * @throws java.lang.NullPointerException if node is null
   */
  def getBoolean(index: Int): Boolean =
    read(index)

  /**
   * Gets node as `Int`.
   *
   * @param index sequence index
   *
   * @throws java.lang.IndexOutOfBoundsException if index is out of bounds
   * @throws java.lang.NullPointerException if node is null
   */
  def getInt(index: Int): Int =
    read(index)

  /**
   * Gets node as `Long`.
   *
   * @param index sequence index
   *
   * @throws java.lang.IndexOutOfBoundsException if index is out of bounds
   * @throws java.lang.NullPointerException if node is null
   */
  def getLong(index: Int): Long =
    read(index)

  /**
   * Gets node as `Float`.
   *
   * @param index sequence index
   *
   * @throws java.lang.IndexOutOfBoundsException if index is out of bounds
   * @throws java.lang.NullPointerException if node is null
   */
  def getFloat(index: Int): Float =
    read(index)

  /**
   * Gets node as `Double`.
   *
   * @param index sequence index
   *
   * @throws java.lang.IndexOutOfBoundsException if index is out of bounds
   * @throws java.lang.NullPointerException if node is null
   */
  def getDouble(index: Int): Double =
    read(index)

  /**
   * Gets node as `BigInt`.
   *
   * @param index sequence index
   *
   * @throws java.lang.IndexOutOfBoundsException if index is out of bounds
   * @throws java.lang.NullPointerException if node is null
   */
  def getBigInt(index: Int): BigInt =
    read(index)

  /**
   * Gets node as `BigDecimal`.
   *
   * @param index sequence index
   *
   * @throws java.lang.IndexOutOfBoundsException if index is out of bounds
   * @throws java.lang.NullPointerException if node is null
   */
  def getBigDecimal(index: Int): BigDecimal =
    read(index)

  /**
   * Gets node as `LocalDate`.
   *
   * @param index sequence index
   *
   * @throws java.lang.IndexOutOfBoundsException if index is out of bounds
   * @throws java.lang.NullPointerException if node is null
   */
  def getLocalDate(index: Int): LocalDate =
    read(index)

  /**
   * Gets node as `LocalDateTime`.
   *
   * @param index sequence index
   *
   * @throws java.lang.IndexOutOfBoundsException if index is out of bounds
   * @throws java.lang.NullPointerException if node is null
   */
  def getLocalDateTime(index: Int): LocalDateTime =
    read(index)

  /**
   * Gets node as `OffsetDateTime`.
   *
   * @param index sequence index
   *
   * @throws java.lang.IndexOutOfBoundsException if index is out of bounds
   * @throws java.lang.NullPointerException if node is null
   */
  def getOffsetDateTime(index: Int): OffsetDateTime =
    read(index)

  /**
   * Gets node as `Instant`.
   *
   * @param index sequence index
   *
   * @throws java.lang.IndexOutOfBoundsException if index is out of bounds
   * @throws java.lang.NullPointerException if node is null
   */
  def getInstant(index: Int): Instant =
    read(index)

  /**
   * Gets node as `YamlMapping`.
   *
   * @param index sequence index
   *
   * @throws java.lang.IndexOutOfBoundsException if index is out of bounds
   * @throws java.lang.NullPointerException if node is null
   */
  def getMapping(index: Int): YamlMapping =
    read(index)

  /**
   * Gets node as `YamlSequence`.
   *
   * @param index sequence index
   *
   * @throws java.lang.IndexOutOfBoundsException if index is out of bounds
   * @throws java.lang.NullPointerException if node is null
   */
  def getSequence(index: Int): YamlSequence =
    read(index)

  /**
   * Reads constructed data.
   *
   * @param index sequence index
   *
   * @throws java.lang.IndexOutOfBoundsException if index is out of bounds
   * @throws java.lang.NullPointerException if node is null
   */
  def read[T](index: Int)(using constructor: YamlConstructor[T]): T =
    apply(index) match
      case YamlNull => throw NullPointerException()
      case node     => node.as[T]

/**
 * Assumes either YAML mapping or YAML sequence.
 *
 * @note A collection facade is created by conversion only.
 *
 * @see [[yamlStructureFacadeConversion]]
 */
class YamlCollectionFacade private[yaml] (node: YamlCollection) extends YamlMapping, YamlSequence:
  def size = node.size

  def keys = node.asInstanceOf[YamlMapping].keys
  def toMap = node.asInstanceOf[YamlMapping].toMap
  def contains(key: String) = node.asInstanceOf[YamlMapping].contains(key)
  def apply(key: String) = node.asInstanceOf[YamlMapping].apply(key)

  def toSeq = node.asInstanceOf[YamlSequence].toSeq
  def apply(index: Int) = node.asInstanceOf[YamlSequence].apply(index)

  private[yaml] def value = node.value

private case class YamlStringImpl(value: String) extends YamlString

private case class YamlBooleanImpl(value: Boolean) extends YamlBoolean

private case class YamlNumberImpl(value: BigDecimal) extends YamlNumber:
  lazy val toInt        = value.toIntExact
  lazy val toLong       = value.toLongExact
  lazy val toFloat      = value.toFloat
  lazy val toDouble     = value.toDouble
  lazy val toBigInt     = value.toBigIntExact.getOrElse(throw ArithmeticException("Rounding necessary"))
  lazy val toBigDecimal = value

private case class YamlTimestampImpl(value: Temporal) extends YamlTimestamp:
  lazy val toLocalDate = value match
    case value: LocalDate => value
    case _                => throw DateTimeException(s"Truncation required to express ${value.getClass.getSimpleName} as LocalDate")

  lazy val toLocalDateTime = value match
    case value: LocalDateTime => value
    case _: LocalDate         => throw DateTimeException(s"Time required to express LocalDate as LocalDateTime")
    case _                    => throw DateTimeException(s"Truncation required to express ${value.getClass.getSimpleName} as LocalDateTime")

  lazy val toOffsetDateTime = value match
    case value: OffsetDateTime => value
    case value: Instant        => value.atOffset(ZoneOffset.UTC)
    case _                     => throw DateTimeException(s"Cannot express ${value.getClass.getSimpleName} as OffsetDateTime")

  lazy val toInstant = value match
    case value: OffsetDateTime => value.toInstant
    case value: Instant        => value
    case _                     => throw DateTimeException(s"Cannot express ${value.getClass.getSimpleName} as OffsetDateTime")

private abstract class AbstractYamlSequence extends YamlSequence

private abstract class AbstractYamlMapping extends YamlMapping
