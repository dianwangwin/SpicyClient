package spicy.modules.movement;

import org.lwjgl.input.Keyboard;

import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockHopper;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import spicy.events.Event;
import spicy.events.listeners.EventMotion;
import spicy.events.listeners.EventUpdate;
import spicy.modules.Module;
import spicy.modules.Module.Category;

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
		
	}
	
	public void onDisable() {
		mc.thePlayer.noClip = false;
	}
	
	public void onEvent(Event e) {
		
		if (e instanceof EventUpdate) {
			
			if (e.isPre()) {
				
                final double mx = Math.cos(Math.toRadians(mc.thePlayer.rotationYaw + 90.0f));
                final double mz = Math.sin(Math.toRadians(mc.thePlayer.rotationYaw + 90.0f));
                final double x = mc.thePlayer.movementInput.moveForward * 1.75 * mx + mc.thePlayer.movementInput.moveStrafe * 1.75 * mz;
                final double z = mc.thePlayer.movementInput.moveForward * 1.75 * mz - mc.thePlayer.movementInput.moveStrafe * 1.75 * mx;

				
                if (mc.thePlayer.isCollidedHorizontally && !mc.thePlayer.isOnLadder() && !isInsideBlock()) {
                    mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX + x, mc.thePlayer.posY, mc.thePlayer.posZ + z, false));
                    final double posX2 = mc.thePlayer.posX;
                    final double posY2 = mc.thePlayer.posY;
                    mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(posX2, posY2 - (1), mc.thePlayer.posZ, false));
                    mc.thePlayer.setPosition(mc.thePlayer.posX + x, mc.thePlayer.posY, mc.thePlayer.posZ + z);
                    return;
                }
                return;

                
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
