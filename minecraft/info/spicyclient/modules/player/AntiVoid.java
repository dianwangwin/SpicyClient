package info.spicyclient.modules.player;

import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.lang3.StringUtils;
import org.lwjgl.input.Keyboard;

import info.spicyclient.SpicyClient;
import info.spicyclient.chatCommands.Command;
import info.spicyclient.events.Event;
import info.spicyclient.events.listeners.EventChatmessage;
import info.spicyclient.events.listeners.EventGetBlockHitbox;
import info.spicyclient.events.listeners.EventMotion;
import info.spicyclient.events.listeners.EventReceivePacket;
import info.spicyclient.events.listeners.EventSendPacket;
import info.spicyclient.events.listeners.EventUpdate;
import info.spicyclient.modules.Module;
import info.spicyclient.notifications.Color;
import info.spicyclient.notifications.NotificationManager;
import info.spicyclient.notifications.Type;
import info.spicyclient.settings.BooleanSetting;
import info.spicyclient.settings.ModeSetting;
import info.spicyclient.settings.NumberSetting;
import info.spicyclient.util.MovementUtils;
import info.spicyclient.util.Timer;
import net.minecraft.block.BlockAir;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition;
import net.minecraft.network.play.client.C03PacketPlayer.C05PacketPlayerLook;
import net.minecraft.network.play.client.C03PacketPlayer.C06PacketPlayerPosLook;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;

public class AntiVoid extends Module {
	
	public BooleanSetting jumpFirst = new BooleanSetting("Jump first", false);
	private ModeSetting mode = new ModeSetting("Mode", "Hypixel", "Hypixel");
	
	public AntiVoid() {
		super("Anti Void", Keyboard.KEY_NONE, Category.PLAYER);
		resetSettings();
	}
	
	@Override
	public void resetSettings() {
		this.settings.clear();
		this.addSettings(mode);
	}
	
	public void onEnable() {
		isWaiting = false;
		lastTeleport = 0;
		timeStarted = 0;
	}
	
	public void onDisable() {
		mc.timer.ticksPerSecond = 20;
	}
	
	public static boolean bounced = false;
	
	public void onEvent(Event e) {
		
		setLastGroundPos(e);
		
		if (e instanceof EventReceivePacket) {
			
			if (e.isPre()) {
				
				boolean isOverVoid = true;
				BlockPos block = mc.thePlayer.getPosition();
				
				for (int i = (int) mc.thePlayer.posY; i > 0; i--) {
					
					if (isOverVoid) {
						
						if (!(mc.theWorld.getBlockState(block).getBlock() instanceof BlockAir)) {
							
							isOverVoid = false;
							
						}
						
					}
					
					block = block.add(0, -1, 0);
					
				}
				
				try {
					if (mode.is("Hypixel") && ((EventReceivePacket) e).packet instanceof S08PacketPlayerPosLook && !MovementUtils.isOnGround(0.001) && (mc.thePlayer.fallDistance >= 20.0f || mc.thePlayer.posY < 0) && isOverVoid && mc.thePlayer.posY > mc.thePlayer.lastTickPosY) {
						
						mc.timer.ticksPerSecond = 20;
						mc.thePlayer.fallDistance = 0;
						NotificationManager.getNotificationManager().createNotification("Antivoid saved you", "", true, 2000, Type.INFO, Color.BLUE);
						
					}
				} catch (NullPointerException e2) {
					
				}
				
			}
			
		}
		
	}
	
	private boolean isOverVoid() {
		boolean isOverVoid = true;
		BlockPos block = mc.thePlayer.getPosition();
		
		for (int i = (int) mc.thePlayer.posY; i > 0; i--) {
			
			if (isOverVoid) {
				
				if (!(mc.theWorld.getBlockState(block).getBlock() instanceof BlockAir)) {
					
					isOverVoid = false;
					
				}
				
			}
			
			block = block.add(0, -1, 0);
			
		}
		return isOverVoid;
	}
	
	private static transient CopyOnWriteArrayList<Packet> packets = new CopyOnWriteArrayList<Packet>();
	private static transient BlockPos lastOnground = null;
	private static transient boolean isWaiting = false;
	private static transient long lastTeleport = 0, timeStarted = 0;
	
	private void setLastGroundPos(Event e) {
		
		if (SpicyClient.config.fly.isEnabled()) {
			return;
		}
		
		if (e instanceof EventUpdate && e.isPre()) {
			if (MovementUtils.isOnGround(0.0001)) {
				lastOnground = mc.thePlayer.getPosition();
				if (System.currentTimeMillis() >= lastTeleport) {
					packets.clear();
					isWaiting = false;
				}
			}else {
				if (isOverVoid() && mc.thePlayer.fallDistance >= 15) {
					mc.thePlayer.motionY = 0;
					MovementUtils.setMotion(0);
					//mc.thePlayer.fallDistance = -1;
					isWaiting = true;
				}
			}
		}
		else if (e instanceof EventSendPacket && e.isPre()) {
			if (((EventSendPacket)e).packet instanceof C03PacketPlayer) {
				if (isWaiting) {
					mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(lastOnground.getX(), lastOnground.getY(), lastOnground.getZ(), true));
					e.setCanceled(true);
				}
			}
		}
		else if (e instanceof EventReceivePacket && e.isPre()) {
			EventReceivePacket event = (EventReceivePacket)e;
			Packet packet = event.packet;
			if (packet instanceof S08PacketPlayerPosLook && isWaiting && System.currentTimeMillis() > lastTeleport) {
				e.setCanceled(true);
				S08PacketPlayerPosLook s08 = (S08PacketPlayerPosLook) packet;
				//mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(s08.getX(), s08.getY(), s08.getZ(), false));
				
				for (int i = packets.size(); i != 0; i--) {
					
					//mc.getNetHandler().getNetworkManager().sendPacketNoEvent(packets.get(i - 1));
					
				}
				
				//mc.thePlayer.setPosition(lastOnground.getX(), lastOnground.getY(), lastOnground.getZ());
				lastTeleport = System.currentTimeMillis() + 1000;
				
			}
		}
		
	}
	
}
