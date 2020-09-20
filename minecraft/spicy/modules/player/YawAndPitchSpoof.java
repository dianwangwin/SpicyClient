package spicy.modules.player;

import java.util.Random;

import org.lwjgl.input.Keyboard;

import spicy.events.Event;
import spicy.events.listeners.EventMotion;
import spicy.modules.Module;
import spicy.settings.BooleanSetting;
import spicy.settings.ModeSetting;
import spicy.settings.NumberSetting;

public class YawAndPitchSpoof extends Module {
	
	ModeSetting pitchMode = new ModeSetting("Pitch Mode", "Down", "Down", "Up", "Middle", "No Spoof");
	ModeSetting yawMode = new ModeSetting("Yaw Mode", "Forward", "Forward", "Reversed");
	
	public YawAndPitchSpoof() {
		super("Yaw And Pitch Spoof", Keyboard.KEY_NONE, Category.PLAYER);
		resetSettings();
	}
	
	@Override
	public void resetSettings() {
		this.settings.clear();
		this.addSettings(pitchMode, yawMode);
	}
	
	@Override
	public void onEnable() {
		
	}
	
	@Override
	public void onEvent(Event e) {
		
		if (e instanceof EventMotion) {
			
			EventMotion event = (EventMotion) e;
			
			if (e.isBeforePre()) {
				
				if (pitchMode.is("Down")) {
					event.setPitch(90);
				}
				else if (pitchMode.is("Up")) {
					event.setPitch(-90);
				}
				else if (pitchMode.is("Middle")) {
					event.setPitch(0);
				}
				else if (pitchMode.is("No Spoof")) {
					
				}
				
				if (yawMode.is("Forward")) {
					
				}
				else if (yawMode.is("Reversed")) {
					event.setYaw(mc.thePlayer.rotationYaw + 180);
				}
				else if (yawMode.is("No Spoof")) {
					
				}
				
			}
			
		}
		
	}
	
}
