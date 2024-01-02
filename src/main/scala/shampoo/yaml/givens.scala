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

import java.time.{ Instant, LocalDate, LocalDateTime, OffsetDateTime }

import scala.collection.Factory
import scala.util.{ Failure, Try }

/**
 * Returns `YamlNode` as is.
 *
 * This constructor is required to perform actions such as the following:
 *
 * {{{
 * import scala.language.implicitConversions
 *
 * import shampoo.yaml.{ Yaml, YamlNode, given }
 *
 * val yaml = Yaml.map("values" -> Yaml.seq("abc", 123, true))
 *
 * // Requires yamlNodeConstructor
 * val list = yaml("values").as[List[YamlNode]]
 * }}}
 */
given yamlNodeConstructor: YamlConstructor[YamlNode] = identity(_)

/** Casts `YamlNode` to `YamlNull`. */
given yamlNullConstructor: YamlConstructor[YamlNull.type] = _.asInstanceOf[YamlNull.type]

/** Casts `YamlNode` to `YamlString`. */
given yamlStringConstructor: YamlConstructor[YamlString] = _.asInstanceOf[YamlString]

/** Casts `YamlNode` to `YamlBoolean`. */
given yamlBooleanConstructor: YamlConstructor[YamlBoolean] = _.asInstanceOf[YamlBoolean]

/** Casts `YamlNode` to `YamlNumber`. */
given yamlNumberConstructor: YamlConstructor[YamlNumber] = _.asInstanceOf[YamlNumber]

/** Casts `YamlNode` to `YamlMapping`. */
given yamlObjectConstructor: YamlConstructor[YamlMapping] = _.asInstanceOf[YamlMapping]

/** Casts `YamlNode` to `YamlSequence`. */
given yamlSequenceConstructor: YamlConstructor[YamlSequence] = _.asInstanceOf[YamlSequence]

/** Constructs `String` from `YamlString`. */
given stringConstructor: YamlConstructor[String] =
  case node: YamlString => node.value
  case node             => throw YamlException(s"Not a string: ${node.getClass.getName}")

/** Constructs `Boolean` from `YamlBoolean`. */
given booleanConstructor: YamlConstructor[Boolean] =
  case node: YamlBoolean => node.value
  case node              => throw YamlException(s"Not a boolean: ${node.getClass.getName}")

/** Constructs `Int` from `YamlNumber`. */
given intConstructor: YamlConstructor[Int] =
  case node: YamlNumber => node.toInt
  case node             => throw YamlException(s"Not a number: ${node.getClass.getName}")

/** Constructs `Long` from `YamlNumber`. */
given longConstructor: YamlConstructor[Long] =
  case node: YamlNumber => node.toLong
  case node             => throw YamlException(s"Not a number: ${node.getClass.getName}")

/** Constructs `Float` from `YamlNumber`. */
given floatConstructor: YamlConstructor[Float] =
  case node: YamlNumber => node.toFloat
  case node             => throw YamlException(s"Not a number: ${node.getClass.getName}")

/** Constructs `Double` from `YamlNumber`. */
given doubleConstructor: YamlConstructor[Double] =
  case node: YamlNumber => node.toDouble
  case node             => throw YamlException(s"Not a number: ${node.getClass.getName}")

/** Constructs `BigInt` from `YamlNumber`. */
given bigIntConstructor: YamlConstructor[BigInt] =
  case node: YamlNumber => node.toBigInt
  case node             => throw YamlException(s"Not a number: ${node.getClass.getName}")

/** Constructs `BigDecimal` from `YamlNumber`. */
given bigDecimalConstructor: YamlConstructor[BigDecimal] =
  case node: YamlNumber => node.toBigDecimal
  case node             => throw YamlException(s"Not a number: ${node.getClass.getName}")

