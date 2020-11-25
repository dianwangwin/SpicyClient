package info.spicyclient.modules.movement;

import org.lwjgl.input.Keyboard;

import info.spicyclient.events.Event;
import info.spicyclient.events.listeners.EventMotion;
import info.spicyclient.events.listeners.EventPacket;
import info.spicyclient.events.listeners.EventReceivedKnockbackPacket;
import info.spicyclient.events.listeners.EventUpdate;
import info.spicyclient.modules.Module;
import net.minecraft.network.play.server.S27PacketExplosion;
import net.minecraft.util.MathHelper;

public class AntiKnockback extends Module {

	public AntiKnockback() {
		super("AntiKnockback", Keyboard.KEY_NONE, Category.MOVEMENT);
	}
	
	public void onEnable() {
		
	}
	
	public void onDisable() {
		
	}
	
	public void onEvent(Event e) {
		
		if (e instanceof EventReceivedKnockbackPacket) {
			
			if (e.isPre()) {
				e.setCanceled(true);
			}
			
		}
		
		if (e instanceof EventPacket) {
			
			if (e.isPre()) {
				
				if (e.isIncoming()) {
					
					EventPacket packetEvent = (EventPacket) e;
					
					if (packetEvent.packet instanceof S27PacketExplosion) {
						
						e.setCanceled(true);
						
					}
					
				}
				
			}
			
		}
		
	}
	
}
