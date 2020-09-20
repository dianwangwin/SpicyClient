package spicy.modules.movement;

import java.util.Comparator;

import org.lwjgl.input.Keyboard;

import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;
import spicy.SpicyClient;
import spicy.events.Event;
import spicy.events.listeners.EventMotion;
import spicy.events.listeners.EventSneaking;
import spicy.events.listeners.EventUpdate;
import spicy.modules.Module;
import spicy.settings.BooleanSetting;
import spicy.settings.ModeSetting;
import spicy.settings.NumberSetting;
import spicy.settings.SettingChangeEvent;
import spicy.settings.SettingChangeEvent.type;

public class BlockFly extends Module {
	
	private ModeSetting rotationMode = new ModeSetting("Rotation Mode", "90 degree snap", "90 degree snap", "test");
	private BooleanSetting keepRotations = new BooleanSetting("keepRotations", true);
	private BooleanSetting sprintEnabled = new BooleanSetting("Sprint", false);
	private ModeSetting sneakMode = new ModeSetting("Sneak Mode", "None", "None", "Always", "End of block");
	private ModeSetting blockFlyMode = new ModeSetting("Mode", "none", "none", "NCP");
	private NumberSetting speed = new NumberSetting("Speed", 100, 1, 100, 1);
	private NumberSetting pitch = new NumberSetting("Pitch", 89.8, 50, 90, 0.05);
	
	public BlockFly() {
		super("Block Fly", Keyboard.KEY_NONE, Category.MOVEMENT);
		resetSettings();
	}
	
	@Override
	public void resetSettings() {
		this.settings.clear();
		this.addSettings(rotationMode, keepRotations, sprintEnabled, sneakMode, blockFlyMode, speed, pitch);
	}
	
	@Override
	public void onSettingChange(SettingChangeEvent e) {
		
		if (e.settingType.equals(type.MODE)) {
			if (blockFlyMode == null) {
				
			}
			else if (blockFlyMode.is("none")) {
				if (!settings.contains(pitch)) {
					settings.add(pitch);
				}
				if (!settings.contains(speed)) {
					settings.add(speed);
				}
				this.settings.sort(Comparator.comparing(s -> s == keycode ? 1 : 0));
			}
			else if (blockFlyMode.is("NCP")) {
				
				if (settings.contains(pitch)) {
					settings.remove(settings.indexOf(pitch));
				}
				if (settings.contains(speed)) {
					settings.remove(settings.indexOf(speed));
				}
				
				speed.setValue(100);
				pitch.setValue(84.75);
				
			}
		}
		
	}
	
	public void onEnable() {
		oldYaw = mc.thePlayer.rotationYaw;
		oldPitch = mc.thePlayer.rotationPitch;
	}
	
	public void onDisable() {
		mc.gameSettings.keyBindSneak.pressed = false;
	}
	
	private BlockPos currentPos;
	private BlockPos playerPos;
	private EnumFacing currentFacing;
	
	private float oldYaw = 0;
	private float oldPitch = 0;
	
