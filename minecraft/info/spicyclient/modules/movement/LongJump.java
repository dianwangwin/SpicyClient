package info.spicyclient.modules.movement;

import org.lwjgl.input.Keyboard;

import info.spicyclient.SpicyClient;
import info.spicyclient.events.Event;
import info.spicyclient.events.listeners.EventMotion;
import info.spicyclient.events.listeners.EventUpdate;
import info.spicyclient.modules.Module;
import info.spicyclient.modules.Module.Category;
import info.spicyclient.util.MovementUtils;
import info.spicyclient.util.Timer;
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
		mc.thePlayer.jump();
	}
	
	public void onDisable() {
		
	}
	
	public static boolean jumped = false;
	public static Timer timer = new Timer();
	
	
	public void onEvent(Event e) {
		
		if (e instanceof EventUpdate && e.isPre()) {
			this.additionalInformation = "Redesky";
		}
		
		if (e instanceof EventMotion) {
			
			if (e.isPost()) {
				
				if (jumped) {
					this.toggle();
					jumped = false;
					return;
				}else {
                	MovementUtils.strafe(1.5f);
                    mc.thePlayer.motionY = 0.4;
                    e.setCanceled(true);
				}
				
				mc.gameSettings.keyBindJump.pressed = false;
				
            	if (timer.hasTimeElapsed(300, true) || MovementUtils.isOnGround(0.00000001)) {
            		jumped = true;
            	}
            	
                mc.thePlayer.setSprinting(true);
				
			}
			
		}
		
	}
	
	
}
