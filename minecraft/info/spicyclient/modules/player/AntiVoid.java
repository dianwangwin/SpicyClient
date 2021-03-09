package info.spicyclient.modules.player;

import java.util.Random;

import org.apache.commons.lang3.StringUtils;
import org.lwjgl.input.Keyboard;

import info.spicyclient.SpicyClient;
import info.spicyclient.chatCommands.Command;
import info.spicyclient.events.Event;
import info.spicyclient.events.listeners.EventChatmessage;
import info.spicyclient.events.listeners.EventGetBlockHitbox;
import info.spicyclient.events.listeners.EventMotion;
import info.spicyclient.events.listeners.EventPacket;
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
import net.minecraft.network.play.client.C03PacketPlayer;
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
		this.addSettings(mode, jumpFirst);
	}
	
	public void onEnable() {
		
	}
	
	public void onDisable() {
		mc.timer.ticksPerSecond = 20;
	}
	
	public static boolean bounced = false;
	
	public void onEvent(Event e) {
		
		if (e instanceof EventPacket) {
			
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
					if (mode.is("Hypixel") && ((EventPacket) e).packet instanceof S08PacketPlayerPosLook && !MovementUtils.isOnGround(0.001) && (mc.thePlayer.fallDistance >= 20.0f || mc.thePlayer.posY < 0) && isOverVoid && mc.thePlayer.posY > mc.thePlayer.lastTickPosY) {
						
						mc.timer.ticksPerSecond = 20;
						mc.thePlayer.fallDistance = 0;
						NotificationManager.getNotificationManager().createNotification("Antivoid saved you", "", true, 2000, Type.INFO, Color.BLUE);
						
					}
				} catch (NullPointerException e2) {
					
				}
				
			}
			
		}
		
		if (e instanceof EventUpdate) {
			
			if (e.isPre()) {
				
				if (mode.is("Hypixel") && !SpicyClient.config.fly.isEnabled()) {
					
					this.additionalInformation = "Hypixel";
					
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
					
			        if (!MovementUtils.isOnGround(0.001) && (mc.thePlayer.fallDistance >= 20.0f || mc.thePlayer.posY < 0) && isOverVoid) {
			        	
			        	if (!bounced) {
			        		bounced = true;
			        		
			        		if (jumpFirst.isEnabled()) {
				        		mc.thePlayer.motionY = 0.2d*11d;
				        		mc.thePlayer.fallDistance = 4;
				        		mc.thePlayer.onGround = false;
				        		//Command.sendPrivateChatMessage(mc.thePlayer.motionY);
			        		}
			        		
			        	}else {
			        		
			        		Random r = new Random();
			        		
			        		//mc.thePlayer.fallDistance = -1;
			        		mc.thePlayer.motionY = 0;
			        		MovementUtils.setMotion(0);
			        		
			        	}
			        	
			        }else if (!isOverVoid && mc.thePlayer.fallDistance <= 2) {
			        	bounced = false;
			        }
					
				}
				
			}
			
		}
		
	}
	
}
