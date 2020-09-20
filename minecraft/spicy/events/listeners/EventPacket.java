package spicy.events.listeners;

import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.network.play.INetHandlerPlayServer;
import spicy.events.Event;
import spicy.events.EventDirection;
import spicy.events.EventType;

public class EventPacket extends Event{
	
	public Packet packet;
	
	public EventPacket(EventType type, EventDirection dir, Packet packet) {
		
		this.setType(type);
		this.setDirection(dir);
		this.packet = packet;
		
	}
	
}
