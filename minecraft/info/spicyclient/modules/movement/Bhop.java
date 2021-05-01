package info.spicyclient.modules.movement;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang3.RandomUtils;
import org.lwjgl.input.Keyboard;

import info.spicyclient.SpicyClient;
import info.spicyclient.chatCommands.Command;
import info.spicyclient.events.Event;
import info.spicyclient.events.listeners.EventMotion;
import info.spicyclient.events.listeners.EventMove;
import info.spicyclient.events.listeners.EventOnLadder;
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
import info.spicyclient.settings.SettingChangeEvent;
import info.spicyclient.util.MovementUtils;
import info.spicyclient.util.Timer;
import info.spicyclient.util.WorldUtils;
import net.minecraft.block.BlockStairs;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.init.Blocks;
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
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import optifine.MathUtils;

public class Bhop extends Module {
	
	public ModeSetting mode = new ModeSetting("Mode", "Vanilla", "Vanilla", "PvpLands", "Hypixel", "NCP1", "Test", "Test 2", "Test 3");
	
	public NumberSetting hypixelSpeed = new NumberSetting("Speed", 0.01, 0.0001, 0.03, 0.0001);
	
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
		if (!mode.modes.contains("NCP1")) {
			mode.modes.add("NCP1");
		}
		this.addSettings(mode, hypixelSpeed);
	}
	
	@Override
	public void onSettingChange(SettingChangeEvent e) {
		if (e.setting.getSetting() == mode) {
			
			if (mode.is("Hypixel") || mode.getMode() == "Hypixel") {
				
				if (!this.settings.contains(hypixelSpeed)) {
					this.settings.add(hypixelSpeed);
				}
				reorderSettings();
				
			}else {
				
				if (this.settings.contains(hypixelSpeed)) {
					this.settings.remove(hypixelSpeed);
				}
				reorderSettings();
				
			}
			
		}
	}
	
	public void onEnable() {
		this.speed = 0.0D;
		if (SpicyClient.config.fly.isEnabled()) {
			toggle();
			NotificationManager.getNotificationManager().createNotification("Don't use bhop while fly is enabled!", "", true, 5000, Type.WARNING, Color.RED);
		}
		
		lastY = mc.thePlayer.posY;
		speed = hypixelSpeed.getValue() * 11;
		status = 0;
		boosted = false;
		
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
		
		if (e instanceof EventReceivePacket) {
			
			if (e.isPre()) {
				
				EventReceivePacket packetEvent = (EventReceivePacket) e;
				
				if (packetEvent.packet instanceof S08PacketPlayerPosLook) {
					
					if (lagbackCheck >= 1) {
						
						lagbackCheck = 0;
						lastLagback = System.currentTimeMillis() - (5*1000);
						this.toggle();
						NotificationManager.getNotificationManager().createNotification(this.name + " has been disabled to prevent flags", "", true, 1000, Type.WARNING, Color.RED);
						
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
				
				if (mode.getMode().equalsIgnoreCase("Pvplands") && !mc.thePlayer.isInWater()) {
					
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
				else if (mode.getMode().equalsIgnoreCase("Pvplands") && mc.thePlayer.isInWater()) {
					if (mc.thePlayer.onGround) {
						mc.gameSettings.keyBindJump.pressed = false;
						mc.thePlayer.jump();
						mc.thePlayer.setSprinting(true);
					}
				}
				else if (mode.is("Hypixel") && MovementUtils.isMoving() && (!SpicyClient.config.blockFly.isEnabled() || (SpicyClient.config.killaura.isEnabled() && SpicyClient.config.killaura.target != null) || true)) {
					
					mc.gameSettings.keyBindJump.pressed = false;
					//Command.sendPrivateChatMessage(Speed);
					if (MovementUtils.isOnGround(0.00000001)) {
						mc.thePlayer.jump();
						MovementUtils.strafe();
						Speed = (float) (100 + (hypixelSpeed.getValue() * 1000));
					}else {
						
						Speed -= 1;
						
						if (Speed <= 1) {
							Speed = 1;
						}
						
						MovementUtils.setMotion((MovementUtils.getBaseMoveSpeed() / 100) * Speed);
//						mc.timer.timerSpeed = 1.2f;
						
					}
					
				}
				else if (mode.is("Hypixel") && !MovementUtils.isMoving()) {
					MovementUtils.setMotion(0);
				}
				else if (mode.is("NCP1")) {
					
					mc.gameSettings.keyBindJump.pressed = false;
					
					if (!MovementUtils.isMoving()) {
						mc.timer.timerSpeed = 1f;
						return;
					}
					
					MovementUtils.strafe();
					
					if (MovementUtils.isOnGround(0.00000001)) {
						mc.thePlayer.jump();
					}
					
					if (!boosted) {
						boosted = true;
						new Thread("Bhop thread") {
							public void run() {
								Command.sendPrivateChatMessage("fast");
								mc.timer.timerSpeed = 1000000000f;
								try {
									Thread.sleep(10);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
								Command.sendPrivateChatMessage("slow");
								mc.timer.timerSpeed = 0.1f;
								try {
									Thread.sleep(20);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
								boosted = false;
							}
						}.start();
					}
					
					if (!MovementUtils.isMoving()) {
						MovementUtils.setMotion(0);
					}
					
				}
				else if (mode.is("Test") && MovementUtils.isMoving()) {
					
					mc.thePlayer.setSprinting(true);
					
			         double xDist = mc.thePlayer.posX - mc.thePlayer.prevPosX;
			         double zDist = mc.thePlayer.posZ - mc.thePlayer.prevPosZ;
			         double lastDistSonic = Math.sqrt(xDist * xDist + zDist * zDist);
			         MovementUtils.setMotion(MovementUtils.getBlocksPerSecond() - lastDistSonic);
					
				}
			}
			
		}
		
	}
	
	public static transient float Speed = 0;
	public static transient boolean lastDistanceReset = false;
	
	public static double roundToPlace(double value, int places) {
		if (places < 0) {
			throw new IllegalArgumentException();
		} else {
			BigDecimal bd = new BigDecimal(value);
			bd = bd.setScale(places, RoundingMode.HALF_UP);
			return bd.doubleValue();
		}
	}
    double forward;
    double strafe;
}
