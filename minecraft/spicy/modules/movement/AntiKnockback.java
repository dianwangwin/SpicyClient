package spicy.modules.movement;

import org.lwjgl.input.Keyboard;

import net.minecraft.network.play.server.S27PacketExplosion;
import net.minecraft.util.MathHelper;
import spicy.events.Event;
import spicy.events.listeners.EventMotion;
import spicy.events.listeners.EventPacket;
import spicy.events.listeners.EventReceivedKnockbackPacket;
import spicy.events.listeners.EventUpdate;
import spicy.modules.Module;

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
