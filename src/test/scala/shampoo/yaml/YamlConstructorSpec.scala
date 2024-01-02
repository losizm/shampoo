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

import java.time.LocalDate

import scala.collection.immutable.{ ListMap, TreeMap }
import scala.language.implicitConversions

class YamlConstructorSpec extends org.scalatest.flatspec.AnyFlatSpec:
  case class User(id: Int, name: String, groups: Seq[String])

  it should "construct YAML mapping" in {
    given UserConstructor: YamlConstructor[User] =
      case node: YamlMapping => User(node.read("id"), node.read("name"), node.read("groups"))
      case node              => throw YamlException(s"Expected mapping: ${node.getClass.getName}")

    val yaml = Yaml.load("""
      id: 1000
      name: lupita
      groups:
        - lupita
        - admin
        - sudoers
    """)

    val user = yaml.as[User]
    assert(user.id == 1000)
    assert(user.name == "lupita")
    assert(user.groups == Seq("lupita", "admin", "sudoers"))
  }

  it should "construct YAML mapping and convert to Map" in {
    val yaml = Yaml.load("""
      lupita: 1983-03-01
      denzel: 1954-12-28
      wesley: 1962-07-31
      kerry:  1977-01-31
    """)

    val dob1 = yaml.as[ListMap[String, LocalDate]]
    assert(dob1.size == 4)
    assert(dob1.keySet == Set("lupita", "denzel", "wesley", "kerry"))
    assert(dob1("lupita") == LocalDate.parse("1983-03-01"))
    assert(dob1("denzel") == LocalDate.parse("1954-12-28"))
    assert(dob1("wesley") == LocalDate.parse("1962-07-31"))
    assert(dob1("kerry")  == LocalDate.parse("1977-01-31"))

    val dob2 = yaml.as[TreeMap[String, LocalDate]]
    assert(dob2.size == 4)
    assert(dob2.keySet.toSeq == Seq("denzel", "kerry", "lupita", "wesley"))
    assert(dob2("lupita") == LocalDate.parse("1983-03-01"))
    assert(dob2("denzel") == LocalDate.parse("1954-12-28"))
    assert(dob2("wesley") == LocalDate.parse("1962-07-31"))
    assert(dob2("kerry")  == LocalDate.parse("1977-01-31"))
  }

  it should "construct YAML sequence" in {
    given UserConstructor: YamlConstructor[User] =
      case node: YamlMapping => User(node.read("id"), node.read("name"), node.read("groups"))
      case node              => throw YamlException(s"Expected mapping: ${node.getClass.getName}")

    val yaml = Yaml.load("""
    - id: 0
      name: root
      groups:
        - root
    - id: 1000
      name: lupita
      groups:
        - lupita
        - admin
        - sudoers
    """)

    val users = yaml.as[List[User]]
    assert(users(0).id == 0)
    assert(users(0).name == "root")
    assert(users(0).groups == Seq("root"))
    assert(users(1).id == 1000)
    assert(users(1).name == "lupita")
    assert(users(1).groups == Seq("lupita", "admin", "sudoers"))
  }
