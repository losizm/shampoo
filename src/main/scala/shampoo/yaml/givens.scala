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

import scala.collection.Factory
import scala.reflect.ClassTag
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
given yamlNullConstructor: YamlConstructor[YamlNull.type] = expect(_)

/** Casts `YamlNode` to `YamlString`. */
given yamlStringConstructor: YamlConstructor[YamlString] = expect(_)

/** Casts `YamlNode` to `YamlBoolean`. */
given yamlBooleanConstructor: YamlConstructor[YamlBoolean] = expect(_)

/** Casts `YamlNode` to `YamlNumber`. */
given yamlNumberConstructor: YamlConstructor[YamlNumber] = expect(_)

/** Casts `YamlNode` to `YamlMapping`. */
given yamlObjectConstructor: YamlConstructor[YamlMapping] = expect(_)

/** Casts `YamlNode` to `YamlSequence`. */
given yamlSequenceConstructor: YamlConstructor[YamlSequence] = expect(_)

/** Constructs `String` from `YamlString`. */
given stringConstructor: YamlConstructor[String] = expect[YamlString](_).value

/** Represents `String` to `YamlString`. */
given stringRepresenter: YamlRepresenter[String] = YamlString(_)

/** Constructs `Boolean` from `YamlBoolean`. */
given booleanConstructor: YamlConstructor[Boolean] = expect[YamlBoolean](_).value

/** Represents `Boolean` to `YamlBoolean`. */
given booleanRepresenter: YamlRepresenter[Boolean] = YamlBoolean(_)

/** Constructs `Int` from `YamlNumber`. */
given intConstructor: YamlConstructor[Int] = expect[YamlNumber](_).toInt

/** Represents `Int` to `YamlNumber`. */
given intRepresenter: YamlRepresenter[Int] = YamlNumber(_)

/** Constructs `Long` from `YamlNumber`. */
given longConstructor: YamlConstructor[Long] = expect[YamlNumber](_).toLong

/** Represents `Long` to `YamlNumber`. */
given longRepresenter: YamlRepresenter[Long] = YamlNumber(_)

/** Constructs `Float` from `YamlNumber`. */
given floatConstructor: YamlConstructor[Float] = expect[YamlNumber](_).toFloat

/** Represents `Float` to `YamlNumber`. */
given floatRepresenter: YamlRepresenter[Float] = YamlNumber(_)

/** Constructs `Double` from `YamlNumber`. */
given doubleConstructor: YamlConstructor[Double] = expect[YamlNumber](_).toDouble

/** Represents `Double` to `YamlNumber`. */
given doubleRepresenter: YamlRepresenter[Double] = YamlNumber(_)

/** Constructs `BigInt` from `YamlNumber`. */
given bigIntConstructor: YamlConstructor[BigInt] = expect[YamlNumber](_).toBigInt

/** Represents `BigInt` to `YamlNumber`. */
given bigIntRepresenter: YamlRepresenter[BigInt] = YamlNumber(_)

/** Constructs `BigDecimal` from `YamlNumber`. */
given bigDecimalConstructor: YamlConstructor[BigDecimal] = expect[YamlNumber](_).toBigDecimal

/** Represents `BigDecimal` to `YamlNumber`. */
given bigDecimalRepresenter: YamlRepresenter[BigDecimal] = YamlNumber(_)

/** Constructs `Option` from `YamlNode`. */
given optionConstructor[T](using constructor: YamlConstructor[T]): YamlConstructor[Option[T]] =
  case YamlNull => None
  case node     => Some(node.as[T])

/** Represents `Some` to `YamlNode` or returns `YamlNull` if `None`. */
given optionRepresenter[T, C[T] <: Option[T]](using representer: YamlRepresenter[T]): YamlRepresenter[C[T]] =
  _.fold(YamlNull)(representer.represent(_))

/** Represents `None` to `YamlNull`. */
given noneRepresenter: YamlRepresenter[None.type] = _ => YamlNull

/** Constructs `Try` from `YamlNode`. */
given tryConstructor[T](using constructor: YamlConstructor[T]): YamlConstructor[Try[T]] =
  node => Try(node.as[T])

