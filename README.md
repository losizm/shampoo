# Shampoo

[![Maven Central](https://img.shields.io/maven-central/v/com.github.losizm/shampoo_3.svg?label=Maven%20Central)](https://central.sonatype.com/search?q=g:com.github.losizm%20a:shampoo_3)

The YAML library for Scala.

## Getting Started
To get started, add **Shampoo** to your project:

```scala
libraryDependencies += "com.github.losizm" %% "shampoo" % "0.1.0"
```

The underlying YAML processor is provided by [SnakeYaml](https://bitbucket.org/snakeyaml/snakeyaml/),
so it'll be added transitively.

## Lather Up !!!
Reading and writing YAML are powered by the `YamlConstructor` and `YamlRepresenter`
traits. They convert values to and from YAML, and library-provided implementations
are included for working with standard types like `String`, `Int`, etc. You must
define custom implementations for converting to and from your classes.

```scala
import scala.language.implicitConversions

import shampoo.yaml.{ *, given }

case class User(id: Int, name: String, groups: Seq[String])

// Define how to construct User from YAML
given YamlConstructor[User] with
  def construct(yaml: YamlNode) =
    User(
      yaml("id"),
      yaml("name"),
      yaml("groups")
    )

// Load YAML mapping
val yaml = Yaml.load("""
  id: 1000
  name: lupita
  groups:
    - lupita
    - admin
    - sudoers
""")

// Construct and verify
val user = yaml.as[User]
assert(user.id == 1000)
assert(user.name == "lupita")
assert(user.groups == Seq("lupita", "admin", "sudoers"))

// Define how to represent YAML from User
given YamlRepresenter[User] with
 def represent(user: User) =
    Yaml.map(
      "id"     -> user.id,
      "name"   -> user.name,
      "groups" -> user.groups
    )

// Represent and verify
val yamlUser = Yaml.toYaml(user)
assert(yamlUser.getInt("id") == 1000)
assert(yamlUser.getString("name") == "lupita")
assert(yamlUser("groups").as[Seq[String]] == Seq("lupita", "admin", "sudoers"))
```

Special implementations are available for working with collections. For example,
if you define `YamlConstructor[User]`, you automatically get
`YamlConstructor[Seq[User]]`. The same applies to `YamlRepresenter[User]`, which
infers `YamlRepresenter[Seq[User]]`.

```scala
// Load YAML sequence
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

// Read YamlSequence as Seq[User]
val users = yaml.as[Seq[User]]
assert { users(0) == User(0, "root", Seq("root")) }
assert { users(1) == User(1000, "lupita", Seq("lupita", "admin", "sudoers")) }

// Or as other Iterables
val userList = yaml.as[List[User]]
val userIter = yaml.as[Iterator[User]]
val userSet  = yaml.as[Set[User]]

// Even as an Array
val userArray = yaml.as[Array[User]]

// Write Seq[User] to YamlSequence
val yamlUsers = Yaml.toYaml(users)
assert { yamlUsers(0).getInt("id") == 0 }
assert { yamlUsers(0).getString("name") == "root" }
assert { yamlUsers(0)("groups").as[Seq[String]] == Seq("root") }
assert { yamlUsers(1).getInt("id") == 1000 }
assert { yamlUsers(1).getString("name") == "lupita" }
assert { yamlUsers(1)("groups").as[Seq[String]] == Seq("lupita", "admin", "sudoers") }
```

### Extracting Values
You can traverse `YamlMapping` and `YamlSequence` to extract nested values. The `\`
extension method makes this clean and easy.

```scala
import scala.language.implicitConversions

import shampoo.yaml.{ *, given }

case class User(id: Int, name: String)

// Define how to construct User
given YamlConstructor[User] =
  yaml => User(yaml("id"), yaml("name"))

val yaml = Yaml.load("""
  node:
    name: localhost
    users:
      - id: 0
        name: root
      - id: 1000
        name: lupita
""")

// Get users array from node object
val users = (yaml \ "node" \ "users").as[Seq[User]]

// Get first user (at index 0) in users array
val user = (yaml \ "node" \ "users" \ 0).as[User]

// Get name of second user (at index 1) in users array
val name = (yaml \ "node" \ "users" \ 1 \ "name").as[String]
```

And, just as easy, you can do a recursive lookup with `\\` to collect values by
key.

```scala
// Get all "name" values
val names = (yaml \\ "name").map(_.as[String])
assert { names == Seq("localhost", "root", "lupita") }
```


## API Documentation
See [scaladoc](https://losizm.github.io/shampoo/latest/api/index.html)
for additional details.

## License
**Shampoo** is licensed under the Apache License, Version 2. See [LICENSE](LICENSE)
for more information.
