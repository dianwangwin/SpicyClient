package spicy.modules.movement;

import org.lwjgl.input.Keyboard;

import spicy.events.Event;
import spicy.events.listeners.EventUpdate;
import spicy.modules.Module;

public class Sprint extends Module {

	public Sprint() {
		super("Sprint", Keyboard.KEY_NONE, Category.MOVEMENT);
	}
	
	public void onEnable() {
		
	}
	
	public void onDisable() {
		mc.thePlayer.setSprinting(mc.gameSettings.keyBindSprint.isKeyDown());
	}
	
	public void onEvent(Event e) {
		
		if (e instanceof EventUpdate) {
			
			if (e.isPre()) {
				
				if (mc.thePlayer.moveForward > 0 && !mc.thePlayer.isUsingItem() && !mc.thePlayer.isSneaking() && !mc.thePlayer.isCollidedHorizontally) {
					mc.thePlayer.setSprinting(true);
					mc.thePlayer.arrowHitTimer = 0;
				}
				
			}
			
		}
		
	}
	
}
