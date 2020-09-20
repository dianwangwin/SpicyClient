package spicy.modules.movement;

import org.lwjgl.input.Keyboard;

import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.MathHelper;
import spicy.events.Event;
import spicy.events.listeners.EventUpdate;
import spicy.modules.Module;
import spicy.settings.ModeSetting;
import spicy.settings.NumberSetting;

public class Fly extends Module {
	
	public NumberSetting speed = new NumberSetting("Speed", 0.1f, 0.01, 2, 0.1);
	private ModeSetting mode = new ModeSetting("Mode", "Vanilla", "Vanilla", "Hypixel");

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
	
	public void onEnable() {
		if (mode.getMode().equals("Vanilla")) {
			original_fly_speed = mc.thePlayer.capabilities.getFlySpeed();
		}
		else if (mode.getMode().equals("Hypixel")) {
			mc.timer.ticksPerSecond = 20f;
		}
	}
	
	public void onDisable() {
		if (mode.getMode().equals("Vanilla")) {
			mc.thePlayer.capabilities.setFlySpeed(original_fly_speed);
			mc.thePlayer.capabilities.isFlying = false;
			mc.thePlayer.capabilities.allowFlying = false;
		}
		else if (mode.getMode().equals("Hypixel")) {
			mc.timer.ticksPerSecond = 20f;
		}
	}
	
	private static float original_fly_speed;
	private static int NCP_Status = 0;
	
	public void onEvent(Event e) {
		
		if (!mode.getMode().equals("Vanilla")) {
			mc.thePlayer.capabilities.setFlySpeed(original_fly_speed);
			mc.thePlayer.capabilities.isFlying = false;
			mc.thePlayer.capabilities.allowFlying = false;
		}
		
		if (e instanceof EventUpdate) {
			
			EventUpdate event = (EventUpdate) e;
			
			if (e.isPre()) {
				
				this.additionalInformation = mode.getMode();
				
				if (mode.getMode().equals("Vanilla")) {
					mc.thePlayer.capabilities.isFlying = true;
					mc.thePlayer.capabilities.setFlySpeed((float) speed.getValue());
				}
				else if (mode.getMode().equals("Hypixel")) {
					
					// Big thank you to MintyCodes for showing this code in his video
					// Go like his video or subscribe
					// https://www.youtube.com/watch?v=lhEzBWNP3hE&list=WL&index=23
					
					double y, y1;
					mc.thePlayer.motionY = 0;
					
					if (mc.thePlayer.ticksExisted % 3 ==0) {
						
						y = mc.thePlayer.posY - 1.0E-10D;
						mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, y, mc.thePlayer.posZ, true));
						
					}
					
					y1 = mc.thePlayer.posY + 1.0E-10D;
					mc.thePlayer.setPosition(mc.thePlayer.posX, y1, mc.thePlayer.posZ);
					mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer(true));
					
				}
				
			}
			
		}
		
	}
	
}
