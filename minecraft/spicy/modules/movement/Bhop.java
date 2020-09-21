package spicy.modules.movement;

import java.io.IOException;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.network.play.client.C13PacketPlayerAbilities;
import net.minecraft.potion.Potion;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import spicy.SpicyClient;
import spicy.events.Event;
import spicy.events.listeners.EventMotion;
import spicy.events.listeners.EventUpdate;
import spicy.events.listeners.EventOnLadder;
import spicy.modules.Module;
import spicy.settings.ModeSetting;

public class Bhop extends Module {
	
	public ModeSetting mode = new ModeSetting("Mode", "Vanilla", "Vanilla", "PvpLands", "Hypixel", "Test", "Test 2", "Test 3");
	
	private static double lastY;
	
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
	}
	
	private int status = 0;
	
	public void onEvent(Event e) {
		
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
			
			if (e.isPost()) {
				
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
			            mc.thePlayer.motionX -= (double)(MathHelper.sin(f) * 0.03F);
			            mc.thePlayer.motionZ += (double)(MathHelper.cos(f) * 0.03F);
					}
					
				}
				else if (mode.getMode().equalsIgnoreCase("Pvplands") && !b.isEnabled() && mc.thePlayer.isInWater()) {
					if (mc.thePlayer.onGround) {
						mc.gameSettings.keyBindJump.pressed = false;
						mc.thePlayer.jump();
						mc.thePlayer.setSprinting(true);
					}
				}
				else if (mode.getMode().equalsIgnoreCase("Hypixel") && !b.isEnabled()) {
					
					mc.thePlayer.setSprinting(true);
					
					if (mc.gameSettings.keyBindForward.pressed) {
						
						mc.gameSettings.keyBindJump.pressed = false;
						
						mc.thePlayer.setSprinting(true);
						
						float f = mc.thePlayer.rotationYaw * 0.017453292F;
			            //mc.thePlayer.motionX -= (double)(MathHelper.sin(f) * 0.007F);
			            //mc.thePlayer.motionZ += (double)(MathHelper.cos(f) * 0.007F);
			            
			            mc.thePlayer.setSprinting(true); 
			            
			            if (SpicyClient.config.timer.toggled) {
			            	mc.timer.ticksPerSecond = (float) (SpicyClient.config.timer.tps.getValue() + 2f);
			            }else {
			            	mc.timer.ticksPerSecond = 22f;
			            }
			            
			            if (event.onGround) {
				            mc.thePlayer.motionX -= (double)(MathHelper.sin(f) * 0.07F);
				            mc.thePlayer.motionZ += (double)(MathHelper.cos(f) * 0.07F);
				            mc.thePlayer.setSprinting(true); 
			            	mc.thePlayer.jumpMovementFactor = 0.425f;
			            	mc.thePlayer.motionY = mc.thePlayer.jumpMovementFactor;
			            }
			            
			            mc.thePlayer.setSprinting(true);
			            
					}else {
						
					}
				}
				else if (mode.getMode().equalsIgnoreCase("Test") && !b.isEnabled() && !mc.thePlayer.isInWater()) {
					
				}
				
				else if (mode.getMode() == "Test 2" && !b.isEnabled()) {
					
				}
				
				else if (mode.getMode() == "Test 3" && !b.isEnabled()) {
					
				}
				
			}
			
		}
		
	}
	
    public static double defaultSpeed() {
    	
        double normalSpeed = 0.2873D;
        
        if (Minecraft.getMinecraft().thePlayer.isPotionActive(Potion.moveSpeed)) {
            int amplifier = Minecraft.getMinecraft().thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier();
            normalSpeed *= (1.0D + 0.2D * (amplifier + 1));
        }
        
        return normalSpeed;
        
    }

	
}
