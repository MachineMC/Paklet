package org.machinemc.paklet.test.packet;

import org.machinemc.paklet.Packet;

import java.util.Currency;
import java.util.Date;

@Packet(0x99)
public class RulesTestingPacket {

    public Date date;
    public Currency currency;
    public State state;

    public enum State {
        COMPLETED, FAILED
    }

}
