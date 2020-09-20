package spicy.modules.world;

import org.lwjgl.input.Keyboard;

import net.minecraft.network.play.client.C03PacketPlayer;
import spicy.events.Event;
import spicy.events.listeners.EventUpdate;
import spicy.modules.Module;

public class NoFall extends Module {

	public NoFall() {
		super("No Fall", Keyboard.KEY_NONE, Category.WORLD);
	}
	
	public void onEnable() {
		
	}
	
	public void onDisable() {
		
	}
	
	public void onEvent(Event e) {
		
		if (e instanceof EventUpdate) {
			
			if (e.isPre()) {
				
				if (mc.thePlayer.fallDistance > 2) {
					
					mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer(true));
					
				}
				
			}
			
		}
		
	}
	
}
