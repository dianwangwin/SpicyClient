package spicy.modules.combat;

import org.lwjgl.input.Keyboard;

import spicy.events.Event;
import spicy.events.listeners.EventGetBlockReach;
import spicy.events.listeners.EventUpdate;
import spicy.modules.Module;
import spicy.settings.NumberSetting;
import spicy.util.Timer;

public class Reach extends Module {
	
	private NumberSetting reach = new NumberSetting("Reach", 3, 3, 6, 0.1);
	
	public Reach() {
		super("Reach", Keyboard.KEY_NONE, Category.BETA);
		resetSettings();
	}
	
	@Override
	public void resetSettings() {
		this.settings.clear();
		this.addSettings(reach);
	}
	
	public void onEnable() {
		
	}
	
	public void onDisable() {
		mc.gameSettings.keyBindUseItem.pressed = false;
	}
	
	public Timer timer = new Timer();
	
	public void onEvent(Event e) {
		
		if (e instanceof EventUpdate) {
			
			if (e.isPre()) {
				
				this.additionalInformation = "" + reach.getValue();
				
			}
			
		}
		
		if (e instanceof EventGetBlockReach) {
			
			EventGetBlockReach event = (EventGetBlockReach) e;
			
			event.setCanceled(true);
			event.reach = (float) reach.getValue();
			
		}
		
	}
	
}
