package spicy.modules.world;

import org.lwjgl.input.Keyboard;

import net.minecraft.network.play.client.C03PacketPlayer;
import spicy.events.Event;
import spicy.events.listeners.EventAddBlockDamage;
import spicy.events.listeners.EventUpdate;
import spicy.modules.Module;
import spicy.settings.NumberSetting;

public class FastBreak extends Module {
	
	private NumberSetting speed = new NumberSetting("Speed", 2, 1, 20, 1);
	
	public FastBreak() {
		super("Fast Break", Keyboard.KEY_NONE, Category.WORLD);
		resetSettings();
	}
	
	@Override
	public void resetSettings() {
		settings.clear();
		addSettings(speed);
	}
	
	public void onEnable() {
		
	}
	
	public void onDisable() {
		
	}
	
	public void onEvent(Event e) {
		
		if (e instanceof EventAddBlockDamage) {
			
			if (e.isPre()) {
				
				EventAddBlockDamage blockDamage = (EventAddBlockDamage) e;
				blockDamage.setDamage((int) speed.getValue());
				
			}
			
		}
		
	}
	
}