	public void onEvent(Event e) {
		
		if (e instanceof EventMotion) {
			
			if (e.isPre()) {
				
				EventMotion event = (EventMotion) e;
				
				if (sneakMode.getMode() == "End of block") {
					mc.gameSettings.keyBindSneak.pressed = false;
				}
				
				if (mc.thePlayer.onGround) {
					
					event.motionX = event.motionX * (speed.getValue() / 100);
					event.motionZ = event.motionZ * (speed.getValue() / 100);
				}
				
				currentFacing = null;
				
				ItemStack itemstack = mc.thePlayer.inventory.getCurrentItem();
				
				playerPos = new BlockPos(event.getX(), event.getY(), event.getZ());
				currentPos = new BlockPos(event.getX(), event.getY() - 1, event.getZ());
				
				if (keepRotations.enabled) {
					hypixelRotation(event);
					oldYaw = event.yaw;
					oldPitch = event.pitch;
				}
				
				if (sprintEnabled.enabled) {
					mc.thePlayer.setSprinting(true);
				}else {
					mc.thePlayer.setSprinting(false);
				}
				
				if (sneakMode.getMode() == "Always") {
					mc.gameSettings.keyBindSneak.pressed = true;
				}
				
				if (rotationMode.is("90 degree snap")) {
					currentFacing = snapFacingAndRotation(event);
				}
				
				if (currentFacing == null || currentPos == null || mc.thePlayer.getCurrentEquippedItem() == null) {
					
				}else {
					if (sneakMode.getMode() == "End of block") {
						mc.gameSettings.keyBindSneak.pressed = true;
					}
					if (mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, mc.thePlayer.getCurrentEquippedItem(), currentPos, currentFacing, new Vec3(currentPos.getX(), currentPos.getY(), currentPos.getZ()))) {
						mc.thePlayer.swingItem();
						System.out.println("Sent a block place packet");
					}
					
				}
				
			}
			
		}
		
	}
	
	public void hypixelRotation(EventMotion event) {
		
		if (mc.thePlayer.moveForward > 0.01 && mc.thePlayer.moveStrafing == 0) {
			
			currentFacing = mc.thePlayer.getHorizontalFacing();
			if (currentFacing == EnumFacing.NORTH) {
				
				event.setYaw(mc.thePlayer.rotationYaw + 180);
				event.setPitch((float) pitch.getValue());
				return;
				
			}
			if (currentFacing == EnumFacing.EAST) {
				
				event.setYaw(mc.thePlayer.rotationYaw + 180);
				event.setPitch((float) pitch.getValue());
				return;
				
			}
			if (currentFacing == EnumFacing.SOUTH) {
				
				event.setYaw(mc.thePlayer.rotationYaw + 180);
				event.setPitch((float) pitch.getValue());
				return;
				
			}
			if (currentFacing == EnumFacing.WEST) {
				
				event.setYaw(mc.thePlayer.rotationYaw + 180);
				event.setPitch((float) pitch.getValue());
				return;
				
			}
			
		}
		
	}
	
	public EnumFacing snapFacingAndRotation(EventMotion event) {
		
		if (mc.theWorld.getBlockState(currentPos).getBlock() instanceof BlockAir) {
			
			if (mc.thePlayer.moveForward > 0.001) {
				
				// North
				if (!(mc.theWorld.getBlockState(currentPos.add(0, 0, 1)).getBlock() instanceof BlockAir)) {
					
					currentPos = new BlockPos(event.getX(), event.getY() - 1, event.getZ() + 1);
					event.setYaw(0);
					event.setPitch((float) pitch.getValue());
					return EnumFacing.NORTH;
					
				}
				
				// South
				if (!(mc.theWorld.getBlockState(currentPos.add(0, 0, -1)).getBlock() instanceof BlockAir)) {
					
					currentPos = new BlockPos(event.getX(), event.getY() - 1, event.getZ() - 1);
					event.setYaw(180);
					event.setPitch((float) pitch.getValue());
					return EnumFacing.SOUTH;
					
				}
				
				// West
				if (!(mc.theWorld.getBlockState(currentPos.add(1, 0, 0)).getBlock() instanceof BlockAir)) {
					
					currentPos = new BlockPos(event.getX() + 1, event.getY() - 1, event.getZ());
					event.setYaw(-90);
					event.setPitch((float) pitch.getValue());
					return EnumFacing.WEST;
					
				}
				
				// East
				if (!(mc.theWorld.getBlockState(currentPos.add(-1, 0, 0)).getBlock() instanceof BlockAir)) {
					
					currentPos = new BlockPos(event.getX() - 1, event.getY() - 1, event.getZ());
					event.setYaw(90);
					event.setPitch((float) pitch.getValue());
					return EnumFacing.EAST;
					
				}
				
			}
			
			return null;
			
		}
		
		return null;
		
	}
	
	public EnumFacing setBlockAndFacing(EventMotion event) {
		
		EnumFacing facing = null;
		
		if (currentPos == null) {
			
		}else {
			
			if (mc.thePlayer.moveForward > 0.01 && mc.thePlayer.moveStrafing == 0) {
				
				currentFacing = mc.thePlayer.getHorizontalFacing();
				if (currentFacing == EnumFacing.NORTH) {
					facing = EnumFacing.SOUTH;
					if (mc.theWorld.getBlockState(currentPos.add(0, 0, 1)).getBlock() instanceof BlockAir) {
						
					}else {
						return facing;
					}
				}
				if (currentFacing == EnumFacing.EAST) {
					facing = EnumFacing.WEST;
					if (mc.theWorld.getBlockState(currentPos.add(-1, 0, 0)).getBlock() instanceof BlockAir) {
						
					}else {
						return facing;
					}
				}
				if (currentFacing == EnumFacing.SOUTH) {
					facing = EnumFacing.NORTH;
					if (mc.theWorld.getBlockState(currentPos.add(0, 0, -1)).getBlock() instanceof BlockAir) {
						
					}else {
						return facing;
					}
				}
				if (currentFacing == EnumFacing.WEST) {
					facing = EnumFacing.EAST;
					if (mc.theWorld.getBlockState(currentPos.add(1, 0, 0)).getBlock() instanceof BlockAir) {
						
					}else {
						return facing;
					}
				}
				
			}
			if (mc.thePlayer.moveForward < -0.01 && mc.thePlayer.moveStrafing == 0) {
				
				facing = mc.thePlayer.getHorizontalFacing();
				
				currentFacing = mc.thePlayer.getHorizontalFacing();
				if (currentFacing == EnumFacing.NORTH) {
					facing = EnumFacing.SOUTH;
					if (mc.theWorld.getBlockState(currentPos.add(0, 0, -1)).getBlock() instanceof BlockAir) {
						
					}else {
						return facing;
					}
				}
				if (currentFacing == EnumFacing.EAST) {
					facing = EnumFacing.WEST;
					if (mc.theWorld.getBlockState(currentPos.add(1, 0, 0)).getBlock() instanceof BlockAir) {
						
					}else {
						event.setPitch(90);
						event.setYaw(-90);
						return facing;
					}
				}
				if (currentFacing == EnumFacing.SOUTH) {
					facing = EnumFacing.NORTH;
					if (mc.theWorld.getBlockState(currentPos.add(0, 0, 1)).getBlock() instanceof BlockAir) {
						
					}else {
						return facing;
					}
				}
				if (currentFacing == EnumFacing.WEST) {
					facing = EnumFacing.EAST;
					if (mc.theWorld.getBlockState(currentPos.add(-1, 0, 0)).getBlock() instanceof BlockAir) {
						
					}else {
						event.setPitch(90);
						event.setYaw(90);
						return facing;
					}
				}
				
			}
			if (mc.thePlayer.moveForward == 0 && mc.thePlayer.moveStrafing > 0.01) {
				
				currentFacing = mc.thePlayer.getHorizontalFacing();
				if (currentFacing == EnumFacing.NORTH) {
					facing = EnumFacing.EAST;
					if (mc.theWorld.getBlockState(currentPos.add(1, 0, 0)).getBlock() instanceof BlockAir) {
						
					}else {
						return facing;
					}
				}
				if (currentFacing == EnumFacing.EAST) {
					facing = EnumFacing.SOUTH;
					if (mc.theWorld.getBlockState(currentPos.add(0, 0, 1)).getBlock() instanceof BlockAir) {
						
					}else {
						return facing;
					}
				}
				if (currentFacing == EnumFacing.SOUTH) {
					facing = EnumFacing.WEST;
					if (mc.theWorld.getBlockState(currentPos.add(-1, 0, 0)).getBlock() instanceof BlockAir) {
						
					}else {
						return facing;
					}
				}
				if (currentFacing == EnumFacing.WEST) {
					facing = EnumFacing.NORTH;
					if (mc.theWorld.getBlockState(currentPos.add(0, 0, -1)).getBlock() instanceof BlockAir) {
						
					}else {
						return facing;
					}
				}
				
			}
			if (mc.thePlayer.moveForward == 0 && mc.thePlayer.moveStrafing < -0.01) {
				
				currentFacing = mc.thePlayer.getHorizontalFacing();
				if (currentFacing == EnumFacing.NORTH) {
					facing = EnumFacing.WEST;
					if (mc.theWorld.getBlockState(currentPos.add(-1, 0, 0)).getBlock() instanceof BlockAir) {
						
					}else {
						return facing;
					}
				}
				if (currentFacing == EnumFacing.EAST) {
					facing = EnumFacing.NORTH;
					if (mc.theWorld.getBlockState(currentPos.add(0, 0, -1)).getBlock() instanceof BlockAir) {
						
					}else {
						return facing;
					}
				}
				if (currentFacing == EnumFacing.SOUTH) {
					facing = EnumFacing.EAST;
					if (mc.theWorld.getBlockState(currentPos.add(1, 0, 0)).getBlock() instanceof BlockAir) {
						
					}else {
						return facing;
					}
				}
				if (currentFacing == EnumFacing.WEST) {
					facing = EnumFacing.SOUTH;
					if (mc.theWorld.getBlockState(currentPos.add(0, 0, 1)).getBlock() instanceof BlockAir) {
						
					}else {
						return facing;
					}
				}
				
			}
			
		}
		
		return null;
		
	}
	
}
