![banner](.github/assets/logo_dark.png#gh-dark-mode-only)
![banner](.github/assets/logo_light.png#gh-light-mode-only)

<p align="center">Paklet is annotation based Java library for simple and fast packet crafting.</p>

<p align="center">
    <img src="https://img.shields.io/github/license/machinemc/paklet?style=for-the-badge&color=107185" alt="LICENSE">
    <img src="https://img.shields.io/github/v/release/machinemc/paklet?style=for-the-badge&color=edb228" alt="RELEASE">
</p>

---

# Table of contents
* [Features](#features)
* [Usage](#usage)
  * [Creating a packet class](#creating-a-packet-class)
  * [Custom Serializer](#custom-serializer)
  * [Packet Crafting](#packet-crafting)
* [Importing](#importing)
* [License](#license)

### Features
* *Simplicity* – The annotation system is straightforward and easy to use
* *Flexibility* – You can customize the serialization of each packet field or even the whole packet
* *Expandable* – Paklet can be easily extended to automate the serialization of custom types.
* *Speed* – Bytecode manipulation in the background ensures the packet serialization is fast

### Usage

#### Creating a packet class

All classes annotated with [`@Packet`](paklet-api/src/main/java/org/machinemc/paklet/Packet.java)
are considered packets. All their fields, if they are not `static`, `transient`,
or annotated with [`@Ignore`](paklet-api/src/main/java/org/machinemc/paklet/modifiers/Ignore.java)
are automatically serialized by default serializers if not specified otherwise.

All field values are expected to be not null by default, but this behaviour can be changed
with the use of [`@Optional`](paklet-api/src/main/java/org/machinemc/paklet/modifiers/Optional.java) annotation.

```java
@Packet(id = 0x00, group = "ClientBoundStatus", catalogue = ClientBoundStatusPackets.class)
public class StatusResponsePacket {

    private String jsonResponse;
    
    // getters and setters

}
```
Each packet needs to specify its numeric ID, catalogue class, and can specify its group in case the packet IDs
overlap, this is later used for deserialization.

Packet IDs can be alternatively resolved using [`@PacketID`](paklet-api/src/main/java/org/machinemc/paklet/PacketID.java)
annotation for packets that require dynamic IDs.

```java
@Packet(id = DYNAMIC_PACKET, group = "ClientBoundStatus", catalogue = ClientBoundStatusPackets.class)
public class StatusResponsePacket {

    @PacketID
    public static final int ID = PacketUtil.getID(StatusResponsePacket.class);
    
    private String jsonResponse;
    
    // getters and setters

}
```

To specify which serializer to use for each packet field serialization, Paklet offers
[`@SerializeWith`](paklet-api/src/main/java/org/machinemc/paklet/modifiers/SerializeWith.java) annotation
and [serializer annotation aliases](paklet-api/src/main/java/org/machinemc/paklet/serialization/aliases/SerializerAlias.java).

```java
@Packet(id = 0x01, catalogue = TestPackets.class)
public class PingPacket {

    // with the usage of SerializeWith
    private @SerializeWith(VarIntSerializer.class) int value;

    // with the alias
    private @VarInt int alias;

}
```

Some types can be annotated with additional metadata. Default set of supported metadata by
Paklet can be found in [metadata package](paklet-api/src/main/java/org/machinemc/paklet/metadata).

Both modifiers and metadata can be used for parameters, e.g. `List<@VarLong Long>`.

For packets that require fully custom serialization, Paklet offers the
[`CustomPacket`](paklet-api/src/main/java/org/machinemc/paklet/CustomPacket.java) interface. This is
meant to be used for packets that require more complicated serialization that can not be
resolved with automatic serialization.

> [!NOTE]
> Paklet also offers [`VarIntSerializer`](paklet-api/src/main/java/org/machinemc/paklet/serialization/VarIntSerializer.java)
> and [`VarLongSerializer`](paklet-api/src/main/java/org/machinemc/paklet/serialization/VarLongSerializer.java) compatible with the Minecraft Java Protocol.

For more examples see tests of the `paklet-core` module.

#### Custom Serializer

Paklet allows simple way of creating custom serializers.
To create a custom serializer, implement [`Serializer`](paklet-api/src/main/java/org/machinemc/paklet/serialization/Serializer.java) interface.

```java
@Supports({Integer.class, int.class})
public class MyCustomSerializer implements Serializer<Integer> {
    
    @Override
    public void serialize(SerializerContext context, DataVisitor visitor, Integer value) {
        // serialization
    }

    @Override
    public Integer deserialize(SerializerContext context, DataVisitor visitor) {
        // deserialization
    }

}
```

[`@Supports`](paklet-api/src/main/java/org/machinemc/paklet/serialization/Supports.java) annotation specifies which types the serializer supports,
if more complex rule for choosing the types is needed (e.g. array types), the array can stay empty and custom
[`SerializationRule`](paklet-api/src/main/java/org/machinemc/paklet/serialization/rule/SerializationRule.java) needs to be implemented.

All serializers annotated with [`@DefaultSerializer`](paklet-api/src/main/java/org/machinemc/paklet/serialization/DefaultSerializer.java) are automatically registered with given catalogue.

[`SerializerContext`](paklet-api/src/main/java/org/machinemc/paklet/serialization/SerializerContext.java) provided for each serialization action gives
access to currently registered serializers and type of field that is currently being serialized.

##### Provided serializers by Paklet

| Serializer                                                                                              | Supported types                                                                             | Catalogue                                                                                                             |
|---------------------------------------------------------------------------------------------------------|---------------------------------------------------------------------------------------------|-----------------------------------------------------------------------------------------------------------------------|
| Boolean                                                                                                 | `Boolean, boolean`                                                                          | [`DefaultSerializers`](paklet-api/src/main/java/org/machinemc/paklet/serialization/catalogue/DefaultSerializers.java) |
| Byte                                                                                                    | `Byte, byte`                                                                                | [`DefaultSerializers`](paklet-api/src/main/java/org/machinemc/paklet/serialization/catalogue/DefaultSerializers.java) |
| Short                                                                                                   | `Short, short`                                                                              | [`DefaultSerializers`](paklet-api/src/main/java/org/machinemc/paklet/serialization/catalogue/DefaultSerializers.java) |
| Integer                                                                                                 | `Integer, int`                                                                              | [`DefaultSerializers`](paklet-api/src/main/java/org/machinemc/paklet/serialization/catalogue/DefaultSerializers.java) |
| Long                                                                                                    | `Long, long`                                                                                | [`DefaultSerializers`](paklet-api/src/main/java/org/machinemc/paklet/serialization/catalogue/DefaultSerializers.java) |
| Float                                                                                                   | `Float, float`                                                                              | [`DefaultSerializers`](paklet-api/src/main/java/org/machinemc/paklet/serialization/catalogue/DefaultSerializers.java) |
| Double                                                                                                  | `Double, double`                                                                            | [`DefaultSerializers`](paklet-api/src/main/java/org/machinemc/paklet/serialization/catalogue/DefaultSerializers.java) |
| Character                                                                                               | `Character, char`                                                                           | [`DefaultSerializers`](paklet-api/src/main/java/org/machinemc/paklet/serialization/catalogue/DefaultSerializers.java) |
| Number                                                                                                  | `Number, BigDecimal, BigInteger`                                                            | [`DefaultSerializers`](paklet-api/src/main/java/org/machinemc/paklet/serialization/catalogue/DefaultSerializers.java) |
| String                                                                                                  | `String`                                                                                    | [`DefaultSerializers`](paklet-api/src/main/java/org/machinemc/paklet/serialization/catalogue/DefaultSerializers.java) |
| Collection                                                                                              | `Collection, SequencedCollection, List, LinkedList, ArrayList, Set, LinkedHashSet, HashSet` | [`DefaultSerializers`](paklet-api/src/main/java/org/machinemc/paklet/serialization/catalogue/DefaultSerializers.java) |
| Map                                                                                                     | `Map, SequencedMap, HashMap, LinkedHashMap, TreeMap`                                        | [`DefaultSerializers`](paklet-api/src/main/java/org/machinemc/paklet/serialization/catalogue/DefaultSerializers.java) |
| UUID                                                                                                    | `UUID`                                                                                      | [`DefaultSerializers`](paklet-api/src/main/java/org/machinemc/paklet/serialization/catalogue/DefaultSerializers.java) |
| Instant                                                                                                 | `Instant`                                                                                   | [`DefaultSerializers`](paklet-api/src/main/java/org/machinemc/paklet/serialization/catalogue/DefaultSerializers.java) |
| BitSet                                                                                                  | `BitSet`                                                                                    | [`DefaultSerializers`](paklet-api/src/main/java/org/machinemc/paklet/serialization/catalogue/DefaultSerializers.java) |
| Enum                                                                                                    | none, has to be specified                                                                   | [`DefaultSerializers`](paklet-api/src/main/java/org/machinemc/paklet/serialization/catalogue/DefaultSerializers.java) |
| Array                                                                                                   | none, has to be specified                                                                   | [`DefaultSerializers`](paklet-api/src/main/java/org/machinemc/paklet/serialization/catalogue/DefaultSerializers.java) |
| Serializable                                                                                            | none, has to be specified                                                                   | [`DefaultSerializers`](paklet-api/src/main/java/org/machinemc/paklet/serialization/catalogue/DefaultSerializers.java) |
| [VarIntSerializer](paklet-api/src/main/java/org/machinemc/paklet/serialization/VarIntSerializer.java)   | `Integer, int`                                                                              | none                                                                                                                  |
| [VarLongSerializer](paklet-api/src/main/java/org/machinemc/paklet/serialization/VarLongSerializer.java) | `Long, long`                                                                                | none                                                                                                                  |

For more examples of serializer implementation, see [`Serializers`](paklet-api/src/main/java/org/machinemc/paklet/serialization/Serializers.java).

#### Packet Crafting

To read and write packets, instance of [`PacketFactory`](paklet-api/src/main/java/org/machinemc/paklet/PacketFactory.java) is required.
The default implementation is provided in `paklet-core` module as [`PacketFactoryImpl`](paklet-core/src/main/java/org/machinemc/paklet/PacketFactoryImpl.java).

```java
public static PacketFactory createFactory() {
    // Creates new serializer provider
    SerializerProvider serializerProvider = new SerializerProviderImpl();
    
    // Registers default serializers and serialization rules provided by Paklet
    serializerProvider.addSerializers(DefaultSerializers.class);
    serializerProvider.addSerializationRules(DefaultSerializationRules.class);

    // Creates new packet factory
    PacketFactory packetFactory = new PacketFactoryImpl(PacketEncoder.varInt(), serializerProvider);
    
    // Registers custom packets
    packetFactory.addPackets(MyPacketsCatalogue.class);

    return packetFactory;
}
```

Packets can be then read and written as such:

```java
// implementation of data visitor backed by netty's byte buffer provided by Paklet.
DataVisitor visitor = new NettyDataVisitor(Unpooled.buffer());

MyPacket packet = new MyPacket();
packet.setContent("Hello World");

// Serialization
factory.write(packet, visitor);

// Deserialization
MyPacket packetClone = factory.create(packetGroup, visitor);
```

Paklet has much more tools to offer than shown in this README, developers are encouraged to explore the source code
and discover additional APIs! :)

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
> Gradle plugin is a key feature of Paklet. It modifies bytecode of compiled packet classes later
> used by generated packet readers and writers to achieve higher speeds.
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