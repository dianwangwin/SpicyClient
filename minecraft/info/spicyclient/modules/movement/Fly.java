package info.spicyclient.modules.movement;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;

import org.lwjgl.input.Keyboard;

import com.ibm.icu.math.BigDecimal;

import info.spicyclient.SpicyClient;
import info.spicyclient.chatCommands.Command;
import info.spicyclient.events.Event;
import info.spicyclient.events.listeners.EventMotion;
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
import info.spicyclient.util.RotationUtils;
import info.spicyclient.util.Timer;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.network.Packet;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.login.client.C00PacketLoginStart;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition;
import net.minecraft.network.play.client.C03PacketPlayer.C06PacketPlayerPosLook;
import net.minecraft.network.play.client.C13PacketPlayerAbilities;
import net.minecraft.util.MathHelper;

public class Fly extends Module {

	public NumberSetting speed = new NumberSetting("Speed", 0.1, 0.01, 2, 0.1);
	private ModeSetting mode = new ModeSetting("Mode", "Vanilla", "Vanilla", "Hypixel");
	
	public BooleanSetting hypixelBlink = new BooleanSetting("Blink", true);
	public BooleanSetting hypixelTimerBoost = new BooleanSetting("Hypixel timer boost", true);
	public NumberSetting hypixelSpeed = new NumberSetting("Speed", 0.18, 0.05, 0.2, 0.005);
	
	public static ArrayList<Packet> hypixelPackets = new ArrayList<Packet>();
	
	public Fly() {
		super("Fly", Keyboard.KEY_NONE, Category.MOVEMENT);
		resetSettings();
	}

	@Override
	public void resetSettings() {
		this.settings.clear();
		this.addSettings(speed, mode, hypixelTimerBoost, hypixelSpeed);
	}
	
	@Override
	public void onSettingChange(SettingChangeEvent e) {
		
		if (e.setting.getSetting() == mode) {
			
			if (mode.is("Hypixel") || mode.getMode() == "Hypixel") {
				
				if (this.settings.contains(speed)) {
					this.settings.remove(speed);
				}
				
				if (!this.settings.contains(hypixelBlink)) {
					this.settings.add(hypixelBlink);
				}
				
				if (!this.settings.contains(hypixelTimerBoost)) {
					this.settings.add(hypixelTimerBoost);
				}
				
				if (!this.settings.contains(hypixelSpeed)) {
					this.settings.add(hypixelSpeed);
				}
				reorderSettings();
			}
			else {
				
				if (!this.settings.contains(speed)) {
					this.settings.add(speed);
				}
				
				if (this.settings.contains(hypixelBlink)) {
					this.settings.remove(hypixelBlink);
				}
				
				if (this.settings.contains(hypixelTimerBoost)) {
					this.settings.remove(hypixelTimerBoost);
				}
				
				if (this.settings.contains(hypixelSpeed)) {
					this.settings.remove(hypixelSpeed);
				}
				reorderSettings();
			}
			
		}
		
	}
	
	public static int fly_keybind = Keyboard.KEY_F;

	public static transient int hypixelStage = 0;
	public static transient boolean hypixelDamaged = false;
	public static transient float lastPlayerHealth;
	
