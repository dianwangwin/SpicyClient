package info.spicyclient.modules.combat;

import java.util.Random;

import org.apache.commons.lang3.RandomUtils;
import org.lwjgl.input.Keyboard;

import info.spicyclient.chatCommands.Command;
import info.spicyclient.events.Event;
import info.spicyclient.events.listeners.EventGetBlockReach;
import info.spicyclient.events.listeners.EventReceivePacket;
import info.spicyclient.events.listeners.EventSendPacket;
import info.spicyclient.events.listeners.EventUpdate;
import info.spicyclient.modules.Module;
import info.spicyclient.settings.NumberSetting;
import info.spicyclient.util.MovementUtils;
import info.spicyclient.util.Timer;
import net.minecraft.client.Minecraft;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C02PacketUseEntity.Action;
import net.minecraft.network.play.client.C03PacketPlayer;

public class Criticals extends Module {
	
	public Criticals() {
		super("Criticals", Keyboard.KEY_NONE, Category.BETA);
	}
	
	public void onEnable() {
		
	}
	
	public void onDisable() {
		
	}
	
	public Timer timer = new Timer();
	
	private static transient int stage = 1;
	
	public void onEvent(Event e) {
		
		if (e instanceof EventSendPacket) {
			Packet packet = ((EventSendPacket)e).packet;
			
			if (packet instanceof C02PacketUseEntity) {
				
				if (!MovementUtils.isOnGround(0.0000001)) {
					return;
				}
				
				C02PacketUseEntity attack = (C02PacketUseEntity) packet;
				
				if (timer.hasTimeElapsed(1000, true) && attack.getAction() == Action.ATTACK) {
					
					double[] crits = new double[] {0.11, 0.1100013579, 0.1090013579};
					
		            for(short i = 0; i < crits.length; i++) {
		                double offset = crits[i];
		                mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + offset, mc.thePlayer.posZ, false));
		            }
		            
		            try {
		            	mc.thePlayer.onCriticalHit(mc.theWorld.getEntityByID(attack.getEntityId()));
					} catch (Exception e2) {
						
					}
		            
				}
				
			}
			
		}
		
	}
	
}
