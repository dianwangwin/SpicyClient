package spicy.modules.movement;

import org.lwjgl.input.Keyboard;

import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import spicy.chatCommands.Command;
import spicy.events.Event;
import spicy.events.listeners.EventUpdate;
import spicy.modules.Module;

public class NoSlow extends Module {

	public NoSlow() {
		super("NoSlow", Keyboard.KEY_NONE, Category.MOVEMENT);
	}

	
	public void onEnable() {
		
	}
	
	public void onDisable() {
		
	}
	
	public void onEvent(Event e) {
		
		if (e instanceof EventUpdate && (mc.thePlayer.isBlocking() || mc.thePlayer.isUsingItem()) && mc.thePlayer.motionX != 0 && mc.thePlayer.motionY != 0 && mc.thePlayer.motionZ != 0) {
			
            if (e.isPre()) {
                mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
            }
            else if (e.isPost()) {
                
            	mc.playerController.sendUseItem(mc.thePlayer, mc.theWorld, mc.thePlayer.getHeldItem());
            	
            	//mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(mc.thePlayer.inventory.getCurrentItem()));
            }
            
		}
		
	}
	
}
