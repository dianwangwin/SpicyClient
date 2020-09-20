package spicy.modules.render;

import java.util.Random;

import org.lwjgl.input.Keyboard;

//import io.netty.util.internal.ThreadLocalRandom;
import spicy.SpicyClient;
import spicy.events.Event;
import spicy.events.listeners.EventUpdate;
import spicy.modules.Module;
import spicy.settings.NumberSetting;

public class RainbowGUI extends Module {
	
	public NumberSetting speed = new NumberSetting("Speed", 95, 1, 100, 0.2);
	
	public RainbowGUI() {
		super("RainbowGUI", Keyboard.KEY_NONE, Category.RENDER);
		resetSettings();
	}
	
	@Override
	public void resetSettings() {
		this.settings.clear();
		this.addSettings(speed);
	}
	
	public void onEnable() {
		TabGUI t = (TabGUI) this.findModule(this.getModuleName(new TabGUI()));
		t.rainbowEnabled = true;
		SpicyClient.hud.rainbowEnabled = true;
	}
	
	public void onDisable() {
		TabGUI t = (TabGUI) this.findModule(this.getModuleName(new TabGUI()));
		t.rainbowEnabled = false;
		SpicyClient.hud.rainbowEnabled = false;
	}
	
	private Random rand = new Random();
	private double counter = 0;
	private int count = 0;
	
	public void onEvent(Event e) {
		
		if (e instanceof EventUpdate) {
			
			if (e.isPre()) {
				
				TabGUI t = (TabGUI) this.findModule(this.getModuleName(new TabGUI()));
				
				t.rainbowEnabled = true;
				t.rainbowTimer = 100.5f - speed.getValue();
				
				SpicyClient.hud.rainbowEnabled = true;
				SpicyClient.hud.rainbowTimer = 100.5f - speed.getValue();
				
			}
			
		}
		
	}
	
}