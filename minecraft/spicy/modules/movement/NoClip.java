package spicy.modules.movement;

import org.lwjgl.input.Keyboard;

import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockHopper;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C13PacketPlayerAbilities;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S18PacketEntityTeleport;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import spicy.events.Event;
import spicy.events.listeners.EventGetBlockHitbox;
import spicy.events.listeners.EventMotion;
import spicy.events.listeners.EventPacket;
import spicy.events.listeners.EventUpdate;
import spicy.modules.Module;
import spicy.modules.Module.Category;
import spicy.util.MovementUtils;

public class NoClip extends Module {
	
	/*
	 * 
	 * NOTICE: This module's code is taken from the sigma 2 code
	 * here is the link to that https://gitlab.com/Arithmo/Sigma/-/blob/master/info/sigmaclient/module/impl/movement/Phase.java
	 * 
	 */
	
	public NoClip() {
		super("NoClip", Keyboard.KEY_NONE, Category.BETA);
	}

	
	public void onEnable() {
		mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.0784001, mc.thePlayer.posZ);
	}
	
	public void onDisable() {
		
	}
	
	public void onEvent(Event e) {
		
		if (e instanceof EventGetBlockHitbox) {
			
			if (e.isPre()) {
				
				EventGetBlockHitbox event = (EventGetBlockHitbox) e;
				event.setCanceled(true);
				mc.thePlayer.motionY = 0;
				mc.thePlayer.onGround = true;
				mc.thePlayer.noClip = true;
				
				if (mc.gameSettings.keyBindForward.pressed || mc.gameSettings.keyBindBack.pressed || mc.gameSettings.keyBindLeft.pressed || mc.gameSettings.keyBindRight.pressed) {
					
					float f = (float) MovementUtils.getDirection() + 180 - 45;
					MovementUtils.forward(0.01f);
		            //mc.thePlayer.motionX = (double)(MathHelper.sin(f) * 0.1F);
		            //mc.thePlayer.motionZ = (double)(MathHelper.cos(f) * 0.1F) * -1;
					
				}
				
				double y, y1;
				mc.thePlayer.motionY = 0;
				
				//if (mc.thePlayer.ticksExisted % 3 ==0) {
					
					//y = mc.thePlayer.posY - 1.0E-10D;
					//mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, y, mc.thePlayer.posZ, true));
					
				//}
				
				//y1 = mc.thePlayer.posY + 1.0E-10D;
				//mc.thePlayer.setPosition(mc.thePlayer.posX, y1, mc.thePlayer.posZ);
				
				//mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer(true));
				
			}
			
		}
		
	}
	
    private boolean isInsideBlock() {
        for (int x = MathHelper.floor_double(mc.thePlayer.boundingBox.minX); x < MathHelper.floor_double(mc.thePlayer.boundingBox.maxX) + 1; x++) {
            for (int y = MathHelper.floor_double(mc.thePlayer.boundingBox.minY); y < MathHelper.floor_double(mc.thePlayer.boundingBox.maxY) + 1; y++) {
                for (int z = MathHelper.floor_double(mc.thePlayer.boundingBox.minZ); z < MathHelper.floor_double(mc.thePlayer.boundingBox.maxZ) + 1; z++) {
                    Block block = mc.theWorld.getBlockState(new BlockPos(x, y, z)).getBlock();
                    if ((block != null) && (!(block instanceof BlockAir))) {
                        AxisAlignedBB boundingBox = block.getCollisionBoundingBox(mc.theWorld, new BlockPos(x, y, z), mc.theWorld.getBlockState(new BlockPos(x, y, z)));
                        if ((block instanceof BlockHopper)) {
                            boundingBox = new AxisAlignedBB(x, y, z, x + 1, y + 1, z + 1);
                        }
                        if (boundingBox != null) {
                            if (mc.thePlayer.boundingBox.intersectsWith(boundingBox)) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

	
}
