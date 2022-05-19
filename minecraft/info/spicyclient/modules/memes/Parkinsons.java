package info.spicyclient.modules.memes;

import java.util.Random;

import org.lwjgl.input.Keyboard;

import info.spicyclient.events.Event;
import info.spicyclient.events.listeners.EventUpdate;
import info.spicyclient.modules.Module;

public class Parkinsons extends Module {

	public Parkinsons() {
		super("Parkinsons", Keyboard.KEY_NONE, Category.MEMES);
	}
	
	@Override
	public void onEvent(Event e) {
		
		if (e instanceof EventUpdate && e.isPre()) {
			mc.thePlayer.rotationYaw += new Random().nextInt(10) - 5;
			mc.thePlayer.rotationPitch += new Random().nextInt(10) - 5;
		}
		
	}
	
}
