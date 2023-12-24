package org.machinemc.paklet.translation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to mark translation handlers (packet listeners).
 * <p>
 * Each method with this annotation is required to have only a single argument of the
 * packet it handles. Optionally it can return {@link TranslationResult}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface TranslationHandler {

    TranslationState value();

}
