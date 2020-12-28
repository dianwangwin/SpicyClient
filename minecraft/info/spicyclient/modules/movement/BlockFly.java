package info.spicyclient.modules.movement;

import java.util.Comparator;
import java.util.Random;

import org.lwjgl.input.Keyboard;

import info.spicyclient.SpicyClient;
import info.spicyclient.chatCommands.Command;
import info.spicyclient.events.Event;
import info.spicyclient.events.listeners.EventMotion;
import info.spicyclient.events.listeners.EventSendPacket;
import info.spicyclient.events.listeners.EventSneaking;
import info.spicyclient.events.listeners.EventUpdate;
import info.spicyclient.modules.Module;
import info.spicyclient.settings.BooleanSetting;
import info.spicyclient.settings.ModeSetting;
import info.spicyclient.settings.NumberSetting;
import info.spicyclient.settings.SettingChangeEvent;
import info.spicyclient.settings.SettingChangeEvent.type;
import info.spicyclient.util.MovementUtils;
import info.spicyclient.util.RenderUtils;
import info.spicyclient.util.RotationUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

public class BlockFly extends Module {
	
	public BlockFly() {
		super("Block Fly", Keyboard.KEY_NONE, Category.BETA);
	}
	
	public void onEnable() {
		
		lastYaw = mc.thePlayer.rotationYaw;
		lastPitch = mc.thePlayer.rotationPitch;
		
	}
	
	public void onDisable() {
		
	}
	
	public static transient float lastYaw = 0, lastPitch = 0;
	
	public void onEvent(Event e) {
		
		if (e instanceof EventSendPacket & e.isPre()) {
			
			if (((EventSendPacket)e).packet instanceof C03PacketPlayer) {
				((C03PacketPlayer)((EventSendPacket)e).packet).setYaw(lastYaw);
				((C03PacketPlayer)((EventSendPacket)e).packet).setPitch(lastPitch);
			}
			
		}
		
		if (e instanceof EventUpdate && e.isPre() && mc.gameSettings.keyBindJump.isKeyDown() && MovementUtils.isOnGround(0.4)) {
			
			EventUpdate update = (EventUpdate)e;
			
			Double motionX = mc.thePlayer.motionX, motionZ = mc.thePlayer.motionZ;
			
			if (motionX < 0)
				motionX *= -1;
			
			if (motionZ < 0)
				motionZ *= -1;
			
			if (motionX < 0.05 && motionZ < 0.05) {
				mc.thePlayer.jump();
			}
			
		}
		
		if (e instanceof EventMotion && e.isPost()) {
			
			EventMotion event = (EventMotion) e;
			
			//mc.thePlayer.setSprinting(false);
			
			event.setYaw(lastYaw);
			event.setYaw(lastPitch);
			
			ItemStack i = mc.thePlayer.getCurrentEquippedItem();
			BlockPos below = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1D, mc.thePlayer.posZ);
			
			if (mc.theWorld.getBlockState(below).getBlock() == Blocks.air) {
				
				for (EnumFacing facing : EnumFacing.VALUES) {
					
					switch (facing) {
					case UP:
						
						BlockPos underBelow = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 2D, mc.thePlayer.posZ);
						
						if (mc.theWorld.getBlockState(underBelow).getBlock() != Blocks.air) {
							
							if (mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, mc.thePlayer.getCurrentEquippedItem(), underBelow, EnumFacing.UP, RotationUtils.getVectorForRotation(getRotationsHypixel(underBelow, EnumFacing.UP)[1], getRotationsHypixel(underBelow, EnumFacing.UP)[1]))) {
								
								mc.thePlayer.swingItem();
								event.setYaw(getRotationsHypixel(underBelow, facing)[0]);
								event.setPitch(getRotationsHypixel(underBelow, facing)[1]);
								lastYaw = event.yaw;
								lastPitch = event.pitch;
								return;
								
							}
							
						}
						
						break;
					case NORTH:
					case EAST:
					case SOUTH:
					case WEST:
						
						BlockInfo defaultPos = findFacingAndBlockPosForBlock(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1D, mc.thePlayer.posZ + mc.thePlayer.motionZ));
						
						if (defaultPos == null)
							return;
						
						if (mc.theWorld.getBlockState(defaultPos.pos).getBlock() != Blocks.air) {
							
							if (mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, mc.thePlayer.getCurrentEquippedItem(), defaultPos.pos, defaultPos.facing, RotationUtils.getVectorForRotation(getRotationsHypixel(defaultPos.targetPos, defaultPos.facing)[0], getRotationsHypixel(defaultPos.targetPos, defaultPos.facing)[1]))) {
								
								mc.thePlayer.swingItem();
								event.setYaw(getRotationsHypixel(defaultPos.pos, defaultPos.facing)[0]);
								event.setPitch(getRotationsHypixel(defaultPos.pos, defaultPos.facing)[1]);
								lastYaw = event.yaw;
								lastPitch = event.pitch;
								return;
								
							}
							
						}
						
						break;
						
					}
					
				}
				
			}
			
		}
		
	}
	
	private BlockInfo findFacingAndBlockPosForBlock(BlockPos input) {
		
		BlockInfo output = new BlockInfo();
		output.pos = input;		
		
		for (EnumFacing face : EnumFacing.VALUES) {
			
			if (mc.theWorld.getBlockState(output.pos.offset(face)).getBlock() != Blocks.air) {
				
				output.pos = output.pos.offset(face);
				output.facing = face.getOpposite();
				output.targetPos = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1D, mc.thePlayer.posZ);
				return output;
				
			}
			
		}
		
		for (EnumFacing face : EnumFacing.VALUES) {
			
			if (mc.theWorld.getBlockState(output.pos.offset(face)).getBlock() == Blocks.air) {
				
				for (EnumFacing face1 : EnumFacing.VALUES) {
					
					if (mc.theWorld.getBlockState(output.pos.offset(face).offset(face1)).getBlock() != Blocks.air) {
						
						output.pos = output.pos.offset(face).offset(face1);
						output.facing = face.getOpposite();
						output.targetPos = output.pos.offset(face);
						return output;
						
					}
					
				}
				
			}
			
		}
		
		return null;
		
	}
	
	public float[] getRotationsHypixel(BlockPos paramBlockPos, EnumFacing paramEnumFacing) {
        double d1 = (double)paramBlockPos.getX() + 0.5D - mc.thePlayer.posX + (double)paramEnumFacing.getFrontOffsetX() / 2.0D;
        double d2 = (double)paramBlockPos.getZ() + 0.5D - mc.thePlayer.posZ + (double)paramEnumFacing.getFrontOffsetZ() / 2.0D;
        double d3 = mc.thePlayer.posY + (double)mc.thePlayer.getEyeHeight() - ((double)paramBlockPos.getY() + 0.5D);
        double d4 = (double)MathHelper.sqrt_double(d1 * d1 + d2 * d2);
        float f1 = (float)(Math.atan2(d2, d1) * 180.0D / 3.141592653589793D) - 90.0F;
        float f2 = (float)(Math.atan2(d3, d4) * 180.0D / 3.141592653589793D);
        if (f1 < 0.0F) {
            f1 += 360.0F;
        }
        
        return new float[]{f1, f2};
    }
	
	private class BlockInfo {
		
		BlockPos pos, targetPos;
		EnumFacing facing;
		
	}
	
}