/** Represents `Success` to `YamlNode` or returns `YamlNull` if `Failure`. */
given tryRepresenter[T, C[T] <: Try[T]](using representer: YamlRepresenter[T]): YamlRepresenter[C[T]] =
  _.fold(_ => YamlNull, representer.represent(_))

/** Represents `Failure` to `YamlNull`. */
given failureRepresenter: YamlRepresenter[Failure[?]] = _ => YamlNull

/** Constructs `Either` from `YamlNode`. */
given eitherConstructor[A, B](using left: YamlConstructor[A])(using right: YamlConstructor[B]): YamlConstructor[Either[A, B]] =
  node => Try(Right(node.as[B])).getOrElse(Left(node.as[A]))

/** Represents `Either` to `YamlNode`. */
given eitherRepresenter[A, B, C[A, B] <: Either[A, B]](using left: YamlRepresenter[A])(using right: YamlRepresenter[B]): YamlRepresenter[C[A, B]] =
  _.fold(left.represent(_), right.represent(_))

/** Represents `Right` to `YamlNode`. */
given rightRepresenter[T](using representer: YamlRepresenter[T]): YamlRepresenter[Right[?, T]] =
  _.fold(_ => YamlNull, representer.represent(_))

/** Represents `Left` to `YamlNode`. */
given leftRepresenter[T](using representer: YamlRepresenter[T]): YamlRepresenter[Left[T, ?]] =
  _.fold(representer.represent(_), _ => YamlNull)

/** Constructs `Array` from `YamlSequence`. */
given arrayConstructor[T](using constructor: YamlConstructor[T])(using ctag: ClassTag[T]): YamlConstructor[Array[T]] =
  expect[YamlSequence](_).toSeq.map(constructor.construct).toArray

/** Represents `Array` to `YamlSequence`. */
given arrayRepresenter[T](using representer: YamlRepresenter[T]): YamlRepresenter[Array[T]] =
  _.foldLeft(YamlSequenceBuilder()) {
    (builder, value) => builder.add(representer.represent(value))
  }.toYamlSequence()

/** Constructs `Iterable` from `YamlSequence`. */
given iterableConstructor[T, C[T] <: Iterable[T]](using constructor: YamlConstructor[T])(using factory: Factory[T, C[T]]): YamlConstructor[C[T]] =
  expect[YamlSequence](_).toSeq.foldLeft(factory.newBuilder) {
    case (builder, node) => builder += constructor.construct(node)
  }.result

/** Represents `Iterable` to `YamlSequence`. */
given iterableRepresenter[T, C[T] <: Iterable[T]](using representer: YamlRepresenter[T]): YamlRepresenter[C[T]] =
  _.foldLeft(YamlSequenceBuilder()) {
    (builder, value) => builder.add(representer.represent(value))
  }.toYamlSequence()

/** Constructs `Map` from `YamlNode`. */
given mapConstructor[T, C[T] <: Map[String, T]](using constructor: YamlConstructor[T])(using factory: Factory[(String, T), C[T]]): YamlConstructor[C[T]] =
  expect[YamlMapping](_).toMap.foldLeft(factory.newBuilder) {
    case (builder, (key, node)) => builder += key -> constructor.construct(node)
  }.result

/** Represents `Map` to `YamlMapping`. */
given mapRepresenter[T, C[T] <: Map[String, T]](using representer: YamlRepresenter[T]): YamlRepresenter[C[T]] =
  _.foldLeft(YamlMappingBuilder()) {
    case (builder, (name, value)) => builder.add(name, representer.represent(value))
  }.toYamlMapping()

/** Applies conversion using `YamlConstructor`. */
given yamlConstructorConversion[T](using constructor: YamlConstructor[T]): Conversion[YamlNode, T] =
  constructor.construct(_)

/** Applies conversion using `YamlRepresenter`. */
given yamlRepresenterConversion[T](using representer: YamlRepresenter[T]): Conversion[T, YamlNode] =
  representer.represent(_)

/** Converts `YamlNode` to `YamlCollectionFacade`. */
given yamlCollectionFacadeConversion: Conversion[YamlNode, YamlCollectionFacade] =
  node => YamlCollectionFacade(expect(node))
