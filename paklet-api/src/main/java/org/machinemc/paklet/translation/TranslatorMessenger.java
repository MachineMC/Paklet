package org.machinemc.paklet.translation;

import org.jetbrains.annotations.ApiStatus;

import java.util.function.Consumer;

@ApiStatus.Internal
@FunctionalInterface
public interface TranslatorMessenger<T /* Packet */> extends Consumer<T> {
}
