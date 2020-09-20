package spicy.events.listeners;

import spicy.events.Event;

public class EventAddBlockDamage extends Event<EventAddBlockDamage> {
	
	public int Damage = 1;

	public int getDamage() {
		return Damage;
	}

	public void setDamage(int damage) {
		Damage = damage;
	}
	
}
