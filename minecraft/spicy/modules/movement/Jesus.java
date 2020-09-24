package spicy.modules.movement;

import org.lwjgl.input.Keyboard;

import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.C03PacketPlayer;
import spicy.events.Event;
import spicy.events.listeners.EventMotion;
import spicy.events.listeners.EventPlayerUseItem;
import spicy.events.listeners.EventUpdate;
import spicy.modules.Module;

public class Jesus extends Module {

	public Jesus() {
		super("Jesus", Keyboard.KEY_NONE, Category.BETA);
	}
	
	public void onEnable() {
		
	}
	
	public void onDisable() {
		
	}
	
	public void onEvent(Event e) {
		
		if (e instanceof EventMotion) {
			
			if (e.isPost()) {
				
		        if (mc.thePlayer.worldObj.handleMaterialAcceleration(mc.thePlayer.getEntityBoundingBox().expand(0.0D, 0.05D, 0.0D).contract(0.001D, 0.001D, 0.001D), Material.water, mc.thePlayer)){
		        	if (mc.thePlayer.motionY <= 0) {
		        		
		        		double y, y1;
						mc.thePlayer.motionY = 0;
						
						if (mc.thePlayer.ticksExisted % 3 ==0) {
							
							y = mc.thePlayer.posY - 1.0E-10D;
							mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, y, mc.thePlayer.posZ, true));
							
						}
						
						y1 = mc.thePlayer.posY + 1.0E-10D;
						mc.thePlayer.setPosition(mc.thePlayer.posX, y1, mc.thePlayer.posZ);
						mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer(true));
		        		
		        	}else {
		        		mc.thePlayer.motionY = 0.1;
		        	}
		        }
				
			}
			
		}
		
	}
	
}
