package info.spicyclient.modules.movement;

import java.util.Comparator;
import java.util.Random;

import org.lwjgl.input.Keyboard;

import info.spicyclient.SpicyClient;
import info.spicyclient.chatCommands.Command;
import info.spicyclient.events.Event;
import info.spicyclient.events.listeners.EventMotion;
import info.spicyclient.events.listeners.EventSneaking;
import info.spicyclient.events.listeners.EventUpdate;
import info.spicyclient.modules.Module;
import info.spicyclient.settings.BooleanSetting;
import info.spicyclient.settings.ModeSetting;
import info.spicyclient.settings.NumberSetting;
import info.spicyclient.settings.SettingChangeEvent;
import info.spicyclient.settings.SettingChangeEvent.type;
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
		
	}
	
	public void onDisable() {
		
	}
	
	public void onEvent(Event e) {
		
		if (e instanceof EventMotion && e.isPost()) {
			
			EventMotion event = (EventMotion) e;
			
			Block below = mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX, mc.thePlayer.boundingBox.minY - 0.1, mc.thePlayer.posZ)).getBlock();
			
			if (below == Blocks.air) {
				
				for (EnumFacing facing : EnumFacing.VALUES) {
					
					switch (facing) {
					case UP:
						
						Block underBelow = mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX, mc.thePlayer.boundingBox.minY - 1.1, mc.thePlayer.posZ)).getBlock();
						if (underBelow != Blocks.air) {
							
							if (mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, mc.thePlayer.getCurrentEquippedItem(), new BlockPos(underBelow.getBlockBoundsMaxX(), underBelow.getBlockBoundsMaxY(), underBelow.getBlockBoundsMaxZ()), EnumFacing.UP, RotationUtils.getVectorForRotation(getRotationsHypixel(new BlockPos(underBelow.getBlockBoundsMaxX() - 0.5, underBelow.getBlockBoundsMaxY() - 0.5, underBelow.getBlockBoundsMaxZ() - 0.5), EnumFacing.UP)[1], getRotationsHypixel(new BlockPos(underBelow.getBlockBoundsMaxX() - 0.5, underBelow.getBlockBoundsMaxY() - 0.5, underBelow.getBlockBoundsMaxZ() - 0.5), EnumFacing.UP)[1]))) {
								Command.sendPrivateChatMessage(new Random().nextInt(1000));
								event.setYaw(getRotationsHypixel(new BlockPos(underBelow.getBlockBoundsMaxX() - 0.5, underBelow.getBlockBoundsMaxY() - 0.5, underBelow.getBlockBoundsMaxZ() - 0.5), facing)[1]);
								event.setPitch(getRotationsHypixel(new BlockPos(underBelow.getBlockBoundsMaxX() - 0.5, underBelow.getBlockBoundsMaxY() - 0.5, underBelow.getBlockBoundsMaxZ() - 0.5), facing)[0]);
								
							}
							
						}
						
						break;
						
					}
					
				}
				
			}
			
		}
		
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
	
}
