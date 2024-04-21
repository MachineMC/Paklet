package org.machinemc.paklet.serialization.rule;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to specify default set of serialization rules.
 * <p>
 * Classes marked with this annotation need to have constructor with no arguments and
 * can be registered automatically.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface DefaultSerializationRule {

    /**
     * Specifies class that is used as catalogue (identifier) for the serialization rule.
     * <p>
     * All serialization rules within the same catalogue can be easily and automatically registered.
     *
     * @return catalogue source of the serialization rule
     */
    Class<?> value();

}