/** Constructs `LocalDate` from `YamlTimestamp`. */
given localDateConstructor: YamlConstructor[LocalDate] =
  case node: YamlTimestamp => node.toLocalDate
  case node                => throw YamlException(s"Not a timestamp: ${node.getClass.getName}")

/** Constructs `LocalDateTime` from `YamlTimestamp`. */
given localDateTimeConstructor: YamlConstructor[LocalDateTime] =
  case node: YamlTimestamp => node.toLocalDateTime
  case node                => throw YamlException(s"Not a timestamp: ${node.getClass.getName}")

/** Constructs `OffsetDateTime` from `YamlTimestamp`. */
given offsetDateTimeConstructor: YamlConstructor[OffsetDateTime] =
  case node: YamlTimestamp => node.toOffsetDateTime
  case node                => throw YamlException(s"Not a timestamp: ${node.getClass.getName}")

/** Constructs `Instant` from `YamlTimestamp`. */
given instantConstructor: YamlConstructor[Instant] =
  case node: YamlTimestamp => node.toInstant
  case node                => throw YamlException(s"Not a timestamp: ${node.getClass.getName}")

/** Converts `YamlNode` to `Map`. */
given mapConstructor[T, M[T] <: Map[String, T]](using constructor: YamlConstructor[T])(using factory: Factory[(String, T), M[T]]): YamlConstructor[M[T]] =
  case node: YamlMapping =>
    node.toMap.foldLeft(factory.newBuilder) {
      case (builder, (key, node)) => builder += key -> constructor.construct(node)
    }.result
  case node => throw YamlException(s"Not a mapping: ${node.getClass.getName}")

/** Constructs collection from `YamlSequence`. */
given collectionConstructor[T, M[T]](using constructor: YamlConstructor[T])(using factory: Factory[T, M[T]]): YamlConstructor[M[T]] =
  case node: YamlSequence =>
    node.toSeq.foldLeft(factory.newBuilder) {
      case (builder, node) => builder += constructor.construct(node)
    }.result
  case node => throw YamlException(s"Not a sequence: ${node.getClass.getName}")

/** Constructs `Option` from `YamlNode`. */
given optionConstructor[T](using constructor: YamlConstructor[T]): YamlConstructor[Option[T]] =
  case YamlNull => None
  case node     => Some(node.as[T])

/** Constructs `Try` from `YamlNode`. */
given tryConstructor[T](using constructor: YamlConstructor[T]): YamlConstructor[Try[T]] =
  node => Try(node.as[T])

/** Constructs `Either` from `YamlNode`. */
given eitherConstructor[A, B](using left: YamlConstructor[A])(using right: YamlConstructor[B]): YamlConstructor[Either[A, B]] =
  node => Try(Right(node.as[B])).getOrElse(Left(node.as[A]))

/** Represents `String` to `YamlString`. */
given stringRepresenter: YamlRepresenter[String] = YamlString(_)

/** Represents `Boolean` to `YamlBoolean`. */
given booleanRepresenter: YamlRepresenter[Boolean] = YamlBoolean(_)

/** Represents `Int` to `YamlNumber`. */
given intRepresenter: YamlRepresenter[Int] = YamlNumber(_)

/** Represents `Long` to `YamlNumber`. */
given longRepresenter: YamlRepresenter[Long] = YamlNumber(_)

/** Represents `Float` to `YamlNumber`. */
given floatRepresenter: YamlRepresenter[Float] = YamlNumber(_)

/** Represents `Double` to `YamlNumber`. */
given doubleRepresenter: YamlRepresenter[Double] = YamlNumber(_)

/** Represents `BigInt` to `YamlNumber`. */
given bigIntRepresenter: YamlRepresenter[BigInt] = YamlNumber(_)

/** Represents `BigDecimal` to `YamlNumber`. */
given bigDecimalRepresenter: YamlRepresenter[BigDecimal] = YamlNumber(_)

/** Represents `LocalDate` to `YamlTimestamp`. */
given localDateRepresenter: YamlRepresenter[LocalDate] = YamlTimestamp(_)

