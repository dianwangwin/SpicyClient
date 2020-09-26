package spicy.modules.movement;

import java.util.Random;

import org.lwjgl.input.Keyboard;

import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.MathHelper;
import spicy.SpicyClient;
import spicy.chatCommands.Command;
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
			if (mc.thePlayer.onGround) {
				if (SpicyClient.config.blink.isEnabled()) {
					
				}else {
					SpicyClient.config.blink.toggle();
				}
			}else {
				this.toggle();
				Command.sendPrivateChatMessage("You have to be standing on ground before you toggle fly");
			}
		}
	}
	
	public void onDisable() {
		
		if (mode.getMode().equals("Vanilla")) {
			mc.thePlayer.capabilities.setFlySpeed(original_fly_speed);
			mc.thePlayer.capabilities.isFlying = false;
			mc.thePlayer.capabilities.allowFlying = false;
		}
		else if (mode.getMode().equals("Hypixel")) {
			
			if (SpicyClient.config.blink.isEnabled()) {
				SpicyClient.config.blink.toggle();
			}
			
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
					
			        if (!mc.thePlayer.onGround && mc.thePlayer.fallDistance >= 2.7f) {
			        	
			        	Random r = new Random();
			        	//mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer(true));
			        	mc.thePlayer.motionY = -((r.nextInt(10)) / 100);
			            float f = mc.thePlayer.rotationYaw * 0.017453292F;
			            mc.thePlayer.motionX -= (double)(MathHelper.sin(f) * 0.035f);
			            mc.thePlayer.motionZ += (double)(MathHelper.cos(f) * 0.035f);

			        }
					
				}
				
			}
			
		}
		
	}
	
}
