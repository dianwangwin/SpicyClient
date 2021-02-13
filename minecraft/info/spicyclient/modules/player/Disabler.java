package info.spicyclient.modules.player;

import java.util.ArrayList;
import java.util.Random;

import org.lwjgl.input.Keyboard;

import info.spicyclient.SpicyClient;
import info.spicyclient.chatCommands.Command;
import info.spicyclient.events.Event;
import info.spicyclient.events.listeners.EventPacket;
import info.spicyclient.events.listeners.EventSendPacket;
import info.spicyclient.events.listeners.EventUpdate;
import info.spicyclient.modules.Module;
import info.spicyclient.notifications.Color;
import info.spicyclient.notifications.NotificationManager;
import info.spicyclient.notifications.Type;
import info.spicyclient.util.MovementUtils;
import info.spicyclient.util.Timer;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.network.Packet;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.login.server.S00PacketDisconnect;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.client.C0CPacketInput;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.client.C13PacketPlayerAbilities;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S32PacketConfirmTransaction;

public class Disabler extends Module {
	public Disabler() {
		super("Disabler", Keyboard.KEY_NONE, Category.PLAYER);
	}
	
	public static transient boolean watchdog = false;
	public static transient ArrayList<Packet> packets = new ArrayList<Packet>();
	public static transient Timer ping = new Timer();
	
	@Override
	public void onEnable() {
		NotificationManager.getNotificationManager().createNotification("Relog for the disabler to take effect", "", true, 5000, Type.INFO, Color.PINK);
		watchdog = false;
	}
	
	@Override
	public void onDisable() {
		watchdog = false;
		
		for (Packet p : packets) {
			
			mc.getNetHandler().getNetworkManager().sendPacketNoEvent(p);
			
		}
		
		packets.clear();
		
	}
	
	@Override
	public void onEvent(Event e) {
		
		if (e instanceof EventUpdate && e.isPre()) {
			
			this.additionalInformation = "Hypixel (fixed motherboard edition)";
			
			if (SpicyClient.config.pingSpoof.isEnabled()) {
				SpicyClient.config.pingSpoof.toggle();
				NotificationManager.getNotificationManager().createNotification("Disabler", "Pingspoof was disabled to prevent flags", true, 2000, Type.INFO, Color.RED);
			}
			
			if (mc.thePlayer.ticksExisted < 5) {
				for (Packet p : packets) {
					
					mc.getNetHandler().getNetworkManager().sendPacketNoEvent(p);
					
				}
				packets.clear();
			}
			
			/*
            PlayerCapabilities playerCapabilities = new PlayerCapabilities();
            playerCapabilities.isFlying = true;
            playerCapabilities.allowFlying = true;
            //playerCapabilities.setFlySpeed((float) ((Math.random() * (9.0 - 0.1)) + 0.1));
            playerCapabilities.setFlySpeed((float) (9.0 + (new Random()).nextDouble() * (9.8 - 9.0)));
            playerCapabilities.isCreativeMode = true;
            mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C13PacketPlayerAbilities(playerCapabilities));
            */
			
           int basePing = 5000;
           
            if (ping.hasTimeElapsed(basePing + new Random().nextInt(2000), true) && packets.size() > 0) {
            	
				for (Packet p : packets) {
					
					mc.getNetHandler().getNetworkManager().sendPacketNoEvent(p);
					
				}
				packets.clear();
				
            }else {
                PlayerCapabilities playerCapabilities = new PlayerCapabilities();
                playerCapabilities.isFlying = true;
                playerCapabilities.allowFlying = true;
                playerCapabilities.setFlySpeed((float) (9.0 + (new Random()).nextDouble() * (9.8 - 9.0)));
                playerCapabilities.isCreativeMode = true;
                mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C13PacketPlayerAbilities(playerCapabilities));
            }
            
		}
		
		if (e instanceof EventPacket && e.isPre()) {
			
			if (((EventPacket)e).packet instanceof S00PacketDisconnect) {
				packets.clear();
			}
			
		}
		
		if (e instanceof EventSendPacket && e.isPre()) {
			
			EventSendPacket event = (EventSendPacket) e;
			
            if (event.packet instanceof C0FPacketConfirmTransaction) {
            	
                C0FPacketConfirmTransaction packetConfirmTransaction = (C0FPacketConfirmTransaction)event.packet;
                
                //mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C0FPacketConfirmTransaction(2147483647, packetConfirmTransaction.getUid(), false));
                //mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C0FPacketConfirmTransaction(1147483647, packetConfirmTransaction.getUid(), false));
                
                //packetConfirmTransaction.setAccepted(new Random().nextBoolean());
                //packetConfirmTransaction.setWindowId(Integer.MIN_VALUE + new Random().nextInt(1000));
                //packetConfirmTransaction.setUid(Short.MAX_VALUE);
            	//mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C0FPacketConfirmTransaction(Integer.MIN_VALUE, Short.MAX_VALUE, true));
                
                /*
                if (!SpicyClient.config.fly.isEnabled()) {
                	packets.add(packetConfirmTransaction);
                }
                */
                
                packets.add(packetConfirmTransaction);
                e.setCanceled(true);
            }

            if (event.packet instanceof C00PacketKeepAlive) {
            	
                packets.add(event.packet);
                e.setCanceled(true);
            	
            }
            
		}
		
	}
	
}