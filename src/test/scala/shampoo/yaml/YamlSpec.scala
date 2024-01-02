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

import scala.language.implicitConversions

class YamlSpec extends org.scalatest.flatspec.AnyFlatSpec:
  it should "load YAML" in {
    val yaml = Yaml.load("""
      host: localhost
      port: 8443
      ssl:
        enabled: true
        key: etc/example.key
        certificate: etc/example.crt
      keepAlive: null
      incoming:
        - type: RequestHandler
          method: GET
          path: /healthcheck
          className: com.example.server.HealthCheck
        - type: RouterApplication
          path: /api
          className: com.example.server.ApiRouter
        - type: FileServer
          path: /ui
          sourceDirectory: /ui
          default: index.html
      outgoing: []
      redirect: {}
    """)

    verify(yaml)
  }

  it should "build and dump YAML (1)" in {
    val yaml = YamlMappingBuilder()
      .add("host", "localhost")
      .add("port", 8443)
      .add("ssl", YamlMappingBuilder()
        .add("enabled", true)
        .add("key", "etc/example.key")
        .add("certificate", "etc/example.crt")
        .toYamlMapping())
      .addNull("keepAlive")
      .add("incoming", YamlSequenceBuilder()
        .add(YamlMappingBuilder()
          .add("type", "RequestHandler")
          .add("method", "GET")
          .add("path", "/healthcheck")
          .add("className", "com.example.server.HealthCheck")
          .toYamlMapping())
        .add(YamlMappingBuilder()
          .add("type", "RouterApplication")
          .add("path", "/api")
          .add("className", "com.example.server.ApiRouter")
          .toYamlMapping())
        .add(YamlMappingBuilder()
          .add("type", "FileServer")
          .add("path", "/ui")
          .add("sourceDirectory", "/ui")
          .add("default", "index.html")
          .toYamlMapping())
        .toYamlSequence())
      .add("outgoing", YamlSequenceBuilder().toYamlSequence())
      .add("redirect", YamlMappingBuilder().toYamlMapping())
      .toYamlMapping()

    verify(yaml)

    val copy = Yaml.load(Yaml.dump(yaml))
    verify(copy)
  }

  it should "build and dump YAML (2)" in {
    val yaml = Yaml.map(
      "host" -> "localhost",
      "port" -> 8443,
      "ssl"  -> Yaml.map(
        "enabled"     -> true,
        "key"         -> "etc/example.key",
        "certificate" -> "etc/example.crt",
      ),
      "keepAlive" -> YamlNull,
      "incoming"  -> Yaml.seq(
        Yaml.map(
          "type"      -> "RequestHandler",
          "method"    -> "GET",
          "path"      -> "/healthcheck",
          "className" -> "com.example.server.HealthCheck"
        ),
        Yaml.map(
          "type"      -> "RouterApplication",
          "path"      -> "/api",
          "className" -> "com.example.server.ApiRouter"
        ),
        Yaml.map(
          "type"            -> "FileServer",
          "path"            -> "/ui",
          "sourceDirectory" -> "/ui",
          "default"         -> "index.html"
        )
      ),
      "outgoing" -> Yaml.seq(),
      "redirect" -> Yaml.map(),
    )

    verify(yaml)

    val copy = Yaml.load(Yaml.dump(yaml))
    verify(copy)
  }

  private def verify(yaml: YamlMapping): Unit =
    assert(yaml.nonEmpty)
    assert(yaml.size == 7)
    assert(yaml.keys == Set("host", "port", "ssl", "keepAlive", "incoming", "outgoing", "redirect"))

    assert(yaml("host") == YamlString("localhost"))
    assert(yaml.getString("host") == "localhost")

    assert(yaml("port") == YamlNumber(8443))
    assert(yaml("port") == YamlNumber(8443L))
    assert(yaml("port") == YamlNumber(8443.0))
    assert(yaml.getInt("port") == 8443)
    assert(yaml.getLong("port") == 8443L)
    assert(yaml.getDouble("port") == 8443.0)

    assert(yaml("keepAlive") == YamlNull)
    assert(yaml.isNull("keepAlive"))
    assertThrows[NullPointerException](yaml.getMapping("keepAlive"))

    assert(yaml("ssl").isInstanceOf[YamlMapping])
    assert(yaml.getMapping("ssl").nonEmpty)
    assert(yaml.getMapping("ssl").size == 3)
    assert(yaml.getMapping("ssl").keys == Set("enabled", "key", "certificate"))

    assert(yaml.getMapping("ssl")("enabled") == YamlBoolean(true))
    assert(yaml.getMapping("ssl").getBoolean("enabled"))
    assert(yaml.getMapping("ssl").getString("key") == "etc/example.key")
    assert(yaml.getMapping("ssl").getString("certificate") == "etc/example.crt")

    assert(yaml("incoming").isInstanceOf[YamlSequence])
    assert(yaml.getSequence("incoming").nonEmpty)
    assert(yaml.getSequence("incoming").size == 3)

    assert(yaml.getSequence("incoming")(0).isInstanceOf[YamlMapping])
    assert(yaml.getSequence("incoming").getMapping(0).nonEmpty)
    assert(yaml.getSequence("incoming").getMapping(0).size == 4)
    assert(yaml.getSequence("incoming").getMapping(0).keys == Set("type", "method", "path", "className"))
    assert(yaml.getSequence("incoming").getMapping(0).getString("type") == "RequestHandler")
    assert(yaml.getSequence("incoming").getMapping(0).getString("method") == "GET")
    assert(yaml.getSequence("incoming").getMapping(0).getString("path") == "/healthcheck")
    assert(yaml.getSequence("incoming").getMapping(0).getString("className") == "com.example.server.HealthCheck")

    assert(yaml.getSequence("incoming")(1).isInstanceOf[YamlMapping])
    assert(yaml.getSequence("incoming").getMapping(1).nonEmpty)
    assert(yaml.getSequence("incoming").getMapping(1).size == 3)
    assert(yaml.getSequence("incoming").getMapping(1).keys == Set("type", "path", "className"))
    assert(yaml.getSequence("incoming").getMapping(1).getString("type") == "RouterApplication")
    assert(yaml.getSequence("incoming").getMapping(1).getString("path") == "/api")
    assert(yaml.getSequence("incoming").getMapping(1).getString("className") == "com.example.server.ApiRouter")

    assert(yaml.getSequence("incoming")(2).isInstanceOf[YamlMapping])
    assert(yaml.getSequence("incoming").getMapping(2).nonEmpty)
    assert(yaml.getSequence("incoming").getMapping(2).size == 4)
    assert(yaml.getSequence("incoming").getMapping(2).keys == Set("type", "path", "sourceDirectory", "default"))
    assert(yaml.getSequence("incoming").getMapping(2).getString("type") == "FileServer")
    assert(yaml.getSequence("incoming").getMapping(2).getString("path") == "/ui")
    assert(yaml.getSequence("incoming").getMapping(2).getString("sourceDirectory") == "/ui")
    assert(yaml.getSequence("incoming").getMapping(2).getString("default") == "index.html")

    assertThrows[IndexOutOfBoundsException](yaml.getSequence("incoming").getString(3))
    assertThrows[IndexOutOfBoundsException](yaml.getSequence("incoming").getBoolean(3))
    assertThrows[IndexOutOfBoundsException](yaml.getSequence("incoming").getInt(3))
    assertThrows[IndexOutOfBoundsException](yaml.getSequence("incoming").getLong(3))
    assertThrows[IndexOutOfBoundsException](yaml.getSequence("incoming").getDouble(3))
    assertThrows[IndexOutOfBoundsException](yaml.getSequence("incoming").getMapping(3))
    assertThrows[IndexOutOfBoundsException](yaml.getSequence("incoming").getSequence(3))

    assert(yaml.getSequence("outgoing").isEmpty)
    assert(yaml.getSequence("outgoing").size == 0)

    assert(yaml.getMapping("redirect").isEmpty)
    assert(yaml.getMapping("redirect").size == 0)
    assert(yaml.getMapping("redirect").keys.isEmpty)

    assertThrows[NoSuchElementException](yaml.getInt("none"))
    info(s"\n${Yaml.dump(yaml)}")
