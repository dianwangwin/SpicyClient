package spicy.modules.world;

import org.lwjgl.input.Keyboard;

import net.minecraft.network.play.client.C03PacketPlayer;
import spicy.events.Event;
import spicy.events.listeners.EventUpdate;
import spicy.modules.Module;

public class FastPlace extends Module {

	public FastPlace() {
		super("Fast Place", Keyboard.KEY_NONE, Category.WORLD);
	}
	
	public void onEnable() {
		
	}
	
	public void onDisable() {
		
	}
	
	public void onEvent(Event e) {
		
		if (e instanceof EventUpdate) {
			
			if (e.isPre()) {
				
				mc.rightClickDelayTimer = 0;
				
			}
			
		}
		
	}
	
}
