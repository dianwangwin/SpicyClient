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
	
	private ModeSetting rotationMode = new ModeSetting("Rotation Mode", "90 degree snap", "90 degree snap", "test");
	private BooleanSetting keepRotations = new BooleanSetting("keepRotations", true);
	private BooleanSetting sprintEnabled = new BooleanSetting("Sprint", false);
	private ModeSetting sneakMode = new ModeSetting("Sneak Mode", "None", "None", "Always", "End of block");
	private ModeSetting blockFlyMode = new ModeSetting("Mode", "none", "none", "NCP");
	private NumberSetting speed = new NumberSetting("Speed", 100, 1, 100, 1);
	private NumberSetting pitch = new NumberSetting("Pitch", 89.8, 50, 90, 0.05);
	
	public BlockFly() {
		super("Block Fly", Keyboard.KEY_NONE, Category.BETA);
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
		RenderUtils.resetPlayerYaw();
		RenderUtils.resetPlayerPitch();
	}
	
	private BlockPos currentPos;
	private BlockPos playerPos;
	private EnumFacing currentFacing;
	
	private float oldYaw = -1000;
	private float oldPitch = -1000;
	
	public void onEvent(Event e) {
		if (mc.thePlayer.getCurrentEquippedItem() == null) {
			return;
		}
		if (e instanceof EventMotion && e.isPre()) {
			
			EventMotion event = (EventMotion) e;
			doStuff(event, 0.1D, 0.1D);
			doStuff(event, 0.25D, 0.25D);
			doStuff(event, 0D, 0D);
			doStuff(event, -0.1D, -0.1D);
			doStuff(event, -0.25D, -0.25D);
			doStuff(event, -1D, 0D);
			doStuff(event, 0D, -1D);
			doStuff(event, 1D, 0D);
			doStuff(event, 0D, 1D);
			
		}
		
	}
	
	public void doStuff(EventMotion event, Double offsetX, Double offsetZ) {
		
		currentPos = mc.thePlayer.getPosition().add(offsetX, -1, offsetZ);
		//currentPos = new BlockPos(((int)mc.thePlayer.posX), ((int)mc.thePlayer.posY), ((int)mc.thePlayer.posZ + 0.25));
		for (EnumFacing facing : EnumFacing.VALUES) {
			
			switch (facing) {
			case DOWN:
				if (mc.theWorld.getBlockState(currentPos.add(0, -1, 0)).getBlock().isBlockSolid(mc.theWorld, currentPos.add(0, -1, 0), EnumFacing.UP)) {
					float[] rots = getRotations(currentPos.add(0, -1, 0).getX(), currentPos.add(0, -1, 0).getY(), currentPos.add(0, -1, 0).getZ());
					if (rots == null) {
						endEarly();
						return;
					}
					event.setYaw(rots[0]);
					event.setPitch(rots[1]);
					RenderUtils.setCustomYaw(rots[0]);
					RenderUtils.setCustomPitch(rots[1]);
					currentFacing = EnumFacing.UP;
					
					placeBlock(currentPos.add(0, -1, 0), currentPos, currentFacing);
					return;
				}
				
				break;

			default:
				endEarly();
				return;
				
			}
			
		}
		
		endEarly();
		return;
		
	}
	
	public void endEarly() {
		
		RenderUtils.resetPlayerYaw();
		RenderUtils.resetPlayerPitch();
		
	}
	
	public boolean placeBlock(BlockPos pos1, BlockPos pos2, EnumFacing face) {
		
		if (mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, mc.thePlayer.getCurrentEquippedItem(), pos1, face, new Vec3(pos2.getX(), pos2.getY(), pos2.getZ()))) {
			mc.thePlayer.swingItem();
			System.out.println("Sent a block place packet");
			return true;
		}
		return false;
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
	
	private static transient float lastSmoothYaw, lastSmoothPitch;
	
	private void getSmoothRotations(EventMotion e, float targetYaw, float targetPitch) throws NullPointerException {
    	
    	// Value 0.25 to 10
        float yawFactor = 36000000;
        float pitchFactor = 90;
        
        // Value 0.01 to 1
        double xz = 0;
        double y = 0;
        
        if (targetYaw > 0 && targetYaw > yawFactor) {
            //mc.thePlayer.rotationYaw += yawFactor;
        	e.setYaw(this.lastSmoothYaw += yawFactor);
        } else if (targetYaw < 0 && targetYaw < -yawFactor) {
            //mc.thePlayer.rotationYaw -= yawFactor;
        	e.setYaw(this.lastSmoothYaw -= yawFactor);
        } else {
            //mc.thePlayer.rotationYaw += targetYaw;
            e.setYaw(this.lastSmoothYaw += targetYaw);
        }
        
        if (targetPitch > 0 && targetPitch > pitchFactor) {
            //mc.thePlayer.rotationPitch += pitchFactor;
        	e.setPitch(this.lastSmoothPitch += pitchFactor);
        } else if (targetPitch < 0 && targetPitch < -pitchFactor) {
            //mc.thePlayer.rotationPitch -= pitchFactor;
        	e.setPitch(this.lastSmoothPitch -= pitchFactor);
        } else {
            //mc.thePlayer.rotationPitch += targetPitch;
        	e.setPitch(this.lastSmoothPitch += targetPitch);
        }
        
        this.lastSmoothYaw = e.yaw;
        this.lastSmoothPitch = e.pitch;
        
        //mc.thePlayer.rotationYawHead = e.yaw;
        
    }
	
}
