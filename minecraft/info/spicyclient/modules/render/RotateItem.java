package info.spicyclient.modules.render;

import org.lwjgl.input.Keyboard;

import info.spicyclient.events.Event;
import info.spicyclient.events.listeners.EventUpdate;
import info.spicyclient.modules.Module;
import info.spicyclient.settings.NumberSetting;

public class RotateItem extends Module {

	public RotateItem() {
		super("RotateItem", Keyboard.KEY_NONE, Category.RENDER);
		resetSettings();
	}
	
	@Override
	public void resetSettings() {
		this.settings.clear();
		this.addSettings(speed);
	}
	
	public NumberSetting speed = new NumberSetting("speed", 1, 0.1, 5, 0.1);
	
	@Override
	public void onEvent(Event e) {
		
		if (e instanceof EventUpdate && e.isPre()) {
			this.additionalInformation = speed.getValue() + "";
		}
		
	}
	
}
