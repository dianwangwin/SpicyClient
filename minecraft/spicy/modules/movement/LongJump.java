package spicy.modules.movement;

import org.lwjgl.input.Keyboard;

import net.minecraft.util.MathHelper;
import spicy.SpicyClient;
import spicy.events.Event;
import spicy.events.listeners.EventMotion;
import spicy.events.listeners.EventUpdate;
import spicy.modules.Module;
import spicy.modules.Module.Category;
import spicy.util.Timer;

public class LongJump extends Module {
	
	public LongJump() {
		super("Long Jump", Keyboard.KEY_NONE, Category.BETA);
	}

	
	public void onEnable() {
		
		if (!SpicyClient.config.blink.isEnabled()) {
			SpicyClient.config.blink.toggle();
		}
		
	}
	
	public void onDisable() {
		
		mc.thePlayer.motionX = 0;
		mc.thePlayer.motionZ = 0;
		
		if (SpicyClient.config.blink.isEnabled()) {
			SpicyClient.config.blink.toggle();
		}
		
	}
	
	public static boolean jumped = false;
	public static Timer timer = new Timer();
	
	public void onEvent(Event e) {
		
		if (e instanceof EventMotion) {
			
			if (e.isPost()) {
				
				if (jumped && mc.thePlayer.onGround) {
					this.toggle();
					jumped = false;
					return;
				}
				
				if (mc.thePlayer.onGround && mc.gameSettings.keyBindForward.pressed) {
					jumped = true;
					mc.thePlayer.setSprinting(true);
					mc.gameSettings.keyBindJump.pressed = false;
		            mc.thePlayer.jump();
		            mc.thePlayer.jump();
		            mc.thePlayer.jump();
		            mc.thePlayer.setSprinting(true);
		            //mc.thePlayer.motionY = 0.1f;
				}
				else if (mc.gameSettings.keyBindForward.pressed) {
		            float f = mc.thePlayer.rotationYaw * 0.017453292F;
		            mc.thePlayer.motionX -= (double)(MathHelper.sin(f) * 0.02F);
		            mc.thePlayer.motionZ += (double)(MathHelper.cos(f) * 0.02F);
				}
				
			}
			
		}
		
	}
	
	
}
