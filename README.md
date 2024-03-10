![banner](.github/assets/logo_dark.png#gh-dark-mode-only)
![banner](.github/assets/logo_light.png#gh-light-mode-only)

# Paklet

[![license](https://img.shields.io/github/license/machinemc/paklet?style=for-the-badge&color=657185)](LICENSE)
![release](https://img.shields.io/github/v/release/machinemc/paklet?style=for-the-badge&color=edb228)

Paklet is annotation based Java library for simple and fast packet crafting.

# Table of contents
* [Features](#features)
* [Usage](#usage)
  * [Creating a packet class](#creating-a-packet-class)
  * [Custom Serializer](#custom-serializer)
  * [Packet Crafting](#packet-crafting)
* [Importing](#importing)
* [License](#license)

### Features
* Simplicity – The annotation system is straightforward and easy to use
* Flexibility – You can individually customize the serialization of each packet field or even the whole packet
* Expandable – Paklet can be easily extended to automate the serialization of custom types.
* Speed – Bytecode manipulation in the background ensures the packet serialization and construction is fast

### Usage

#### Creating a packet class

All classes annotated with `Packet` are considered packets. All their fields, if they are not static, transient, or annotated with `@Ignore`
are automatically serialized by default serializers if not specified otherwise with `@SerializeWith` annotation.
```java
@Packet(0x00)
public class TestPacket {

    private String[] data;

}
```
Together with `@Ignore` and `@SerializeWith`, Paklet offers one more modifier `@Optional` for nullable fields.
Default serializers can be found in `Serializers` class.

Some types can be annotated with additional metadata, Paklet offers `@Length` and `@FixedLength` supported for
String Bitset, Collection, and array types.

Both modifiers and metadata can be also used for parameters e.g. `List<@SerializeWith(VarLongSerializer.class) Long>`.

> [!NOTE]
> Paklet also offers `@VarIntSerializer` and `@VarLongSerializer` compatible with the Minecraft Java Protocol.

For more examples see tests of the `paklet-core` module.

#### Custom Serializer

Paklet allows simple way of creating custom serializers. To create a custom serializer, implement `Serializer<T>`.

```java
@Supports({Integer.class, int.class})
public class MyCustomSerializer implements Serializer<Integer> {
    
    @Override
    public void serialize(DataVisitor visitor, Integer value) {
        // serialization
    }

    @Override
    public Integer deserialize(DataVisitor visitor) {
        // deserialization
    }

}
```

`@Supports` annotation can specify which types the serializer supports, if more complex rule for choosing the types is needed
(e.g. array types), the array can stay empty and custom `SerializationRule` needs to be implemented.

All serializers annotated with `@DefaultSerializer` are automatically registered.

Serialization context of current field can be accessed using `Serializer.context()`.

For more examples see `Serializers` class with default serializers provided by Paklet.

#### Packet Crafting

To read and write packets, instance of `PacketFactory` is required. The default one is provided in the core Paklet module in
a form of `PacketFactoryBuilder`. To create an instance, you need to provide serializer provider and serializer that will be used
to prefix packet id.

```java
SerializerProvider serializerProvider = SerializerProviderBuilder.create().loadProvided().loadDefaults().build();
PacketFactory packetFactory = PacketFactoryBuilder.create(new Serializers.Integer(), serializerProvider).loadDefaults().build();
```

`SerializerProviderBuilder` allows you to simply load all serializers annotated with `@DefaultSerializer` and default serializers provided
by Paklet. `PacketFactoryBuilder` then takes the created serializer provider, integer serializer for prefixing packet id and can
automatically load all the classes marked with `@Packet`. Both serializer provider and packet factory builder then allows individual
registration of packets, serializers, and serialization rules. Serializers that have no argument constructor do not have to be registered
and will be automatically resolved during runtime.

For more examples see tests of the `paklet-core` module.

### Importing

#### API and Annotation Processor
```kotlin
repositories {
    maven {
        name = "machinemcRepositoryReleases"
        url = uri("https://repo.machinemc.org/releases")
    }
}

dependencies {
    implementation("org.machinemc:paklet-api:VERSION")
    annotationProcessor("org.machinemc:paklet-processor:VERSION")
}
```
#### Implementation
```kotlin
repositories {
    maven {
        name = "machinemcRepositoryReleases"
        url = uri("https://repo.machinemc.org/releases")
    }
}

dependencies {
    implementation("org.machinemc:paklet-core:VERSION")
}
```

#### Gradle Plugin
> [!NOTE]
> Gradle plugin is a key feature of Paklet. It modifies bytecode of compiled packet classes by adding hidden getters and setters which are later
> used by generated packet readers and writers to ensure no reflection is used during the serialization to achieve higher speeds.
> If unused, the packet serialization will be noticeably slower.
```kotlin
buildscript {
    repositories {
        maven {
            url = uri("https://repo.machinemc.org/releases")
        }
    }
    dependencies {
        classpath("org.machinemc:paklet-plugin:VERSION")
    }
}

apply<PakletPlugin>()
```

### License
Paklet is free software licensed under the [MIT license](LICENSE).