package info.spicyclient.modules.render;

import org.lwjgl.input.Keyboard;

import info.spicyclient.chatCommands.Command;
import info.spicyclient.events.Event;
import info.spicyclient.events.listeners.EventKey;
import info.spicyclient.events.listeners.EventTick;
import info.spicyclient.events.listeners.EventUpdate;
import info.spicyclient.modules.Module;
import info.spicyclient.portedMods.antiantixray.Mixins.TickMixin;
import info.spicyclient.settings.KeybindSetting;
import info.spicyclient.settings.NumberSetting;

public class AntiAntiXray extends Module {

	public AntiAntiXray() {
		super("AntiAntiXray", Keyboard.KEY_NONE, Category.RENDER);
		resetSettings();
	}
	
	public NumberSetting delay = new NumberSetting("Delay", 0, 0, 1000, 10),
			diameter = new NumberSetting("Diameter", 6, 1, 20, 1);
	public KeybindSetting checkBlocks = new KeybindSetting("Check blocks", Keyboard.KEY_Y);
	
	@Override
	public void resetSettings() {
		this.settings.clear();
		this.addSettings(checkBlocks, delay, diameter);
	}
	
	public static transient boolean lastTickPressed = false;
	
	@Override
	public void onDisable() {
		lastTickPressed = true;
	}
	
	@Override
	public void onEvent(Event e) {
		
		if (e instanceof EventUpdate && e.isPre()) {
			
			if (info.spicyclient.portedMods.antiantixray.AntiAntiXray.rvn.getKeyCode() != checkBlocks.getKeycode()) {
				info.spicyclient.portedMods.antiantixray.AntiAntiXray.rvn.setKeyCode(checkBlocks.getKeycode());
			}
			
			if (info.spicyclient.portedMods.antiantixray.Etc.Config.rad != (int) (diameter.getValue() / 2)) {
				info.spicyclient.portedMods.antiantixray.Etc.Config.rad = (int) (diameter.getValue() / 2);
			}
			
			if (info.spicyclient.portedMods.antiantixray.Etc.Config.delay != ((long)delay.getValue())) {
				info.spicyclient.portedMods.antiantixray.Etc.Config.delay = ((long)delay.getValue());
			}
			
			if (info.spicyclient.portedMods.antiantixray.AntiAntiXray.rvn.checkPressed() && !lastTickPressed) {
				lastTickPressed = true;
			}
			else if (!info.spicyclient.portedMods.antiantixray.AntiAntiXray.rvn.checkPressed() && lastTickPressed) {
				lastTickPressed = false;
			}
			
		}
		
		if (e instanceof EventTick && e.isPre()) {
			
			TickMixin.tick();
			
		}
		
	}
	
}
