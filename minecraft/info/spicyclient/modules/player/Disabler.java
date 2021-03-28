package info.spicyclient.modules.player;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

import org.lwjgl.input.Keyboard;

import com.sun.corba.se.impl.logging.OMGSystemException;

import info.spicyclient.SpicyClient;
import info.spicyclient.bypass.Hypixel;
import info.spicyclient.chatCommands.Command;
import info.spicyclient.events.Event;
import info.spicyclient.events.listeners.EventReceivePacket;
import info.spicyclient.events.listeners.EventSendPacket;
import info.spicyclient.events.listeners.EventUpdate;
import info.spicyclient.modules.Module;
import info.spicyclient.notifications.Color;
import info.spicyclient.notifications.NotificationManager;
import info.spicyclient.notifications.Type;
import info.spicyclient.util.MovementUtils;
import info.spicyclient.util.Timer;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.network.Packet;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.login.server.S00PacketDisconnect;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.client.C0CPacketBoatInput;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.client.C13PacketPlayerAbilities;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S32PacketConfirmTransaction;
import net.minecraft.util.EnumFacing;

public class Disabler extends Module {
	public Disabler() {
		super("Disabler", Keyboard.KEY_NONE, Category.PLAYER);
	}
	
	public static transient boolean watchdog = false;
	public static transient CopyOnWriteArrayList<Packet> packets = new CopyOnWriteArrayList<Packet>();
	public static transient CopyOnWriteArrayList<Packet> packetsQueue = new CopyOnWriteArrayList<Packet>();
	public static transient Timer ping = new Timer();
	public transient static Thread senderThread = new Thread("Disabler Thread") {
		@Override
		public void run() {
			int pingInt = 354;
			while (true) {
				
				for (Packet p : packetsQueue) {
					if (!packets.contains(p)) {
						packetsQueue.add(p);
					}
					else {
						Command.sendPrivateChatMessage("bruh");
					}
				}
				
				packetsQueue = new CopyOnWriteArrayList<Packet>();
				
				if (Disabler.ping.hasTimeElapsed(pingInt, true)) {
					pingInt = 200 + new Random().nextInt(200);
					for (Packet p : packets) {
						
						try {
							mc.getNetHandler().getNetworkManager().sendPacketNoEvent(p);
						} catch (Exception e) {
							e.printStackTrace();
						}
						
					}
					System.out.println(pingInt);
					packets.clear();
				}
				
			}
		}
	};
	
	static {
		//senderThread.start();
	}
	
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
			
			this.additionalInformation = "Hypixel";
			
			if (ping.hasTimeElapsed(2500, true)) {
				mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, 0, mc.thePlayer.posZ, false));
				mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C08PacketPlayerBlockPlacement(mc.thePlayer.getPosition().add(0, 0, 0), EnumFacing.UP.getIndex(), mc.thePlayer.getCurrentEquippedItem(), 0, 0, 0));
				mc.thePlayer.noClip = true;
			}
			
			//if (SpicyClient.config.pingSpoof.isEnabled()) {
				//SpicyClient.config.pingSpoof.toggle();
				//NotificationManager.getNotificationManager().createNotification("Disabler", "Pingspoof was disabled to prevent flags", true, 2000, Type.INFO, Color.RED);
			//}
			
			//if (mc.thePlayer.ticksExisted < 5) {
				//for (Packet p : packets) {
					
					//mc.getNetHandler().getNetworkManager().sendPacketNoEvent(p);
					
				//}
				//packets.clear();
			//}
			
		}
		
		if (e instanceof EventReceivePacket && e.isPre()) {
			
			if (((EventReceivePacket)e).packet instanceof S08PacketPlayerPosLook) {
				//mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(((S08PacketPlayerPosLook)((EventReceivePacket)e).packet).getX(), ((S08PacketPlayerPosLook)((EventReceivePacket)e).packet).getY(), ((S08PacketPlayerPosLook)((EventReceivePacket)e).packet).getZ(), false));
				//e.setCanceled(true);
				//Minecraft.getMinecraft().thePlayer.setPosition(((S08PacketPlayerPosLook)((EventReceivePacket)e).packet).getX(), ((S08PacketPlayerPosLook)((EventReceivePacket)e).packet).getY(), ((S08PacketPlayerPosLook)((EventReceivePacket)e).packet).getZ());
				//packets.clear();
			}
			
		}
		
		if (e instanceof EventSendPacket && e.isPre()) {
			
			EventSendPacket event = (EventSendPacket) e;
			
			if (event.packet instanceof C0FPacketConfirmTransaction) {
	            packetsQueue.add(event.packet);
	            e.setCanceled(true);
			}
			else if (event.packet instanceof C00PacketKeepAlive) {
				packetsQueue.add(event.packet);
	            e.setCanceled(true);
			}
			
		}
		
	}
	
	private class packetDelay{
		
		public packetDelay(Packet packet, long sendAt) {
			this.packet = packet;
			this.sendAt = sendAt;
		}
		
		public final Packet packet;
		public final long sendAt;
	}
	
}