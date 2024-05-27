package org.machinemc.paklet;

import java.util.Objects;

/**
 * Allows to access the additional information during dynamic packet registration
 * for packets with {@link Packet#DYNAMIC_PACKET} ID.
 */
public class PacketRegistrationContext {

    protected static final ThreadLocal<PacketRegistrationContext> threadLocal = ThreadLocal.withInitial(PacketRegistrationContext::new);

    private final String group;

    /**
     * Returns the current packet registration context.
     * <p>
     * This can be called only within methods annotated with {@link PacketID}, resolving
     * packet IDs for packets with {@link Packet#DYNAMIC_PACKET} ID.
     *
     * @return current packet registration context
     */
    public static PacketRegistrationContext get() {
        PacketRegistrationContext context = threadLocal.get();
        if (context.group == null) throw new RuntimeException("Called outside of dynamic packet registration context");
        return context;
    }

    private PacketRegistrationContext() {
        group = null;
    }

    /**
     * Creates new packet registration context with given group.
     *
     * @param group group
     */
    protected PacketRegistrationContext(String group) {
        this.group = Objects.requireNonNull(group, "Packet group can not be null");
    }

    /**
     * Returns packet group used to register the packet.
     *
     * @return current packet group
     */
    public String getPacketGroup() {
        return group;
    }

}