/** Represents `LocalDateTime` to `YamlTimestamp`. */
given localDateTimeRepresenter: YamlRepresenter[LocalDateTime] = YamlTimestamp(_)

/** Represents `OffsetDateTime` to `YamlTimestamp`. */
given offsetDateTimeRepresenter: YamlRepresenter[OffsetDateTime] = YamlTimestamp(_)

/** Represents `Instant` to `YamlTimestamp`. */
given instantRepresenter: YamlRepresenter[Instant] = YamlTimestamp(_)

/** Represents `Map` to `YamlMapping`. */
given mapRepresenter[T, M[T] <: Map[String, T]](using representer: YamlRepresenter[T]): YamlRepresenter[M[T]] =
  _.foldLeft(YamlMappingBuilder()) {
    case (builder, (name, value)) => builder.add(name, representer.represent(value))
  }.toYamlMapping()

/** Represents `Array` to `YamlSequence`. */
given arrayRepresenter[T](using representer: YamlRepresenter[T]): YamlRepresenter[Array[T]] =
  _.foldLeft(YamlSequenceBuilder()) {
    (builder, value) => builder.add(representer.represent(value))
  }.toYamlSequence()

/** Represents `Iterable` to `YamlSequence`. */
given interableRepresenter[T, M[T] <: Iterable[T]](using representer: YamlRepresenter[T]): YamlRepresenter[M[T]] =
  _.foldLeft(YamlSequenceBuilder()) {
    (builder, value) => builder.add(representer.represent(value))
  }.toYamlSequence()

/** Represents `Some` to `YamlNode` or returns `YamlNull` if `None`. */
given optionRepresenter[T, M[T] <: Option[T]](using representer: YamlRepresenter[T]): YamlRepresenter[M[T]] =
  _.fold(YamlNull)(representer.represent(_))

/** Represents `None` to `YamlNull`. */
given noneRepresenter: YamlRepresenter[None.type] = _ => YamlNull

/** Represents `Success` to `YamlNode` or returns `YamlNull` if `Failure`. */
given tryRepresenter[T, M[T] <: Try[T]](using representer: YamlRepresenter[T]): YamlRepresenter[M[T]] =
  _.fold(_ => YamlNull, representer.represent(_))

/** Represents `Failure` to `YamlNull`. */
given failureRepresenter: YamlRepresenter[Failure[?]] = _ => YamlNull

/** Represents `Either` to `YamlNode`. */
given eitherRepresenter[A, B, M[A, B] <: Either[A, B]](using left: YamlRepresenter[A])(using right: YamlRepresenter[B]): YamlRepresenter[M[A, B]] =
  _.fold(left.represent(_), right.represent(_))

/** Represents `Right` to `YamlNode`. */
given rightRepresenter[T](using representer: YamlRepresenter[T]): YamlRepresenter[Right[?, T]] =
  _.fold(_ => YamlNull, representer.represent(_))

/** Represents `Left` to `YamlNode`. */
given leftRepresenter[T](using representer: YamlRepresenter[T]): YamlRepresenter[Left[T, ?]] =
  _.fold(representer.represent(_), _ => YamlNull)

/** Applies conversion using `YamlConstructor`. */
given yamlConstructorConversion[T](using constructor: YamlConstructor[T]): Conversion[YamlNode, T] =
  constructor.construct(_)

/** Applies conversion using `YamlRepresenter`. */
given yamlRepresenterConversion[T](using representer: YamlRepresenter[T]): Conversion[T, YamlNode] =
  representer.represent(_)

/** Converts `YamlNode` to `YamlCollectionFacade`. */
given yamlStructureFacadeConversion: Conversion[YamlNode, YamlCollectionFacade] =
  case node: YamlCollection => YamlCollectionFacade(node)
  case node                 => throw YamlException(s"Not a collection: ${node.getClass.getName}")
