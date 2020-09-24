package spicy.modules.player;

import org.lwjgl.input.Keyboard;

import net.minecraft.network.play.client.C03PacketPlayer;
import spicy.chatCommands.Command;
import spicy.events.Event;
import spicy.events.listeners.EventMotion;
import spicy.events.listeners.EventUpdate;
import spicy.modules.Module;
import spicy.settings.ModeSetting;

public class NoFall extends Module {
	
	public ModeSetting noFallMode = new ModeSetting("NoFall Mode", "Vanilla", "Vanilla", "Packet");
	
	public NoFall() {
		super("No Fall", Keyboard.KEY_NONE, Category.PLAYER);
		resetSettings();
	}
	
	@Override
	public void resetSettings() {
		this.settings.clear();
		this.addSettings(noFallMode);
	}
	
	public void onEnable() {
		
	}
	
	public void onDisable() {
		
	}
	
	public void onEvent(Event e) {
		
		if (e instanceof EventMotion && e.isPre() && noFallMode.is("Vanilla") && mc.thePlayer.fallDistance > 2) {
			
			EventMotion event = (EventMotion) e;
			event.onGround = true;
			
		}
		
		if (e instanceof EventUpdate) {
			
			if (e.isPre()) {
				
				if (mc.thePlayer.fallDistance > 2 && noFallMode.is("Packet")) {
					
					mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer(true));
					
				}
				
			}
			
		}
		
	}
	
}
