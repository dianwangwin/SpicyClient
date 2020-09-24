package spicy.modules.movement;

import java.io.IOException;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C13PacketPlayerAbilities;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.potion.Potion;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import spicy.SpicyClient;
import spicy.chatCommands.Command;
import spicy.events.Event;
import spicy.events.listeners.EventMotion;
import spicy.events.listeners.EventUpdate;
import spicy.events.listeners.EventOnLadder;
import spicy.events.listeners.EventPacket;
import spicy.modules.Module;
import spicy.settings.ModeSetting;

public class Bhop extends Module {
	
	public ModeSetting mode = new ModeSetting("Mode", "Vanilla", "Vanilla", "PvpLands", "Hypixel", "Test", "Test 2", "Test 3");
	
	private static double lastY;
	private static float rotate = 180;
	
	private static int lagbackCheck = 0;
	private static long lastLagback = System.currentTimeMillis();
	
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
		
		if (e instanceof EventPacket) {
			
			if (e.isPre()) {
				
				if (((EventPacket) e).packet instanceof S08PacketPlayerPosLook) {
					
					if (lagbackCheck >= 4) {
						
						lagbackCheck = 0;
						lastLagback = System.currentTimeMillis() - (5*1000);
						this.toggle();
						Command.sendPrivateChatMessage(this.name + " has been disabled due to lagbacks");
						
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
					
				}
				
			}
			
		}
		
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
				else if (mode.is("Hypixel") && !b.isEnabled() && !mc.thePlayer.isInWater() && (mc.gameSettings.keyBindForward.pressed || mc.gameSettings.keyBindBack.pressed || mc.gameSettings.keyBindLeft.pressed || mc.gameSettings.keyBindRight.pressed)) {
					
					rotate = 180;
					
					if (!mc.gameSettings.keyBindForward.pressed && !mc.gameSettings.keyBindBack.pressed && mc.gameSettings.keyBindRight.pressed && mc.gameSettings.keyBindLeft.pressed) {
						
					}
					else if (mc.thePlayer.onGround) {
						mc.thePlayer.setSprinting(true);
						mc.thePlayer.motionY += 0.5;
			            //mc.thePlayer.jump();
					}
					
					if (mc.gameSettings.keyBindLeft.pressed && !mc.gameSettings.keyBindRight.pressed) {
						
						if (!mc.gameSettings.keyBindForward.pressed && !mc.gameSettings.keyBindRight.pressed && !mc.gameSettings.keyBindBack.pressed) {
							rotate -= 90;
						}
						else if (mc.gameSettings.keyBindForward.pressed) {
							rotate -= 10;
						}else {
							rotate += 45;
						}
						
					}
					
					if (mc.gameSettings.keyBindRight.pressed && !mc.gameSettings.keyBindLeft.pressed) {
						
						if (!mc.gameSettings.keyBindForward.pressed && !mc.gameSettings.keyBindLeft.pressed && !mc.gameSettings.keyBindBack.pressed) {
							rotate += 90;
						}
						else if (mc.gameSettings.keyBindForward.pressed) {
							rotate += 90;
						}else {
							rotate -= 45;
						}
						
					}
					
					if (mc.gameSettings.keyBindBack.pressed) {
						
						if (mc.gameSettings.keyBindForward.pressed) {
							rotate += 45;
						}else {
							rotate -= 180;
						}
						
					}
					
					if (mc.gameSettings.keyBindForward.pressed) {
						
						if (rotate != 180) {
							
							if (rotate < 0) {
								rotate += 45;
							}else {
								rotate -= 45;
							}
							
						}
						
					}
					
					float f = (mc.thePlayer.rotationYaw + rotate) * 0.017453292F;
					
					if (!mc.gameSettings.keyBindForward.pressed && !mc.gameSettings.keyBindBack.pressed && mc.gameSettings.keyBindRight.pressed && mc.gameSettings.keyBindLeft.pressed) {
						
					}else {
						mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer(true));
						event.onGround = true;
			            mc.thePlayer.motionX = (double)(MathHelper.sin(f) * 0.32F);
			            mc.thePlayer.motionZ = (double)(MathHelper.cos(f) * 0.32F) * -1;
					}
					
				}
				else if (mode.getMode().equalsIgnoreCase("Test") && !b.isEnabled() && !mc.thePlayer.isInWater()) {
					
					float f = mc.thePlayer.rotationYaw * 0.017453292F;
					float l = (mc.thePlayer.rotationYaw + 90) * 0.017453292F;
					float r = (mc.thePlayer.rotationYaw - 90) * 0.017453292F;
					
					mc.gameSettings.keyBindJump.pressed = false;
					
					if (mc.thePlayer.onGround && mc.gameSettings.keyBindForward.pressed) {
						mc.thePlayer.setSprinting(true);
			            mc.thePlayer.motionX = (double)(MathHelper.sin(f) * 0.33F) * -1;
			            mc.thePlayer.motionZ = (double)(MathHelper.cos(f) * 0.33F);
			            mc.thePlayer.jump();
						
					}else if (mc.gameSettings.keyBindForward.pressed) {
						mc.thePlayer.setSprinting(true);
			            mc.thePlayer.motionX = (double)(MathHelper.sin(f) * 0.33F) * -1;
			            mc.thePlayer.motionZ = (double)(MathHelper.cos(f) * 0.33F);
					}
					
					if (mc.thePlayer.onGround && mc.gameSettings.keyBindBack.pressed && !mc.gameSettings.keyBindForward.pressed) {
						mc.thePlayer.setSprinting(true);
			            mc.thePlayer.motionX = (double)(MathHelper.sin(f) * 0.33F);
			            mc.thePlayer.motionZ = (double)(MathHelper.cos(f) * 0.33F) * -1;
			            mc.thePlayer.jump();
						
					}else if (mc.gameSettings.keyBindBack.pressed && !mc.gameSettings.keyBindForward.pressed) {
						mc.thePlayer.setSprinting(true);
			            mc.thePlayer.motionX = (double)(MathHelper.sin(f) * 0.33F);
			            mc.thePlayer.motionZ = (double)(MathHelper.cos(f) * 0.33F) * -1;
					}
					
					if (mc.thePlayer.onGround && mc.gameSettings.keyBindLeft.pressed && !mc.gameSettings.keyBindRight.pressed) {
						mc.thePlayer.setSprinting(true);
			            mc.thePlayer.motionX = (double)(MathHelper.sin(l) * 0.33F);
			            mc.thePlayer.motionZ = (double)(MathHelper.cos(l) * 0.33F) * -1;
			            mc.thePlayer.jump();
						
					}else if (mc.gameSettings.keyBindLeft.pressed && !mc.gameSettings.keyBindRight.pressed) {
						mc.thePlayer.setSprinting(true);
			            mc.thePlayer.motionX = (double)(MathHelper.sin(l) * 0.33F);
			            mc.thePlayer.motionZ = (double)(MathHelper.cos(l) * 0.33F) * -1;
					}
					
					if (mc.thePlayer.onGround && mc.gameSettings.keyBindRight.pressed && !mc.gameSettings.keyBindLeft.pressed) {
						mc.thePlayer.setSprinting(true);
			            mc.thePlayer.motionX = (double)(MathHelper.sin(r) * 0.33F);
			            mc.thePlayer.motionZ = (double)(MathHelper.cos(r) * 0.33F) * -1;
			            mc.thePlayer.jump();
						
					}else if (mc.gameSettings.keyBindRight.pressed && !mc.gameSettings.keyBindLeft.pressed) {
						mc.thePlayer.setSprinting(true);
			            mc.thePlayer.motionX = (double)(MathHelper.sin(r) * 0.33F);
			            mc.thePlayer.motionZ = (double)(MathHelper.cos(r) * 0.33F) * -1;
					}
					
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
