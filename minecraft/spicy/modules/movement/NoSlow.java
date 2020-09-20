package spicy.modules.movement;

import org.lwjgl.input.Keyboard;

import spicy.events.Event;
import spicy.events.listeners.EventPlayerUseItem;
import spicy.events.listeners.EventUpdate;
import spicy.modules.Module;

public class NoSlow extends Module {

	public NoSlow() {
		super("NoSlow", Keyboard.KEY_NONE, Category.MOVEMENT);
	}

	
	public void onEnable() {
		
	}
	
	public void onDisable() {
		
	}
	
	public void onEvent(Event e) {
		
		if (e instanceof EventPlayerUseItem) {
			
			if (e.isPre()) {
				e.setCanceled(true);
			}
			
		}
		
	}
	
}
