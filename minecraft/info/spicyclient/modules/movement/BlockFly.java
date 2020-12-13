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
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;

public class BlockFly extends Module {
	
	public BlockFly() {
		super("Block Fly", Keyboard.KEY_NONE, Category.BETA);
	}
	
	public void onEnable() {
		
	}
	
	public void onDisable() {
		
	}
	
	private BlockPos currentPos;
	private BlockPos playerPos;
	private EnumFacing currentFacing;
	
	public void onEvent(Event e) {
		
		if (e instanceof EventMotion && e.isPre() && mc.thePlayer.getCurrentEquippedItem() != null) {
			
			if (mc.theWorld.getBlockState(mc.thePlayer.getPosition().add(0, -1, 0)).getBlock() instanceof BlockAir) {
				//currentPos = mc.thePlayer.getPosition().add(0, -1, 0);
				currentPos = new BlockPos(mc.thePlayer.posX,mc.thePlayer.isCollidedVertically ? mc.thePlayer.posY+0:mc.thePlayer.posY-1+0, mc.thePlayer.posZ);
				mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, mc.thePlayer.inventory.getCurrentItem(), currentPos.add(0, -1, 0), EnumFacing.UP, RotationUtils.getVectorForRotation(RotationUtils.getRotationFromPosition(currentPos.getX(), currentPos.getZ(), currentPos.getY())[1], RotationUtils.getRotationFromPosition(currentPos.getX(), currentPos.getZ(), currentPos.getY())[0]));
				mc.thePlayer.swingItem();
				((EventMotion)e).setYaw(RotationUtils.getRotationFromPosition(currentPos.getX(), currentPos.getZ(), currentPos.getY())[0]);
				((EventMotion)e).setPitch(RotationUtils.getRotationFromPosition(currentPos.getX(), currentPos.getZ(), currentPos.getY())[1]);
				
				RenderUtils.setCustomYaw(RotationUtils.getRotationFromPosition(currentPos.getX(), currentPos.getZ(), currentPos.getY())[0]);
				RenderUtils.setCustomPitch(RotationUtils.getRotationFromPosition(currentPos.getX(), currentPos.getZ(), currentPos.getY())[1]);
				
			}
			
		}
		
	}
	
}
