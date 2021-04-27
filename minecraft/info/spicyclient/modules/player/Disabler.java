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
import info.spicyclient.files.FileManager;
import info.spicyclient.modules.Module;
import info.spicyclient.notifications.Color;
import info.spicyclient.notifications.NotificationManager;
import info.spicyclient.notifications.Type;
import info.spicyclient.settings.NumberSetting;
import info.spicyclient.util.MovementUtils;
import info.spicyclient.util.Timer;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.login.server.S00PacketDisconnect;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.client.C0CPacketBoatInput;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.client.C13PacketPlayerAbilities;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S32PacketConfirmTransaction;
import net.minecraft.util.EnumFacing;

public class Disabler extends Module {
	public Disabler() {
		super("Disabler", Keyboard.KEY_NONE, Category.PLAYER);
		resetSettings();
	}
	
	@Override
	public void resetSettings() {
		this.settings.clear();
		double oldValue = addPing.getValue();
		addPing = new NumberSetting("Ping", oldValue, 20, 200, 1);
		this.addSettings(addPing);
	}
	
	public NumberSetting addPing = new NumberSetting("Ping", 55, 20, 200, 1);
	
	public static transient boolean watchdog = false;
	public static transient CopyOnWriteArrayList<Packet> packets = new CopyOnWriteArrayList<Packet>();
	public static transient Timer ping = new Timer();
	public static Thread pingThread = new Thread() {
		public void run() {
			int pingInt = 0;
			while (true) {
				
				try {
					if (ping.hasTimeElapsed(pingInt, true) && (mc.thePlayer == null || mc.thePlayer.ticksExisted >= 5)) {
						CopyOnWriteArrayList<Packet> packetsToRemove = new CopyOnWriteArrayList<Packet>();
						for (int i = 0; i < packets.size(); i++) {
							
							Packet p = packets.get(i);
							packetsToRemove.add(p);
							
							try {
								mc.getNetHandler().getNetworkManager().sendPacketNoEvent(p);
							} catch (Exception e) {
								
							}
							
							if (p instanceof C0FPacketConfirmTransaction) {
								C0FPacketConfirmTransaction f = (C0FPacketConfirmTransaction)p;
								//Command.sendPrivateChatMessage(f.getUid());
								//Command.sendPrivateChatMessage(f.isAccepted());
							}
							
						}
						if (packets.size() != 0) {
							//Command.sendPrivateChatMessage(pingInt);
							//Command.sendPrivateChatMessage(packets.size());
							
							if (SpicyClient.config.disabler.addPing == null) {
								SpicyClient.config.disabler.addPing = new NumberSetting("Ping", 75, 75, 200, 1);
								FileManager.save_config(SpicyClient.config.name);
							}
							
							pingInt = (int) SpicyClient.config.disabler.addPing.getValue();
						}
						
						for (Packet remove : packetsToRemove) {
							if (packets.contains(remove)) {
								packets.remove(remove);
							}
						}
						packetsToRemove.clear();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
		}
	};
	
	static {
		pingThread.start();
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
			
			this.additionalInformation = "Hypixel furry edition " + SpicyClient.hud.separator + " " + addPing.getValue();
			
			//mc.timer.ticksPerSecond = 16;
			
			if (SpicyClient.config.pingSpoof.isEnabled()) {
				SpicyClient.config.pingSpoof.toggle();
				NotificationManager.getNotificationManager().createNotification("Disabler", "Pingspoof was disabled to prevent flags", true, 2000, Type.INFO, Color.RED);
			}
			
			if (mc.thePlayer.ticksExisted < 5) {
				for (Packet p : packets) {
					
					mc.getNetHandler().getNetworkManager().sendPacketNoEvent(p);
					if (p instanceof C0FPacketConfirmTransaction) {
						C0FPacketConfirmTransaction f = (C0FPacketConfirmTransaction)p;
					}
					
				}
				packets.clear();
			}
			
			/*
			if (mc.thePlayer.ticksExisted < 5) {
				for (Packet p : packets) {
					
					//mc.getNetHandler().getNetworkManager().sendPacketNoEvent(p);
					if (p instanceof C0FPacketConfirmTransaction) {
						C0FPacketConfirmTransaction f = (C0FPacketConfirmTransaction)p;
					}
					
				}
				//packets.clear();
			}
			
			if (ping.hasTimeElapsed(500, true)) {
				for (Packet p : packets) {
					
					mc.getNetHandler().getNetworkManager().sendPacketNoEvent(p);
					if (p instanceof C0FPacketConfirmTransaction) {
						C0FPacketConfirmTransaction f = (C0FPacketConfirmTransaction)p;
						//Command.sendPrivateChatMessage(f.getUid());
						//Command.sendPrivateChatMessage(f.isAccepted());
					}
					
				}
				if (packets.size() != 0) {
					Command.sendPrivateChatMessage("I like secks");
				}
				packets.clear();
			}
			*/
			
			if (mc.thePlayer.ticksExisted % 20 == 0) {
				//packets.add(new C13PacketPlayerAbilities(mc.thePlayer.capabilities));
				//mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C13PacketPlayerAbilities(mc.thePlayer.capabilities));
				//Command.sendPrivateChatMessage("Disabler: Sent a thing");
			}
			
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
			
			if (!e.isCanceled()) {
	            packets.add(event.packet);
	            e.setCanceled(true);
			}
			
			try {
				if (event.packet instanceof C17PacketCustomPayload) {
					//packets.add(event.packet);
		            //e.setCanceled(true);
					C17PacketCustomPayload customPayload = (C17PacketCustomPayload)event.packet;
					if (customPayload.getChannelName().contains("MC|Brand")) {
						customPayload.setData(new PacketBuffer(Unpooled.buffer()).writeString("vanilla"));
						Command.sendPrivateChatMessage("Spoofed the client brand as vanilla");
					}
				}
			} catch (Exception e2) {
				Command.sendPrivateChatMessage("Failed to spoof client brand");
				e2.printStackTrace();
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