package spicy.events.listeners;

import net.minecraft.client.gui.inventory.GuiChest;
import spicy.events.Event;

public class EventOpenChest extends Event<EventOpenChest> {
	
	public EventOpenChest(GuiChest chest) {
		this.chest = chest;
	}
	
	public GuiChest chest = null;
	
}
