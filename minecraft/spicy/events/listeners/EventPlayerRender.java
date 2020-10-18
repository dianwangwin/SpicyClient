package spicy.events.listeners;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.entity.Entity;
import spicy.events.Event;

public class EventPlayerRender extends Event<EventPlayerRender> {
	
	public EventPlayerRender(AbstractClientPlayer entity) {
		
		this.entity = entity;
		
	}
	
	public AbstractClientPlayer entity;
	
}
