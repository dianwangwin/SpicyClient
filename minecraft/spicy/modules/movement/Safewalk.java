package spicy.modules.movement;

import java.io.IOException;

import org.lwjgl.input.Keyboard;

import net.minecraft.entity.player.EntityPlayer;
import spicy.events.Event;
import spicy.events.listeners.EventMotion;
import spicy.events.listeners.EventSneaking;
import spicy.events.listeners.EventUpdate;
import spicy.modules.Module;
import spicy.settings.NumberSetting;

public class Safewalk extends Module {

	public Safewalk() {
		super("Safewalk", Keyboard.KEY_NONE, Category.MOVEMENT);
	}
	
	public void onEnable() {
		
	}
	
	public void onDisable() {
		
	}
	
	public void onEvent(Event e) {
		
		if (e instanceof EventSneaking) {
			
			if (e.isPre()) {
				
				EventSneaking sneak = (EventSneaking) e;
				
				if (sneak.entity.onGround && sneak.entity instanceof EntityPlayer) {
					sneak.sneaking = true;
				}else {
					sneak.sneaking = false;
				}
				sneak.offset = -1D;
				sneak.revertFlagAfter = true;
				
			}
			
		}
		
	}
	
}
