package info.spicyclient.bypass;

import java.text.DecimalFormat;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

import info.spicyclient.SpicyClient;
import info.spicyclient.chatCommands.Command;
import info.spicyclient.events.Event;
import info.spicyclient.events.listeners.EventPacket;
import info.spicyclient.events.listeners.EventSendPacket;
import info.spicyclient.events.listeners.EventSneaking;
import info.spicyclient.events.listeners.EventUpdate;
import info.spicyclient.modules.Module;
import info.spicyclient.notifications.Color;
import info.spicyclient.notifications.NotificationManager;
import info.spicyclient.notifications.Type;
import info.spicyclient.util.MovementUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.network.Packet;
import net.minecraft.network.login.server.S00PacketDisconnect;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.client.C13PacketPlayerAbilities;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.util.MathHelper;

public class Hypixel {
	
	public static void damageHypixel(double damage) {
		
		Minecraft mc = Minecraft.getMinecraft();
		
		if (damage > MathHelper.floor_double(mc.thePlayer.getMaxHealth()))
			damage = MathHelper.floor_double(mc.thePlayer.getMaxHealth());

		double offset = 0.0625;
		//offset = 0.015625;
		if (mc.thePlayer != null && mc.getNetHandler() != null && mc.thePlayer.onGround) {
			for (int i = 0; i <= ((3 + damage) / offset); i++) {
				mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX,
						mc.thePlayer.posY + ((offset / 2) * 1), mc.thePlayer.posZ, false));
				mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX,
						mc.thePlayer.posY + ((offset / 2) * 2), mc.thePlayer.posZ, false));
				mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX,
						mc.thePlayer.posY, mc.thePlayer.posZ, (i == ((3 + damage) / offset))));
				//mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C06PacketPlayerPosLook(mc.thePlayer.posX,
						//mc.thePlayer.posY, mc.thePlayer.posZ, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch, (i == ((3 + damage) / offset))));
			}
		}
		
	}
	
	public static transient boolean disabled = false, watchdog = false, shouldCancelPackets = false;
	public static transient double originalX, originalY, originalZ, originalMotionX, originalMotionY, originalMotionZ;
	public static transient int status = 0;
	public static transient CopyOnWriteArrayList<Packet> packets = new CopyOnWriteArrayList<Packet>();
	
	public static void onFlyEnable() {
		
		disabled = false;
		watchdog = false;
		shouldCancelPackets = false;
		packets.clear();
		status = 0;
		originalX = Minecraft.getMinecraft().thePlayer.posX;
		originalY = Minecraft.getMinecraft().thePlayer.posY;
		originalZ = Minecraft.getMinecraft().thePlayer.posZ;
		originalMotionX = Minecraft.getMinecraft().thePlayer.motionX;
		originalMotionY = Minecraft.getMinecraft().thePlayer.motionY;
		originalMotionZ = Minecraft.getMinecraft().thePlayer.motionZ;
		
	}
	
	public static void onFlyDisable() {
		
		if (!disabled) {
			Minecraft.getMinecraft().thePlayer.setPosition(originalX, originalY, originalZ);
			Minecraft.getMinecraft().thePlayer.motionX = originalMotionX;
			Minecraft.getMinecraft().thePlayer.motionY = originalMotionY;
			Minecraft.getMinecraft().thePlayer.motionZ = originalMotionZ;
		}
		
	}
	
	public static void onFlyEvent(Event e, Module module, Minecraft mc) {
		
		if (e instanceof EventUpdate && e.isPre()) {
			module.additionalInformation = "Hypixel Freecam";
		}
		
		if (e instanceof EventUpdate && e.isPre() && shouldCancelPackets) {
			
			if (mc.gameSettings.keyBindJump.isKeyDown()) {
				mc.thePlayer.motionY += SpicyClient.config.fly.hypixelFreecamVerticalFlySpeed.getValue();
			}			
			else if (mc.gameSettings.keyBindSneak.isKeyDown()) {
				mc.thePlayer.motionY -= SpicyClient.config.fly.hypixelFreecamVerticalFlySpeed.getValue();
			}
			else {
				mc.thePlayer.motionY = 0;
			}
			
			mc.thePlayer.onGround = true;
			
			MovementUtils.setMotion(SpicyClient.config.fly.hypixelFreecamHorizontalFlySpeed.getValue());
			
		}
		
		if (e instanceof EventSneaking && e.isPre()) {
			((EventSneaking)e).sneaking = false;
		}
		
		if (e instanceof EventUpdate && e.isPre() && disabled && packets.size() > 0) {
			
			double tpX = originalX, tpY = originalY, tpZ = originalZ;
			
			for (Packet p : packets) {
				mc.getNetHandler().getNetworkManager().sendPacketNoEvent(p);
				if (p instanceof C03PacketPlayer) {
					tpX = ((C03PacketPlayer)p).getPositionX();
					tpY = ((C03PacketPlayer)p).getPositionY();
					tpZ = ((C03PacketPlayer)p).getPositionZ();
				}
			}
			
			packets.clear();
			
			Minecraft.getMinecraft().thePlayer.setPosition(tpX, tpY, tpZ);
			
		}
		
		if (e instanceof EventSendPacket && e.isPre() && !disabled) {
			
            if (e.isPre()) {
            	
                if (((EventSendPacket)e).packet instanceof C03PacketPlayer) {
                    if (watchdog && shouldCancelPackets) {
                    	packets.add(((EventSendPacket)e).packet);
                        e.setCanceled(true);
                    }
                    
                }
			
            }
            
		}
		
		if (e instanceof EventPacket && e.isPre() && !disabled) {
			
            if (e.isPre()) {
            	
                if (((EventPacket)e).packet instanceof S08PacketPlayerPosLook) {
                	
                    if (watchdog) {
                        //toggle();
                    	disabled = true;
                        NotificationManager.getNotificationManager().createNotification("Fly", "Teleporting you to your current position", true, 5000, Type.INFO, Color.PINK);
                        //mc.thePlayer.motionY += 1;
                        //SpicyClient.config.fly.toggle();
                    }
                    
                }
			
            }
            
		}
		
		if (e instanceof EventUpdate && e.isPre() && !disabled) {
			
			if (!watchdog) {
                if (MovementUtils.isOnGround(0.001) && mc.thePlayer.isCollidedVertically) {
                    double x = mc.thePlayer.posX;
                    double y = mc.thePlayer.posY;
                    double z = mc.thePlayer.posZ;
                    //mc.thePlayer.sendQueue.getNetworkManager().sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(x, y, z, true));
                    //mc.thePlayer.sendQueue.getNetworkManager().sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(x, y + 0.21D, z, true));
                    //mc.thePlayer.sendQueue.getNetworkManager().sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(x, y + 0.11D, z, true));
                    mc.thePlayer.motionY = 0.21;
                    watchdog = true;
                    NotificationManager.getNotificationManager().createNotification("Fly: Wait 5s.", "", true, 5000, Type.INFO, Color.PINK);
                    //mc.thePlayer.jump();
                }
            }
			else if (mc.thePlayer.motionY <= 0 && watchdog) {
				shouldCancelPackets = true;
			}
			else if (shouldCancelPackets) {
                //mc.thePlayer.motionX = 0;
                //mc.thePlayer.motionY = 0;
                //mc.thePlayer.motionZ = 0;
                //mc.thePlayer.jumpMovementFactor = 0;
                //mc.thePlayer.noClip = true;
                //mc.thePlayer.onGround = false;
            }
			
		}
		
	}
	
}
