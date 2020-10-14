package spicy.modules.movement;

import java.util.Comparator;
import java.util.Random;

import org.lwjgl.input.Keyboard;

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
import spicy.SpicyClient;
import spicy.chatCommands.Command;
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
		oldYaw = -1000;
		oldPitch = -1000;
	}
	
	public void onDisable() {
		mc.gameSettings.keyBindSneak.pressed = false;
	}
	
	private BlockPos currentPos;
	private BlockPos playerPos;
	private EnumFacing currentFacing;
	
	private float oldYaw = -1000;
	private float oldPitch = -1000;
	
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
				
				playerPos = new BlockPos(event.getX(), event.getY(), event.getZ());
				currentPos = new BlockPos(event.getX(), event.getY() - 1, event.getZ());
				
				if (sprintEnabled.enabled) {
					mc.thePlayer.setSprinting(true);
				}else {
					mc.thePlayer.setSprinting(false);
				}
				
				if (sneakMode.getMode() == "Always") {
					mc.gameSettings.keyBindSneak.pressed = true;
				}
				
				if (keepRotations.enabled) {
					keepRotations(event);
				}
				
				if (rotationMode.is("90 degree snap")) {
					currentFacing = snapFacingAndRotation(event);
					if (event.yaw != mc.thePlayer.rotationYaw) {
						event.yaw = event.yaw + 180;
					}
					Random random = new Random();
					int r = random.nextInt(4);
					if (random.nextBoolean()) {
						r *= -1;
					}
					event.pitch += r;
					
					r = random.nextInt(4);
					if (random.nextBoolean()) {
						r *= -1;
					}
					
					event.yaw += r;
					
				}
				else if (rotationMode.is("test")) {
					
					currentFacing = snapFacingAndRotation(event);
					
				}
				
				if (mc.thePlayer.inventory.getCurrentItem() == null) {
					return;
				}
				
				ItemStack itemstack = mc.thePlayer.inventory.getCurrentItem();
				
				if (currentFacing == null || currentPos == null || mc.thePlayer.getCurrentEquippedItem() == null || !(mc.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemBlock)) {
					
				}else {
					if (sneakMode.getMode() == "End of block") {
						mc.gameSettings.keyBindSneak.pressed = true;
					}
					if (mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, mc.thePlayer.getCurrentEquippedItem(), currentPos, currentFacing, new Vec3(currentPos.getX(), currentPos.getY(), currentPos.getZ()))) {
						mc.thePlayer.swingItem();
						System.out.println("Sent a block place packet");
						oldYaw = event.yaw;
						oldPitch = event.pitch;
					}
					
				}
				
			}
			
		}
		
	}
	
	public void keepRotations(EventMotion event) {
		
		Random random = new Random();
		
		if (rotationMode.is("90 degree snap")) {
			
			if (mc.thePlayer.moveForward > 0.01 && mc.thePlayer.moveStrafing == 0) {
				
				int pitch = random.nextInt(2);
				pitch = pitch + 75;
				
				float yaw = random.nextInt(2);
				if (random.nextBoolean()) {
					
					yaw = yaw * -1;
					
				}
				
				currentFacing = mc.thePlayer.getHorizontalFacing();
				if (currentFacing == EnumFacing.NORTH) {
					
					event.setYaw(mc.thePlayer.rotationYaw + 180 + yaw);
					event.setPitch(pitch);
					return;
					
				}
				if (currentFacing == EnumFacing.EAST) {
					
					event.setYaw(mc.thePlayer.rotationYaw + 180 + yaw);
					event.setPitch(pitch);
					return;
					
				}
				if (currentFacing == EnumFacing.SOUTH) {
					
					event.setYaw(mc.thePlayer.rotationYaw + 180 + yaw);
					event.setPitch(pitch);
					return;
					
				}
				if (currentFacing == EnumFacing.WEST) {
					
					event.setYaw(mc.thePlayer.rotationYaw + 180 + yaw);
					event.setPitch(pitch);
					return;
					
				}
				
			}
			
		}else {
			
			event.setYaw(oldYaw);
			event.setPitch(oldPitch);
			
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
	
	public float[] getRotations(double x, double y, double z) {
		
		double playerX = mc.thePlayer.posX, playerZ = mc.thePlayer.posZ;
		
		if (currentFacing == null) {
			return null;
		}
		
		double deltaX = x + (x - x) - playerX,
				deltaY = y - 3.5 - mc.thePlayer.posY + mc.thePlayer.getEyeHeight(),
				deltaZ = z + (z - z) - playerZ,
				distance = Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaZ, 2));
		
		//deltaY = y - 3.5 + e.getEyeHeight() - mc.thePlayer.posY + mc.thePlayer.getEyeHeight(),
		
		float yaw = (float) Math.toDegrees(-Math.atan(deltaX / deltaZ)),
				pitch = (float) -Math.toDegrees(Math.atan(deltaY / distance));
		
		
		if (deltaX < 0 && deltaZ < 0) {
			
			yaw = (float) (90 + Math.toDegrees(Math.atan(deltaZ/deltaX)));
			
		}else if (deltaX > 0 && deltaZ < 0) {
			
			yaw = (float) (-90 + Math.toDegrees(Math.atan(deltaZ/deltaX)));
			
		}
		
		float tempYaw = yaw;
		
		if (!(tempYaw < mc.thePlayer.rotationYaw - 130) && !(tempYaw > mc.thePlayer.rotationYaw + 130)) {
			Command.sendPrivateChatMessage("no");
			return null;
		}
		
		if (pitch >= 88) {
			return null;
		}
		
		Command.sendPrivateChatMessage("Yaw: " + yaw + " Pitch: " + pitch);
		
		//return new float[] { yaw, pitch };
		return new float[] { yaw, pitch };
		
	}
	
	public double[] getCoords(EventMotion event) {

		if (currentPos != null && mc.theWorld.getBlockState(currentPos).getBlock() instanceof BlockAir) {
			
			if (true) {
				
				Random random = new Random();
				
				float r = random.nextInt(30);
				r = r / 100f;
				if (random.nextBoolean()) {
					r = r * -1;
				}
				Command.sendPrivateChatMessage(r + "");
				
				// North
				if (!(mc.theWorld.getBlockState(currentPos.add(0, 0, 1)).getBlock() instanceof BlockAir)) {
					
					currentPos = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1, mc.thePlayer.posZ + 1);
					currentFacing = EnumFacing.NORTH;
					if (getRotations(currentPos.getX() + 0.25 + r, currentPos.getY() + 0.5 + r, currentPos.getZ() + r) == null) {
						currentFacing = null;
						return null;
					}
					
					event.yaw = getRotations(currentPos.getX() + 0.25 + r, currentPos.getY() + 0.5, currentPos.getZ() + r)[0];
					event.pitch = getRotations(currentPos.getX() + 0.25 + r, currentPos.getY() + 0.5, currentPos.getZ() + r)[1];
					
				}
				
				// South
				if (!(mc.theWorld.getBlockState(currentPos.add(0, 0, -1)).getBlock() instanceof BlockAir)) {
					
					currentPos = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1, mc.thePlayer.posZ - 1);
					currentFacing = EnumFacing.SOUTH;
					if (getRotations(currentPos.getX() + 0.5 + r, currentPos.getY(), currentPos.getZ() + 0.75 + r) == null) {
						currentFacing = null;
						return null;
					}
					event.yaw = getRotations(currentPos.getX() + 0.5 + r, currentPos.getY(), currentPos.getZ() + 0.75 + r)[0];
					event.pitch = getRotations(currentPos.getX() + 0.5 + r, currentPos.getY(), currentPos.getZ() + 0.75 + r)[1];
					
				}
				
				// West
				if (!(mc.theWorld.getBlockState(currentPos.add(1, 0, 0)).getBlock() instanceof BlockAir)) {
					
					currentPos = new BlockPos(mc.thePlayer.posX + 1, mc.thePlayer.posY - 1, mc.thePlayer.posZ);
					currentFacing = EnumFacing.WEST;
					if (getRotations(currentPos.getX() + 0.25 + r, currentPos.getY(), currentPos.getZ() + 0.5 + r) == null) {
						currentFacing = null;
						return null;
					}
					event.yaw = getRotations(currentPos.getX() + 0.25 + r, currentPos.getY(), currentPos.getZ() + 0.5 + r)[0];
					event.pitch = getRotations(currentPos.getX() + 0.25 + r, currentPos.getY(), currentPos.getZ() + 0.5 + r)[1];
					
				}
				
				// East
				if (!(mc.theWorld.getBlockState(currentPos.add(-1, 0, 0)).getBlock() instanceof BlockAir)) {
					
					currentPos = new BlockPos(mc.thePlayer.posX - 1, mc.thePlayer.posY - 1, mc.thePlayer.posZ);
					currentFacing = EnumFacing.EAST;
					if (getRotations(currentPos.getX() + 0.75 + r, currentPos.getY() + 0.25, currentPos.getZ() + 0.5 + r) == null) {
						currentFacing = null;
						return null;
					}
					event.yaw = getRotations(currentPos.getX() + 0.75 + r, currentPos.getY() + 0.25, currentPos.getZ() + 0.5 + r)[0];
					event.pitch = getRotations(currentPos.getX() + 0.75 + r, currentPos.getY() + 0.25, currentPos.getZ() + 0.5 + r)[1];
					
				}
				
			}
			
			return null;
			
		}
		
		return null;
		
		
	}
}
