package info.spicyclient.modules.movement;

import org.lwjgl.input.Keyboard;

import info.spicyclient.SpicyClient;
import info.spicyclient.bypass.Hypixel;
import info.spicyclient.events.Event;
import info.spicyclient.events.listeners.EventMotion;
import info.spicyclient.events.listeners.EventUpdate;
import info.spicyclient.modules.Module;
import info.spicyclient.modules.Module.Category;
import info.spicyclient.util.MovementUtils;
import info.spicyclient.util.Timer;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.network.play.client.C13PacketPlayerAbilities;
import net.minecraft.util.MathHelper;

public class LongJump extends Module {
	
	public LongJump() {
		super("Long Jump", Keyboard.KEY_NONE, Category.MOVEMENT);
	}

	
	public void onEnable() {
		jumped = false;
		timer.reset();
	}
	
	public void onDisable() {
		
	}
	
	public static boolean jumped = false;
	public static Timer timer = new Timer();
	
	
	public void onEvent(Event e) {
		
		if (e instanceof EventUpdate && e.isPre()) {
			this.additionalInformation = "Hypixel";
		}
		
		if (e instanceof EventMotion) {
			
			if (!jumped && MovementUtils.isOnGround(0.00001)) {
				Hypixel.damageHypixel(2);
				jumped = true;
			}
			
			if (jumped && mc.thePlayer.hurtResistantTime == 19) {
				mc.thePlayer.motionY += 0.4;
				MovementUtils.setMotion(0.45);
			}
			else if (jumped) {
				MovementUtils.strafe();
			}
			
			if (jumped && mc.thePlayer.hurtResistantTime == 0 && MovementUtils.isOnGround(0.0001) && timer.hasTimeElapsed(1000, true)) {
				toggle();
			}
			
		}
		
	}
	
	
}
