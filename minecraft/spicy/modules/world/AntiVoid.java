package spicy.modules.world;

import org.apache.commons.lang3.StringUtils;
import org.lwjgl.input.Keyboard;

import net.minecraft.block.BlockAir;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import spicy.events.Event;
import spicy.events.listeners.EventChatmessage;
import spicy.events.listeners.EventMotion;
import spicy.events.listeners.EventUpdate;
import spicy.modules.Module;
import spicy.settings.ModeSetting;
import spicy.settings.NumberSetting;

public class AntiVoid extends Module {
	
	private ModeSetting mode = new ModeSetting("Mode", "Hypixel", "Hypixel");
	private NumberSetting packetAmount = new NumberSetting("Packet Amount", 150, 1, 1000, 1);
	private NumberSetting maxTimeInAir = new NumberSetting("Max time in air", 0.1, 0, 60, 0.1);
	
	public AntiVoid() {
		super("Anti Void", Keyboard.KEY_NONE, Category.BETA);
		resetSettings();
	}
	
	@Override
	public void resetSettings() {
		this.settings.clear();
		this.addSettings(mode, packetAmount, maxTimeInAir);
	}
	
	public void onEnable() {
		
	}
	
	public void onDisable() {
		
	}
	
	private boolean overVoid = true;
	private long timeOverVoid = 0;
	private boolean sendPackets = false;
	
	private static Long temp = 0L;
	
	public void onEvent(Event e) {
		
		if (e instanceof EventMotion) {
			
			if (e.isPre()) {
				
				EventMotion event = (EventMotion) e;
				
				if (mode.getMode() == "Hypixel") {
					
					if (sendPackets) {
						
			        	mc.thePlayer.motionY =  -0.0000000001f;
			        	mc.thePlayer.motionY =  0.0000000003f;
			        	mc.thePlayer.onGround = true;
						
						sendPackets = false;
						
					}
					
					if (!event.onGround) {
						
						for (double i = event.getY(); i > 0; i--) {
							
							if (mc.theWorld.getBlockState(new BlockPos(event.getX(), i, event.getZ())).getBlock() instanceof BlockAir){
								
								overVoid = true;
								
							}else {
								overVoid = false;
							}
							
							if (!overVoid) {
								break;
							}
							
						}
						
						if (overVoid) {
							
							if (timeOverVoid == 0) {
								
								timeOverVoid = (long) (System.currentTimeMillis() + (maxTimeInAir.getValue() * 1000));
								
							}else {
								
								if (System.currentTimeMillis() >= timeOverVoid) {
									
									timeOverVoid = 0;
									sendPackets = true;
									
									
								}
								
							}
							
						}
						
					}else {
						timeOverVoid = 0;
					}
					
				}
				
			}
			
		}
		
	}
	
}
