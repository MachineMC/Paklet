package org.machinemc.paklet.test.packet;

import org.machinemc.paklet.Packet;
import org.machinemc.paklet.test.TestPackets;

import java.util.Currency;
import java.util.Date;

@Packet(id = 8, catalogue = TestPackets.class)
public class RulesTestingPacket {

    public Date date;
    public Currency currency;
    public State state;

    public enum State {
        COMPLETED, FAILED
    }

}
