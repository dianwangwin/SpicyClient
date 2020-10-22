package spicy.modules.world;

import java.util.Random;

import org.apache.commons.lang3.StringUtils;
import org.lwjgl.input.Keyboard;

import net.minecraft.block.BlockAir;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import spicy.SpicyClient;
import spicy.chatCommands.Command;
import spicy.events.Event;
import spicy.events.listeners.EventChatmessage;
import spicy.events.listeners.EventMotion;
import spicy.events.listeners.EventPacket;
import spicy.events.listeners.EventUpdate;
import spicy.modules.Module;
import spicy.settings.ModeSetting;
import spicy.settings.NumberSetting;
import spicy.util.Timer;

public class AntiVoid extends Module {
	
	
	// Not used anymore
	private ModeSetting mode = new ModeSetting("Mode", "Hypixel", "Hypixel");
	
	public AntiVoid() {
		super("Anti Void", Keyboard.KEY_NONE, Category.BETA);
		resetSettings();
	}
	
	@Override
	public void resetSettings() {
		this.settings.clear();
		this.addSettings(mode);
	}
	
	public void onEnable() {
		
	}
	
	public void onDisable() {
		
	}
	
	public void onEvent(Event e) {
		
		if (e instanceof EventPacket) {
			
			if (e.isPre()) {
				
				try {
					if (((EventPacket) e).packet instanceof S08PacketPlayerPosLook) {
						
						mc.thePlayer.fallDistance = 0;
						
					}
				} catch (NullPointerException e2) {
					
				}
				
			}
			
		}
		
		if (e instanceof EventUpdate) {
			
			if (e.isPre()) {
				
				if (mode.is("Hypixel") && !SpicyClient.config.fly.isEnabled()) {
					
					this.additionalInformation = "Hypixel";
					
					boolean isOverVoid = true;
					BlockPos block = mc.thePlayer.getPosition();
					
					for (int i = (int) mc.thePlayer.posY; i > 0; i--) {
						
						if (isOverVoid) {
							
							if (!(mc.theWorld.getBlockState(block).getBlock() instanceof BlockAir)) {
								
								isOverVoid = false;
								
							}
							
						}
						
						block = block.add(0, -1, 0);
						
					}
					
			        if (!mc.thePlayer.onGround && mc.thePlayer.fallDistance >= 6.0f && isOverVoid) {
			        	
			        	Random r = new Random();
			        	mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer(true));
			        	mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + 6, mc.thePlayer.posZ);
			        	mc.thePlayer.fallDistance = 0;
			        	//mc.thePlayer.motionY = 1;
			            //float f = mc.thePlayer.rotationYaw * 0.017453292F;
			            //mc.thePlayer.motionX -= (double)(MathHelper.sin(f) * 0.035f);
			           //c.thePlayer.motionZ += (double)(MathHelper.cos(f) * 0.035f);
			            
			        }
					
				}
				
			}
			
		}
		
	}
	
}
