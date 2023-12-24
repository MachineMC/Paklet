package org.machinemc.paklet.serializers;

/**
 * Exception thrown if no serializer for given class has been found.
 */
public class NoSuchSerializerException extends RuntimeException {

    public NoSuchSerializerException() {
        super();
    }

    public NoSuchSerializerException(String message) {
        super(message);
    }

    public NoSuchSerializerException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoSuchSerializerException(Throwable cause) {
        super(cause);
    }

}
