package spicy.modules.movement;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.network.EnumPacketDirection;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C02PacketUseEntity.Action;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition;
import net.minecraft.network.play.client.C03PacketPlayer.C06PacketPlayerPosLook;
import net.minecraft.network.play.client.C13PacketPlayerAbilities;
import net.minecraft.network.play.client.C14PacketTabComplete;
import net.minecraft.network.play.client.C15PacketClientSettings;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.network.play.client.C16PacketClientStatus.EnumState;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.network.play.client.C18PacketSpectate;
import net.minecraft.network.play.client.C19PacketResourcePackStatus;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.status.client.C00PacketServerQuery;
import net.minecraft.potion.Potion;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import spicy.SpicyClient;
import spicy.chatCommands.Command;
import spicy.events.Event;
import spicy.events.listeners.EventMotion;
import spicy.events.listeners.EventUpdate;
import spicy.events.listeners.EventOnLadder;
import spicy.events.listeners.EventPacket;
import spicy.events.listeners.EventSendPacket;
import spicy.modules.Module;
import spicy.settings.BooleanSetting;
import spicy.settings.ModeSetting;
import spicy.settings.NumberSetting;
import spicy.settings.SettingChangeEvent;
import spicy.util.MovementUtils;
import spicy.util.Timer;

public class Bhop extends Module {
	
	public ModeSetting mode = new ModeSetting("Mode", "Vanilla", "Vanilla", "PvpLands", "Hypixel", "Test", "Test 2", "Test 3");
	
	private static double lastY;
	private static float rotate = 180;
	
	private static int lagbackCheck = 0;
	private static long lastLagback = System.currentTimeMillis();
	
	private transient boolean toggle = true;
	
	public Bhop() {
		super("Bhop", Keyboard.KEY_NONE, Category.MOVEMENT);
		resetSettings();
	}
	
	@Override
	public void resetSettings() {
		this.settings.clear();
		this.addSettings(mode);
	}
	
	public void onEnable() {
		lastY = mc.thePlayer.posY;
	}
	
	public void onDisable() {
		mc.timer.ticksPerSecond = 20f;
		this.mc.timer.timerSpeed = 1.00f;
		status = 0;
	}
	
	private int status = 0;
	private boolean boosted = false;
	
    private double speed;
    private double lastDist;
    public static int stage;
    
    private transient Timer timer = new Timer();
    
