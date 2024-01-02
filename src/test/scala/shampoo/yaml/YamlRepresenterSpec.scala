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

class YamlRepresenterSpec extends org.scalatest.flatspec.AnyFlatSpec:
  case class User(id: Int, name: String, groups: Seq[String])

  it should "represent YAML" in {
    given UserRepresenter: YamlRepresenter[User] =
      user => YamlMappingBuilder()
        .add("id", user.id)
        .add("name", user.name)
        .add("groups", Yaml.toYaml(user.groups))
        .toYamlMapping()

    val user = User(1000, "lupita", Seq("staff", "admin", "sudoers"))

    val yaml = Yaml.toYaml(user).as[YamlMapping]
    assert(yaml.getInt("id") == 1000)
    assert(yaml.getString("name") == "lupita")
    assert(yaml.read[Seq[String]]("groups") == Seq("staff", "admin", "sudoers"))
  }
