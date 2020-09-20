package spicy.modules.movement;

import org.lwjgl.input.Keyboard;

import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import spicy.events.Event;
import spicy.events.listeners.EventMotion;
import spicy.events.listeners.EventUpdate;
import spicy.events.listeners.EventOnLadder;
import spicy.modules.Module;
import spicy.settings.ModeSetting;

public class Spider extends Module {
	
	private ModeSetting mode = new ModeSetting("Mode", "Vanilla", "Vanilla");
	
	public Spider() {
		super("Spider", Keyboard.KEY_NONE, Category.MOVEMENT);
		resetSettings();
	}
	
	@Override
	public void resetSettings() {
		this.settings.clear();
		this.addSettings(mode);
	}
	
	public void onEnable() {
		
	}
	
	public void onDisable() {
		
	}
	
	public void onEvent(Event e) {
		
		if (e instanceof EventUpdate) {
			
			if (e.isPre()) {
				
				this.additionalInformation = mode.getMode();
				
			}
			
		}
		
		if (e instanceof EventOnLadder) {
			if (e.isPost()) {
				if (mc.thePlayer.isCollidedHorizontally && mode.is("Vanilla")) {
					((EventOnLadder) e).onLadder = true;
				}
			}
		}
		
	}
	
}