	public void onEvent(Event e) {
		
		if (e instanceof EventPacket) {
			
			if (e.isPre()) {
				
				EventPacket packetEvent = (EventPacket) e;
				
				if (packetEvent.packet instanceof S08PacketPlayerPosLook) {
					
					if (lagbackCheck >= 3) {
						
						lagbackCheck = 0;
						lastLagback = System.currentTimeMillis() - (5*1000);
						this.toggle();
						Command.sendPrivateChatMessage(this.name + " has been disabled due to lagbacks");
						
					}else {
						
						lastLagback = System.currentTimeMillis();
						lagbackCheck++;
						
					}
					
				}
				
			}
			
		}
		
		if (e instanceof EventUpdate) {
			
			if (e.isPre()) {
				
				if (lastLagback + (5*1000) < System.currentTimeMillis()) {
					lagbackCheck = 0;
					lastLagback = System.currentTimeMillis() + (10*1000);
					
				}
				
			}
			
		}
		
		BlockFly b = (BlockFly) this.findModule(this.getModuleName(new BlockFly()));
		
		if (e instanceof EventUpdate) {
			if (e.isPre()) {
				
				this.additionalInformation = mode.getMode();
				
				if (mode.getMode().equalsIgnoreCase("Vanilla") && mc.gameSettings.keyBindForward.pressed) {
					if (mc.thePlayer.onGround) {
						mc.gameSettings.keyBindJump.pressed = false;
						mc.thePlayer.jump();
						mc.thePlayer.setSprinting(true);
					}
				}
			}
		}
		
		if (e instanceof EventMotion) {
			
			if (e.isBeforePost()) {
				
				EventMotion event = (EventMotion) e;
				if (b == null) {
					
				}
				else if (mode.getMode().equalsIgnoreCase("Pvplands") && !b.isEnabled() && !mc.thePlayer.isInWater()) {
					
					if (mc.thePlayer.onGround && mc.gameSettings.keyBindForward.pressed) {
						mc.thePlayer.setSprinting(true);
						mc.gameSettings.keyBindJump.pressed = false;
			            mc.thePlayer.jump();
			            mc.thePlayer.jump();
			            mc.thePlayer.setSprinting(true);
			            //mc.thePlayer.motionY = 0.1f;
					}
					else if (mc.gameSettings.keyBindForward.pressed) {
			            float f = mc.thePlayer.rotationYaw * 0.017453292F;
			            mc.thePlayer.motionX -= (double)(MathHelper.sin(f) * 0.035F);
			            mc.thePlayer.motionZ += (double)(MathHelper.cos(f) * 0.035F);
					}
					
				}
				else if (mode.getMode().equalsIgnoreCase("Pvplands") && !b.isEnabled() && mc.thePlayer.isInWater()) {
					if (mc.thePlayer.onGround) {
						mc.gameSettings.keyBindJump.pressed = false;
						mc.thePlayer.jump();
						mc.thePlayer.setSprinting(true);
					}
				}
				else if (mode.is("Hypixel") && !b.isEnabled() && !mc.thePlayer.isInWater() && (mc.gameSettings.keyBindForward.pressed || mc.gameSettings.keyBindBack.pressed || mc.gameSettings.keyBindLeft.pressed || mc.gameSettings.keyBindRight.pressed)) {

					if (mc.thePlayer.onGround) {
						
						mc.thePlayer.motionY = 0.399999f;
						e.setCanceled(true);
						
					}else {
						
						mc.timer.timerSpeed = 1.0f;
						float f = (float) MovementUtils.getDirection() + 180 - 45;
			            mc.thePlayer.motionX = (double)(MathHelper.sin(f) * 0.3F);
			            mc.thePlayer.motionZ = (double)(MathHelper.cos(f) * 0.3F) * -1;
			            
			            //mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + -0.0002000000000066393,
								//mc.thePlayer.posZ);
			            mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer(true));
			            
					}
					
				}
				else if (mode.is("Test") && !b.isEnabled() && !mc.thePlayer.isInWater() && (mc.gameSettings.keyBindForward.pressed || mc.gameSettings.keyBindBack.pressed || mc.gameSettings.keyBindLeft.pressed || mc.gameSettings.keyBindRight.pressed)) {	

					if (mc.thePlayer.onGround) {
						
						mc.thePlayer.jump();
						//mc.thePlayer.motionY = 0.42f;
						e.setCanceled(true);
						
					}else {
						
						//mc.timer.timerSpeed = 1.0f;
						//float f = (float) MovementUtils.getDirection() + 180 - 45;
			            //mc.thePlayer.motionX = (double)(MathHelper.sin(f) * 0.26F);
			            //mc.thePlayer.motionZ = (double)(MathHelper.cos(f) * 0.26F) * -1;
			            
			            //mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + -0.0002000000000066393,
								//mc.thePlayer.posZ);
			            //mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer(true));
			            
					}
					
					float f = (float) MovementUtils.getDirection() + 180 - 45;
		            mc.thePlayer.motionX = (double)(MathHelper.sin(f) * 0.35F);
		            mc.thePlayer.motionZ = (double)(MathHelper.cos(f) * 0.35F) * -1;
		            Command.sendPrivateChatMessage(mc.thePlayer.motionX + " - " + mc.thePlayer.motionZ);
		            
				}
				else if (mode.is("Test 3") && !mc.thePlayer.isInWater() && (mc.gameSettings.keyBindForward.pressed || mc.gameSettings.keyBindBack.pressed)) {
					
					if (mc.thePlayer.onGround) {
						
						mc.thePlayer.jump();
						//mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer(true));
						mc.thePlayer.motionY = 0.4f;
						
					}else {
						
						mc.timer.timerSpeed = 1f;
						//mc.thePlayer.moveStrafing *= 10;
						float f = (float) MovementUtils.getDirection() + 180 - 45;
			            mc.thePlayer.motionX += (double)(MathHelper.sin(f) * 0.006F);
			            mc.thePlayer.motionZ += (double)(MathHelper.cos(f) * 0.006F) * -1;
			            mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer());
			            double d = mc.thePlayer.motionX + mc.thePlayer.motionZ;
			            if (d < 0) {
			            	d *= -1;
			            }
			            //Command.sendPrivateChatMessage(d + "");
			             
			            //mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + -0.0002000000000066393,
								//mc.thePlayer.posZ);
			            mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer(true));
						//mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + -0.0002000000000066393,
								//mc.thePlayer.posZ);
			            
					}
					
					
					if (mc.thePlayer.fallDistance > 0.01 && mc.thePlayer.fallDistance < 3) {
						
					}
					
				}
				
			}
			
		}
		
	}
	
}