	public void onEnable() {
		hypixelDamaged = false;
		if (mode.getMode().equals("Vanilla")) {
			original_fly_speed = mc.thePlayer.capabilities.getFlySpeed();
		} else if (mode.getMode().equals("Hypixel")) {
			
			if (mc.isSingleplayer()) {
				//Command.sendPrivateChatMessage("You cannot use hypixel fly in singleplayer!");
				NotificationManager.getNotificationManager().createNotification("Don't use hypixel fly in singleplayer!", "", true, 5000, Type.WARNING, Color.RED);
				this.toggle();
			}
			
			if (SpicyClient.config.bhop.isEnabled()) {
				SpicyClient.config.bhop.toggle();
				NotificationManager.getNotificationManager().createNotification("Don't use bhop while fly is enabled!", "", true, 5000, Type.WARNING, Color.RED);
			}
			
			hypixelStartTime = (long) (System.currentTimeMillis());
			
			if (!SpicyClient.config.blink.isEnabled()) {
				//SpicyClient.config.blink.toggle();
			}
			//damage();
			if (MovementUtils.isOnGround(0.0001)) {
				mc.thePlayer.jump();
			}
			
			mc.thePlayer.stepHeight = 0;
			
			if (!hypixelBlink.isEnabled()) {
				
				lastPlayerHealth = mc.thePlayer.getHealth();
				damage();
				
	            PlayerCapabilities playerCapabilities = new PlayerCapabilities();
	            playerCapabilities.isFlying = true;
	            playerCapabilities.allowFlying = true;
	            //playerCapabilities.setFlySpeed((float) ((Math.random() * (9.0 - 0.1)) + 0.1));
	            playerCapabilities.setFlySpeed((float) ((Math.random() * (9.0 - 0.1)) + 0.1));
	            playerCapabilities.isCreativeMode = new Random().nextBoolean();
	            mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C13PacketPlayerAbilities(playerCapabilities));
				
			}else {
				hypixelDamaged = true;
			}
			
		}
	}
	
	public void onDisable() {
		hypixelDamaged = false;
		mc.thePlayer.stepHeight = 0.6f;
		
		if (mode.getMode().equals("Vanilla")) {
			mc.thePlayer.capabilities.setFlySpeed(original_fly_speed);
			mc.thePlayer.capabilities.isFlying = false;
			mc.thePlayer.capabilities.allowFlying = false;
		} else if (mode.getMode().equals("Hypixel")) {
			
			if (MovementUtils.isOnGround(0.0001)) {
				mc.thePlayer.jump();
			}
			
			for (Packet p : hypixelPackets) {
				
				if (mc.isSingleplayer()) {
					
				}else {
					mc.getNetHandler().addToSendQueue(p);
				}
				
			}
			hypixelPackets.clear();
			
			if (SpicyClient.config.blink.isEnabled()) {
				//SpicyClient.config.blink.toggle();
			}

			mc.timer.ticksPerSecond = 20f;

		}

	}
	
	private static float original_fly_speed;
	private static int viewBobbing = 0;

	private transient long hypixelStartTime = System.currentTimeMillis();

	private transient Timer timer = new Timer();

	public void onEvent(Event e) {
		
		if (e instanceof EventUpdate && e.isPre()) {
			
			this.additionalInformation = mode.getMode() + " : " + (hypixelBlink.isEnabled() ? "Blink" : "Non Blink");
			
		}
		
		// For the viewbobbing
		if (e instanceof EventMotion && e.isPre() && (mc.gameSettings.keyBindForward.isKeyDown() || mc.gameSettings.keyBindBack.isKeyDown() || mc.gameSettings.keyBindLeft.isKeyDown() || mc.gameSettings.keyBindRight.isKeyDown())) {
			
			switch (viewBobbing) {
			case 0:
				mc.thePlayer.cameraYaw = 0.105F;
				mc.thePlayer.cameraPitch = 0.105F;
				viewBobbing++;
				break;
			case 1:
				viewBobbing++;
				break;
			case 2:
				viewBobbing = 0;
				break;
			}
			
		}
		
		// For the viewbobbing
		
		if (e instanceof EventSendPacket && mode.getMode().equals("Hypixel") && hypixelDamaged) {
			
			if (e.isPre()) {
				
				if (((EventSendPacket)e).packet instanceof C00PacketKeepAlive || ((EventSendPacket)e).packet instanceof C00Handshake || ((EventSendPacket)e).packet instanceof C00PacketLoginStart) {
					return;
				}
				
				EventSendPacket sendPacket = (EventSendPacket) e;
				
				if (sendPacket.packet instanceof C03PacketPlayer) {
					((C03PacketPlayer)sendPacket.packet).setIsOnGround(false);
					((C03PacketPlayer)sendPacket.packet).setMoving(false);
				}
				
				if (hypixelBlink.isEnabled()) {
					hypixelPackets.add(sendPacket.packet);
					sendPacket.setCanceled(true);
				}
				
			}
			
		}
		
		if (e instanceof EventUpdate && e.isPre()) {

			if (timer.hasTimeElapsed(2000 + new Random().nextInt(500), true)) {
				// SpicyClient.config.blink.toggle();
			}

		}
		
		if (mode.getMode().equals("Vanilla")) {
			try {
				mc.thePlayer.capabilities.setFlySpeed(original_fly_speed);
			} catch (NullPointerException e2) {
				e2.printStackTrace();
			}
			try {
				mc.thePlayer.capabilities.isFlying = false;
			} catch (NullPointerException e2) {
				e2.printStackTrace();
			}
			try {
				mc.thePlayer.capabilities.allowFlying = false;
			} catch (NullPointerException e2) {
				e2.printStackTrace();
			}
		}

		if (mode.getMode().equals("Vanilla")) {
			mc.thePlayer.capabilities.isFlying = true;
			mc.thePlayer.capabilities.setFlySpeed((float) speed.getValue());
		}
		
		if (e instanceof EventMotion) {

			EventMotion event = (EventMotion) e;

			if (e.isPost()) {
				
				//this.additionalInformation = mode.getMode();
				
				if (lastPlayerHealth > mc.thePlayer.getHealth()) {
					hypixelDamaged = true;
				}
				
				if (mode.getMode().equals("Hypixel") && hypixelDamaged) {
					
					//mc.thePlayer.capabilities.isFlying = true;
					
					mc.thePlayer.onGround = true;
					mc.thePlayer.motionY = 0.0;
					
					//MovementUtils.setMotion(0.2);
					//MovementUtils.strafe(0.195f);
					MovementUtils.setMotion(((float)hypixelSpeed.getValue()));
					
					//int time = (int) ((System.currentTimeMillis() - hypixelStartTime) / 1000);
					
					if (hypixelTimerBoost.isEnabled()) {
						mc.timer.ticksPerSecond = 27f;
					}
					
					//double offset = 9.947598300641403E-14D;
					//double offset = 9.947599900641403E-14D;
					//double offset = 9.274936900641403E-14D;
					double offset1 = 0.00000000824934 / 4;
					double offset2 = 0.002248000625918 / 6;
					double offset3 = 9.274936900641403E-12D;
					
					offset1 += ((float)new Random().nextInt(99999)) / 10000000000000000f; 
					offset2 += ((float)new Random().nextInt(99999)) / 10000000000000000f; 
					//Command.sendPrivateChatMessage(new DecimalFormat("#.####################################################").format(offset2));
					
					switch (hypixelStage) {
					case 0:
						event.setY(mc.thePlayer.posY);
						//mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
						mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0,
								mc.thePlayer.posZ);
						hypixelStage++;
						break;
					case 1:
						// mc.thePlayer.posY = mc.thePlayer.posY + 9.947598300641403E-14;
						// mc.thePlayer.posY = mc.thePlayer.lastTickPosY + 0.0002000000000066393;
						//mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.0002000000000066393,
								//mc.thePlayer.posZ);
						if (!MovementUtils.isOnGround(0.0001)) {
							mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + -offset2, mc.thePlayer.posZ);
						}
						
						//event.setY(mc.thePlayer.posY);
						hypixelStage++;
						break;
					case 2:
						event.setY(mc.thePlayer.posY);
						//mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
						mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + (offset2),
								mc.thePlayer.posZ);
						hypixelStage = 0;
						break;
					}
					//Command.sendPrivateChatMessage(mc.thePlayer.posY);
					DecimalFormat dec = new DecimalFormat("#.##########################################");
					//Command.sendPrivateChatMessage(dec.format(offset));
					
				}

			}

		}

	}
	
	// Found on github
	
    //Damage method. It can only take 1 heart of damage.
    //2020/2/3 Jump Potion supported.
    public void damage(){
    	
    	for (int i = 0; i < 10; i++) {
            //Imagine flagging to NCP.
            mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, true));
        }

        float fallDistance = 3.0125f; //does half a heart of damage
    	//float fallDistance = 8.0125f;

        while (fallDistance > 0) {
            mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.0624986421, mc.thePlayer.posZ, false));
            mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.0625, mc.thePlayer.posZ, false));
            mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.0624986421, mc.thePlayer.posZ, false));
            mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.0000013579, mc.thePlayer.posZ, false));
            fallDistance -= 0.0624986421f;
        }

        mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, true));

        //mc.thePlayer.jump();

        mc.thePlayer.posY += 0.42f;
    	
    	/*
        double fallDistance = 0;
        double offset = 0.41999998688698;
        while (fallDistance < 6)
        {
            sendPacket(offset,false);
            sendPacket(0, fallDistance + offset >= 4);
            fallDistance += offset;
        }
        */
        hypixelDamaged = true;
    }
    void sendPacket(double addY,boolean ground){
        mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(
                mc.thePlayer.posX,mc.thePlayer.posY+addY,mc.thePlayer.posZ,ground
        ));
    }
	
}
