package org.machinemc.paklet.serialization;

/**
 * Token serves as a tool to create annotated types within your code. For tokens to work
 * it is required to always create new implementation of the class as shown in the example
 * below.
 * <p>
 * It can be used with {@link SerializerContext#withType(Token)} to resolve
 * more specific contexts where annotations are required.
 * <p>
 * Example:
 * {@code context.withType(new Token<@SerializeWith(Serializers.Integer.class) Integer>() {})}
 */
public class Token<T> {
}
