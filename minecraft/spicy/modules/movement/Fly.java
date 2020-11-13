package spicy.modules.movement;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;

import org.lwjgl.input.Keyboard;

import com.ibm.icu.math.BigDecimal;

import net.minecraft.network.Packet;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.login.client.C00PacketLoginStart;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition;
import net.minecraft.network.play.client.C03PacketPlayer.C06PacketPlayerPosLook;
import net.minecraft.util.MathHelper;
import spicy.SpicyClient;
import spicy.chatCommands.Command;
import spicy.events.Event;
import spicy.events.listeners.EventMotion;
import spicy.events.listeners.EventSendPacket;
import spicy.events.listeners.EventUpdate;
import spicy.modules.Module;
import spicy.settings.ModeSetting;
import spicy.settings.NumberSetting;
import spicy.util.MovementUtils;
import spicy.util.RotationUtils;
import spicy.util.Timer;

public class Fly extends Module {

	public NumberSetting speed = new NumberSetting("Speed", 0.1f, 0.01, 2, 0.1);
	private ModeSetting mode = new ModeSetting("Mode", "Vanilla", "Vanilla", "Hypixel");
	
	
	public static ArrayList<Packet> hypixelPackets = new ArrayList<Packet>();
	
	public Fly() {
		super("Fly", Keyboard.KEY_NONE, Category.MOVEMENT);
		resetSettings();
	}

	@Override
	public void resetSettings() {
		this.settings.clear();
		this.addSettings(speed, mode);
	}

	public static int fly_keybind = Keyboard.KEY_F;

	public static transient int hypixelStage = 0;
	public static transient boolean hypixelDamaged = false;
	public static transient float lastPlayerHealth;
	
	public void onEnable() {
		if (mode.getMode().equals("Vanilla")) {
			original_fly_speed = mc.thePlayer.capabilities.getFlySpeed();
		} else if (mode.getMode().equals("Hypixel")) {
			
			if (mc.isSingleplayer()) {
				Command.sendPrivateChatMessage("You cannot use hypixel fly in singleplayer!");
				this.toggle();
			}
			
			hypixelStartTime = (long) (System.currentTimeMillis() + (3 * 1000));
			
			if (!SpicyClient.config.blink.isEnabled()) {
				//SpicyClient.config.blink.toggle();
			}
			
			if (mc.thePlayer.onGround) {
				mc.thePlayer.jump();
			}
			
			mc.thePlayer.stepHeight = 0;
			
		}
	}

	public void onDisable() {
		
		mc.thePlayer.stepHeight = 0.6f;
		
		if (mode.getMode().equals("Vanilla")) {
			mc.thePlayer.capabilities.setFlySpeed(original_fly_speed);
			mc.thePlayer.capabilities.isFlying = false;
			mc.thePlayer.capabilities.allowFlying = false;
		} else if (mode.getMode().equals("Hypixel")) {
			
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
	private static int NCP_Status = 0;

	private transient long hypixelStartTime = System.currentTimeMillis() + (3 * 1000);

	private transient Timer timer = new Timer();

	public void onEvent(Event e) {
		
		if (e instanceof EventSendPacket && mode.getMode().equals("Hypixel")) {
			
			if (e.isPre()) {
				
				if (((EventSendPacket)e).packet instanceof C00PacketKeepAlive || ((EventSendPacket)e).packet instanceof C00Handshake || ((EventSendPacket)e).packet instanceof C00PacketLoginStart) {
					return;
				}
				
				EventSendPacket sendPacket = (EventSendPacket) e;
				
				if (sendPacket.packet instanceof C03PacketPlayer) {
					((C03PacketPlayer)sendPacket.packet).setIsOnGround(false);
				}
				
				hypixelPackets.add(sendPacket.packet);
				sendPacket.setCanceled(true);
				
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

				// double d = 9.94759830064103-14D;
				// DecimalFormat dec = new
				// DecimalFormat("0.00000000000000000000000000000000000000000000000");
				// 0.00000000000009947598300641403
				// System.out.println(dec.format(d) + "");

				this.additionalInformation = mode.getMode();

				if (mode.getMode().equals("Hypixel")) {

					mc.thePlayer.onGround = true;
					mc.thePlayer.motionY = 0;
					
					//MovementUtils.setMotion(0.2);
					MovementUtils.strafe(0.195f);
					
					
					int time = (int) ((System.currentTimeMillis() - hypixelStartTime) / 1000);
					// mc.thePlayer.motionX += (double)(MathHelper.sin(f) * 0.008 * time);
					// mc.thePlayer.motionZ -= (double)(MathHelper.cos(f) * 0.008 * time);
					// System.out.println(20 * time * -1);
					
					// mc.thePlayer.motionX += (double)(MathHelper.sin(f) * 0.008 * time);
					// mc.thePlayer.motionZ -= (double)(MathHelper.cos(f) * 0.008 * time);
					// System.out.println(20 * time * -1);
					
					mc.timer.ticksPerSecond = 27f;
					
					/*
					if (time < 0) {
						mc.timer.ticksPerSecond = 20f * time * -1;
						//mc.timer.ticksPerSecond = 21 * time * -1;
					} else {
						mc.timer.ticksPerSecond = 20f;
					}
					*/
					
					//double offset = 9.947598300641403E-14D;
					//double offset = 9.947599900641403E-14D;
					double offset = 9.947599900641403E-14D;
					
					switch (hypixelStage) {
					case 0:
						event.setY(mc.thePlayer.posY);
						mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
						hypixelStage++;
						break;
					case 1:
						// mc.thePlayer.posY = mc.thePlayer.posY + 9.947598300641403E-14;
						// mc.thePlayer.posY = mc.thePlayer.lastTickPosY + 0.0002000000000066393;
						//mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.0002000000000066393,
								//mc.thePlayer.posZ);
						mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + offset,
								mc.thePlayer.posZ);
						event.setY(mc.thePlayer.posY);
						hypixelStage++;
						break;
					case 2:
						// mc.thePlayer.posY = mc.thePlayer.posY + -9.947598300641403E-14;
						// mc.thePlayer.posY = mc.thePlayer.lastTickPosY -0.0002000000000066393;
						//mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + -0.0002000000000066393,
								//mc.thePlayer.posZ);
						mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + -offset,
								mc.thePlayer.posZ);
						event.setY(mc.thePlayer.posY);
						hypixelStage = 0;
						break;
					}
					
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
        double fallDistance = 0;
        double offset = 0.41999998688698;
        while (fallDistance < 4)
        {
            sendPacket(offset,false);
            sendPacket(0, fallDistance + offset >= 4);
            fallDistance += offset;
        }
    }
    void sendPacket(double addY,boolean ground){
        mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(
                mc.thePlayer.posX,mc.thePlayer.posY+addY,mc.thePlayer.posZ,ground
        ));
    }
	
}
