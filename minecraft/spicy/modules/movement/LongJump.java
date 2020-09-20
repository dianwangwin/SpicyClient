package spicy.modules.movement;

import org.lwjgl.input.Keyboard;

import spicy.events.Event;
import spicy.events.listeners.EventMotion;
import spicy.events.listeners.EventPlayerUseItem;
import spicy.events.listeners.EventUpdate;
import spicy.modules.Module;
import spicy.modules.Module.Category;

public class LongJump extends Module {
	
	public LongJump() {
		super("Long Jump", Keyboard.KEY_NONE, Category.BETA);
	}

	
	public void onEnable() {
		
	}
	
	public void onDisable() {
		
	}
	
	public void onEvent(Event e) {
		
		if (e instanceof EventUpdate) {
			
			if (e.isPre()) {
				
				EventUpdate event = (EventUpdate) e;
				
				
			}
			
		}
		
		if (e instanceof EventMotion) {
			
			if (e.isPre()) {
				
				EventMotion event = (EventMotion) e;
				
				if (mc.thePlayer.motionY < 0) {
					//mc.thePlayer.motionY = -0.03f;
				}
				
				mc.thePlayer.setSprinting(true);
				
			}
			
		}
		
	}
	
	
}
