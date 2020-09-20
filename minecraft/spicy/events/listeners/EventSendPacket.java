package spicy.events.listeners;

import net.minecraft.network.Packet;
import spicy.events.Event;

public class EventSendPacket extends Event<EventSendPacket> {
	
	public EventSendPacket(Packet packet) {
		this.packet = packet;
	}
	
	public Packet packet = null;
	
}
