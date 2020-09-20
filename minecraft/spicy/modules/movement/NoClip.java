package spicy.modules.movement;

import org.lwjgl.input.Keyboard;

import spicy.events.Event;
import spicy.events.listeners.EventMotion;
import spicy.events.listeners.EventPlayerUseItem;
import spicy.events.listeners.EventUpdate;
import spicy.modules.Module;
import spicy.modules.Module.Category;

public class NoClip extends Module {
	
	public NoClip() {
		super("NoClip", Keyboard.KEY_NONE, Category.BETA);
	}

	
	public void onEnable() {
		
	}
	
	public void onDisable() {
		
	}
	
	public void onEvent(Event e) {
		if (mc.thePlayer.isCollidedHorizontally) {
			mc.thePlayer.noClip = true;
			
			if (mc.thePlayer.motionX > 0) {
				mc.thePlayer.motionX += 0.75;
			}
			else if (mc.thePlayer.motionX < 0) {
				mc.thePlayer.motionX -= 0.75;
			}
			
			if (mc.thePlayer.motionZ > 0) {
				mc.thePlayer.motionZ += 0.75;
			}
			else if (mc.thePlayer.motionZ < 0) {
				mc.thePlayer.motionZ -= 0.75;
			}
			this.toggle();
		}else {
			mc.thePlayer.motionX = 0;
			mc.thePlayer.motionZ = 0;
			this.toggle();
		}
	}
	
	
}
