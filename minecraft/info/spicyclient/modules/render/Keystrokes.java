package info.spicyclient.modules.render;

import java.util.List;

import org.lwjgl.input.Keyboard;

import info.spicyclient.SpicyClient;
import info.spicyclient.events.Event;
import info.spicyclient.events.listeners.EventKey;
import info.spicyclient.events.listeners.EventRenderGUI;
import info.spicyclient.events.listeners.EventUpdate;
import info.spicyclient.modules.Module;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;

public class Keystrokes extends Module {

	public Keystrokes() {
		super("Keystrokes", Keyboard.KEY_NONE, Category.RENDER);
	}
	
	public void onEnable() {
		
	}
	
	public void onDisable() {
		
	}
	
	public void onEvent(Event e) {
		
		if (e instanceof EventRenderGUI && e.isPre()) {
			SpicyClient.config.hudModConfig.keystrokes1.draw(false);
		}
		
	}
	
}
