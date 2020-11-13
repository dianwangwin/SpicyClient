package spicy.modules.movement;

import org.lwjgl.input.Keyboard;

import net.minecraft.util.MathHelper;
import spicy.SpicyClient;
import spicy.events.Event;
import spicy.events.listeners.EventMotion;
import spicy.events.listeners.EventUpdate;
import spicy.modules.Module;
import spicy.modules.Module.Category;
import spicy.util.MovementUtils;
import spicy.util.Timer;

public class LongJump extends Module {
	
	public LongJump() {
		super("Long Jump", Keyboard.KEY_NONE, Category.MOVEMENT);
	}

	
	public void onEnable() {
		
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
			
			if (e.isPost()) {
				
				if (jumped && mc.thePlayer.onGround) {
					this.toggle();
					jumped = false;
					return;
				}
				
				mc.gameSettings.keyBindJump.pressed = false;

                if (mc.thePlayer.onGround) {
                	
                	MovementUtils.strafe((float) Math.sqrt(mc.thePlayer.motionX * mc.thePlayer.motionX + mc.thePlayer.motionZ * mc.thePlayer.motionZ) + 1.25f);
                    mc.thePlayer.jump();
                    e.setCanceled(true);
                    jumped = true;

                }

                mc.thePlayer.setSprinting(true);
				
			}
			
		}
		
	}
	
	
}
