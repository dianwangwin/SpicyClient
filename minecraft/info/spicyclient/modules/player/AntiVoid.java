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
import info.spicyclient.util.Data6d;
import info.spicyclient.util.MovementUtils;
import info.spicyclient.util.RandomUtils;
import info.spicyclient.util.ServerUtils;
import info.spicyclient.util.Timer;
import net.minecraft.block.BlockAir;
import net.minecraft.init.Blocks;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition;
import net.minecraft.network.play.client.C03PacketPlayer.C05PacketPlayerLook;
import net.minecraft.network.play.client.C03PacketPlayer.C06PacketPlayerPosLook;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;

public class AntiVoid extends Module {
	
	public BooleanSetting jumpFirst = new BooleanSetting("Jump first", false);
	private ModeSetting mode = new ModeSetting("Mode", "HypixelNew", "HypixelNew");
	
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
		lastOnground = null;
		antivoid = false;
		packets.clear();
		if (ServerUtils.isOnHypixel()) {
			NotificationManager.getNotificationManager().createNotification("Antivoid", "Antivoid does not bypass", true, 5000, Type.WARNING, Color.RED);
			//toggle();
		}
	}
	
	public void onDisable() {
		/*
		for (Packet p : packets) {
			mc.getNetHandler().getNetworkManager().sendPacket(p);
		}
		*/
		packets.clear();
	}
	
	private static transient CopyOnWriteArrayList<Packet> packets = new CopyOnWriteArrayList<Packet>();
	private static transient Data6d lastOnground = null;
	private static transient boolean antivoid = false, resumeCheckingAfterFall = false;
	private static transient Timer noSpam = new Timer();
	
	public void onEvent(Event e) {
		
		if (SpicyClient.config.fly.isEnabled()) {
			resumeCheckingAfterFall = SpicyClient.config.fly.isEnabled();
			lastOnground = null;
			return;
		}
		
		if (resumeCheckingAfterFall) {
			lastOnground = null;
			if (e instanceof EventUpdate && e.isPre()) {
				if (MovementUtils.isOnGround(0.0001)) {
					resumeCheckingAfterFall = false;
				}
			}
			return;
		}
		
//		if (ServerUtils.isOnHypixel()) {
//			NotificationManager.getNotificationManager().createNotification("Antivoid", "Antivoid does not bypass", true, 5000, Type.WARNING, Color.RED);
//			toggle();
//		}
		
		if (e instanceof EventUpdate && e.isPre()) {
			
			this.additionalInformation = mode.getMode();
			
			if (!isOverVoid()) {
				lastOnground = new Data6d(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, mc.thePlayer.motionX, mc.thePlayer.motionY, mc.thePlayer.motionZ);
				for (Packet p : packets) {
					mc.getNetHandler().getNetworkManager().sendPacketNoEvent(p);
				}
				packets.clear();
				antivoid = false;
			}else {
				
				if (mc.thePlayer.fallDistance >= 20 && antivoid && noSpam.hasTimeElapsed(2000, true)) {
					packets.clear();
					try {
						RandomUtils.setPosAndMotionWithData6d(lastOnground);
					} catch (Exception e2) {
						
					}
					antivoid = false;
					NotificationManager.getNotificationManager().createNotification("Antivoid", "Antivoid saved you", true, 5000, Type.INFO, Color.BLUE);
					resumeCheckingAfterFall = true;
				}
				
			}
			
		}
		else if (e instanceof EventSendPacket && e.isBeforePre()) {
			
			if (isOverVoid()) {
				packets.add(((EventSendPacket)e).packet);
				e.setCanceled(true);
				antivoid = true;
			}
			
		}
		
	}
	
	private boolean isOverVoid() {
		
		boolean isOverVoid = true;
		BlockPos block = mc.thePlayer.getPosition();
		
		for (double i = mc.thePlayer.posY + 1; i > 0; i -= 0.5) {
			
			if (isOverVoid) {
				
				try {
					if (mc.theWorld.getBlockState(block).getBlock() != Blocks.air) {
						
						isOverVoid = false;
						break;
						
					}
				} catch (Exception e) {
					
				}
				
			}
			
			block = block.add(0, -1, 0);
			
		}
		
		for (double i = 0; i < 10; i += 0.1) {
			if (MovementUtils.isOnGround(i) && isOverVoid) {
				isOverVoid = false;
				break;
			}
		}
		
		return isOverVoid;
	}
	
}
