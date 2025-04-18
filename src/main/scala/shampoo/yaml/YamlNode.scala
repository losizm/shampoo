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

/** Defines YAML null. */
case object YamlNull extends YamlScalar

/** Defines YAML string. */
sealed trait YamlString extends YamlScalar:
  /** Gets value. */
  def value: String

/** Provides YAML string constructor. */
object YamlString:
  /**
   * Creates YAML string.
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
   * Creates YAML boolean.
   *
   * @param value boolean
   */
  def apply(value: Boolean): YamlBoolean =
    YamlBooleanImpl(value)

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

  /**
   * Gets number as `BigInt`.
   *
   * @throws java.lang.ArithmeticException if conversion is not exact.
   */
  def toBigInt: BigInt

  /** Gets number as `BigDecimal`. */
  def toBigDecimal: BigDecimal

  private[yaml] def value: BigDecimal

/** Provides YAML number constructor. */
object YamlNumber:
  /**
   * Creates YAML number.
   *
   * @param value int
   */
  def apply(value: Int): YamlNumber =
    YamlNumberImpl(BigDecimal(value))

  /**
   * Creates YAML number.
   *
   * @param value long
   */
  def apply(value: Long): YamlNumber =
    YamlNumberImpl(BigDecimal(value))

  /**
   * Creates YAML number.
   *
   * @param value float
   */
  def apply(value: Float): YamlNumber =
    YamlNumberImpl(BigDecimal(value))

  /**
   * Creates YAML number.
   *
   * @param value double
   */
  def apply(value: Double): YamlNumber =
    YamlNumberImpl(BigDecimal(value))

  /**
   * Creates YAML number.
   *
   * @param value big int
   */
  def apply(value: BigInt): YamlNumber =
    YamlNumberImpl(BigDecimal(value))

  /**
   * Creates YAML number.
   *
   * @param value big decimal
   */
  def apply(value: BigDecimal): YamlNumber =
    YamlNumberImpl(value)

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
   * @throws YamlMappingError if key does not exist
   */
  def apply(key: String): YamlNode

  /**
   * Optionally gets node.
   *
   * @param key mapping key
   */
  def get(key: String): Option[YamlNode]

  /**
   * Gets node or returns default node.
   *
   * @param key mapping key
   * @param default default node
   */
  def getOrElse(key: String, default: => YamlNode): YamlNode =
    get(key).getOrElse(default)

  /**
   * Tests for null node.
   *
   * @param key mapping key
   *
   * @throws YamlMappingError if key does not exist
   */
  def isNull(key: String): Boolean =
    apply(key) == YamlNull

  /**
   * Gets node as `String`.
   *
   * @param key mapping key
   *
   * @throws YamlMappingError if key does not exist or if data cannot be
   * constructed
   */
  def getString(key: String): String =
    read(key)

  /**
   * Gets node as `Boolean`.
   *
   * @param key mapping key
   *
   * @throws YamlMappingError if key does not exist or if data cannot be
   * constructed
   */
  def getBoolean(key: String): Boolean =
    read(key)

  /**
   * Gets node as `Int`.
   *
   * @param key mapping key
   *
   * @throws YamlMappingError if key does not exist or if data cannot be
   * constructed
   */
  def getInt(key: String): Int =
    read(key)

  /**
   * Gets node as `Long`.
   *
   * @param key mapping key
   *
   * @throws YamlMappingError if key does not exist or if data cannot be
   * constructed
   */
  def getLong(key: String): Long =
    read(key)

  /**
   * Gets node as `Float`.
   *
   * @param key mapping key
   *
   * @throws YamlMappingError if key does not exist or if data cannot be
   * constructed
   */
  def getFloat(key: String): Float =
    read(key)

  /**
   * Gets node as `Double`.
   *
   * @param key mapping key
   *
   * @throws YamlMappingError if key does not exist or if data cannot be
   * constructed
   */
  def getDouble(key: String): Double =
    read(key)

  /**
   * Gets node as `BigInt`.
   *
   * @param key mapping key
   *
   * @throws YamlMappingError if key does not exist or if data cannot be
   * constructed
   */
  def getBigInt(key: String): BigInt =
    read(key)

  /**
   * Gets node as `BigDecimal`.
   *
   * @param key mapping key
   *
   * @throws YamlMappingError if key does not exist or if data cannot be
   * constructed
   */
  def getBigDecimal(key: String): BigDecimal =
    read(key)

  /**
   * Gets node as `YamlMapping`.
   *
   * @param key mapping key
   *
   * @throws YamlMappingError if key does not exist or if node is not mapping
   */
  def getMapping(key: String): YamlMapping =
    read(key)

  /**
   * Gets node as `YamlSequence`.
   *
   * @param key mapping key
   *
   * @throws YamlMappingError if key does not exist or if node is not sequence
   */
  def getSequence(key: String): YamlSequence =
    read(key)

  /**
   * Reads constructed data.
   *
   * @param key mapping key
   * @param constructor data constructor
   *
   * @throws YamlMappingError if key does not exist or if data cannot be
   * constructed
   */
  def read[T](key: String)(using constructor: YamlConstructor[T]): T =
    try
      apply(key) match
        case YamlNull => throw NullPointerException()
        case node     => node.as[T]
    catch
      case e: YamlMappingError => throw e
      case e: Exception        => throw YamlMappingError(key, e)

  /**
   * Optionally reads constructed data.
   *
   * @param key mapping key
   * @param constructor data constructor
   *
   * @return constructed data, or none if key does not exist or its node is null
   *
   * @throws YamlMappingError if data cannot be constructed
   */
  def readOption[T](key: String)(using constructor: YamlConstructor[T]): Option[T] =
    try
      get(key).filter(YamlNull.!=).map(_.as[T])
    catch
      case e: YamlMappingError => throw e
      case e: Exception        => throw YamlMappingError(key, e)

  /**
   * Reads constructed data or returns default.
   *
   * @param key mapping key
   * @param default default data
   * @param constructor data constructor
   *
   * @return constructed data, or default if key does not exist or its node
   * is null
   *
   * @throws YamlMappingError if data cannot be constructed
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
   * @throws YamlSequenceError if index is out of range
   */
  def apply(index: Int): YamlNode

  /**
   * Tests for null node.
   *
   * @param index sequence index
   *
   * @throws YamlSequenceError if index is out of range
   */
  def isNull(index: Int): Boolean =
    apply(index) == YamlNull

  /**
   * Gets node as `String`.
   *
   * @param index sequence index
   *
   * @throws YamlSequenceError if index is out of range or if data cannot be
   * constructed
   */
  def getString(index: Int): String =
    read(index)

  /**
   * Gets node as `Boolean`.
   *
   * @param index sequence index
   *
   * @throws YamlSequenceError if index is out of range or if data cannot be
   * constructed
   */
  def getBoolean(index: Int): Boolean =
    read(index)

  /**
   * Gets node as `Int`.
   *
   * @param index sequence index
   *
   * @throws YamlSequenceError if index is out of range or if data cannot be
   * constructed
   */
  def getInt(index: Int): Int =
    read(index)

  /**
   * Gets node as `Long`.
   *
   * @param index sequence index
   *
   * @throws YamlSequenceError if index is out of range or if data cannot be
   * constructed
   */
  def getLong(index: Int): Long =
    read(index)

  /**
   * Gets node as `Float`.
   *
   * @param index sequence index
   *
   * @throws YamlSequenceError if index is out of range or if data cannot be
   * constructed
   */
  def getFloat(index: Int): Float =
    read(index)

  /**
   * Gets node as `Double`.
   *
   * @param index sequence index
   *
   * @throws YamlSequenceError if index is out of range or if data cannot be
   * constructed
   */
  def getDouble(index: Int): Double =
    read(index)

  /**
   * Gets node as `BigInt`.
   *
   * @param index sequence index
   *
   * @throws YamlSequenceError if index is out of range or if data cannot be
   * constructed
   */
  def getBigInt(index: Int): BigInt =
    read(index)

  /**
   * Gets node as `BigDecimal`.
   *
   * @param index sequence index
   *
   * @throws YamlSequenceError if index is out of range or if data cannot be
   * constructed
   */
  def getBigDecimal(index: Int): BigDecimal =
    read(index)

  /**
   * Gets node as `YamlMapping`.
   *
   * @param index sequence index
   *
   * @throws YamlSequenceError if index is out of range or if node is not
   * mapping
   */
  def getMapping(index: Int): YamlMapping =
    read(index)

  /**
   * Gets node as `YamlSequence`.
   *
   * @param index sequence index
   *
   * @throws YamlSequenceError if index is out of range or if node is not
   * sequence
   */
  def getSequence(index: Int): YamlSequence =
    read(index)

  /**
   * Reads constructed data.
   *
   * @param index sequence index
   *
   * @throws YamlSequenceError if index is out of range or if data cannot be
   * constructed
   */
  def read[T](index: Int)(using constructor: YamlConstructor[T]): T =
    try
      apply(index) match
        case YamlNull => throw NullPointerException()
        case node     => node.as[T]
    catch
      case e: YamlSequenceError => throw e
      case e: Exception         => throw YamlSequenceError(index, e)

  /**
   * Optionally reads constructed data.
   *
   * @param index sequence index
   * @param constructor data constructor
   *
   * @return constructed data, or none if node is null
   *
   * @throws YamlSequenceError if index is out of range or if data cannot be
   * constructed
   */
  def readOption[T](index: Int)(using constructor: YamlConstructor[T]): Option[T] =
    try
      apply(index) match
        case YamlNull => None
        case node     => Some(node.as[T])
    catch
      case e: YamlSequenceError => throw e
      case e: Exception         => throw YamlSequenceError(index, e)

  /**
   * Reads constructed data or returns default.
   *
   * @param index sequence index
   * @param default default data
   * @param constructor data constructor
   *
   * @return constructed data, or default if node is null
   *
   * @throws YamlSequenceError if index is out of range or if data cannot be
   * constructed
   */
  def readOrElse[T](index: Int, default: => T)(using constructor: YamlConstructor[T]): T =
    readOption(index).getOrElse(default)

/**
 * Assumes either YAML mapping or YAML sequence.
 *
 * @note A collection facade is created by conversion only.
 *
 * @see [[yamlCollectionFacadeConversion]]
 */
class YamlCollectionFacade private[yaml] (node: YamlCollection) extends YamlMapping, YamlSequence:
  def size = node.size

  def keys = expect[YamlMapping](node).keys
  def toMap = expect[YamlMapping](node).toMap
  def contains(key: String) = expect[YamlMapping](node).contains(key)
  def get(key: String) = expect[YamlMapping](node).get(key)
  def apply(key: String) = expect[YamlMapping](node).apply(key)

  def toSeq = expect[YamlSequence](node).toSeq
  def apply(index: Int) = expect[YamlSequence](node).apply(index)

  /** Unwraps underlying YAML collection. */
  def unwrap: YamlCollection = node

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

private abstract class AbstractYamlSequence extends YamlSequence

private abstract class AbstractYamlMapping extends YamlMapping
