package info.spicyclient.modules.movement;

import java.text.DecimalFormat;
import java.util.Comparator;
import java.util.Random;

import org.apache.commons.lang3.RandomUtils;
import org.lwjgl.input.Keyboard;
import org.newdawn.slick.tests.xml.ItemContainer;

import info.spicyclient.SpicyClient;
import info.spicyclient.chatCommands.Command;
import info.spicyclient.events.Event;
import info.spicyclient.events.listeners.EventGetBlockReach;
import info.spicyclient.events.listeners.EventMotion;
import info.spicyclient.events.listeners.EventReceivePacket;
import info.spicyclient.events.listeners.EventRender3D;
import info.spicyclient.events.listeners.EventRenderGUI;
import info.spicyclient.events.listeners.EventSendPacket;
import info.spicyclient.events.listeners.EventSneaking;
import info.spicyclient.events.listeners.EventUpdate;
import info.spicyclient.modules.Module;
import info.spicyclient.notifications.Color;
import info.spicyclient.notifications.NotificationManager;
import info.spicyclient.notifications.Type;
import info.spicyclient.settings.BooleanSetting;
import info.spicyclient.settings.ModeSetting;
import info.spicyclient.settings.NumberSetting;
import info.spicyclient.settings.SettingChangeEvent;
import info.spicyclient.settings.SettingChangeEvent.type;
import info.spicyclient.util.MovementUtils;
import info.spicyclient.util.RayTraceUtils;
import info.spicyclient.util.RenderUtils;
import info.spicyclient.util.RotationUtils;
import info.spicyclient.util.Timer;
import info.spicyclient.util.WorldUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.client.C0BPacketEntityAction.Action;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.network.play.server.S2FPacketSetSlot;
import net.minecraft.potion.Potion;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.util.Vec3;

public class BlockFly extends Module {
	
	public BlockFly() {
		super("Block Fly", Keyboard.KEY_NONE, Category.MOVEMENT);
		resetSettings();
	}
	
	public static transient BlockPos lastPlace = null;
	
	public NumberSetting extend = new NumberSetting("Extend", 0.1, 0.1, 5, 0.1),
			timerBoost = new NumberSetting("Timer Boost", 1, 1, 2, 0.01);
	public BooleanSetting keepY = new BooleanSetting("Keep Y", true),
			sprint = new BooleanSetting("Sprint", true);
	
	private static transient double keepPosY = 0;
	
	@Override
	public void resetSettings() {
		
		this.settings.clear();
		//extend.setValue(0.1);
		if (timerBoost == null) {
			timerBoost = new NumberSetting("Timer Boost", 1, 1, 2, 0.01);
		}
		
		if (sprint == null) {
			sprint = new BooleanSetting("Sprint", false);
		}
		
		this.addSettings(extend, keepY, timerBoost, sprint);
		
	}
	
	public void onEnable() {
		
		lastYaw = mc.thePlayer.rotationYaw;
		lastPitch = mc.thePlayer.rotationPitch;
		lastSlot = -1;
		keepPosY = mc.thePlayer.posY - 1;
		
	}
	
	public void onDisable() {
		mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
		lastSlot = -1;
		mc.timer.timerSpeed = 1;
	}
	
	public static transient float lastYaw = 0, lastPitch = 0;
	public static transient BlockPos lastBlockPos = null;
	public static transient EnumFacing lastFacing = null;
	public static transient Timer timer = new Timer();
	public static transient int lastSlot = -1;
	
	public void onEvent(Event e) {
		
		if (e instanceof EventRenderGUI && e.isPre()) {
			
			int blocksLeft = 0;
			
			for (short g = 0; g < 9; g++) {
				
				if (mc.thePlayer.inventoryContainer.getSlot(g + 36).getHasStack()
						&& mc.thePlayer.inventoryContainer.getSlot(g + 36).getStack().getItem() instanceof ItemBlock
						&& mc.thePlayer.inventoryContainer.getSlot(g + 36).getStack().stackSize != 0
						&& !((ItemBlock) mc.thePlayer.inventoryContainer.getSlot(g + 36).getStack().getItem()).getBlock()
								.getLocalizedName().toLowerCase().contains("chest")
						&& !((ItemBlock) mc.thePlayer.inventoryContainer.getSlot(g + 36).getStack().getItem()).getBlock()
								.getLocalizedName().toLowerCase().contains("table")) {
					blocksLeft += mc.thePlayer.inventoryContainer.getSlot(g + 36).getStack().stackSize;
				}
				
			}
			
			String left = blocksLeft + " block" + (blocksLeft != 1 ? "s" : "") + " left";
			
			if (blocksLeft > 0) {
				
				mc.fontRendererObj.drawString(left,
						((float) (new ScaledResolution(mc).getScaledWidth_double() / 2)
								- (mc.fontRendererObj.getStringWidth(left) / 2)),
						((float) (new ScaledResolution(mc).getScaledHeight_double() / 2)
								- (mc.fontRendererObj.FONT_HEIGHT - 18)),
						-1, false);

			} else {
				mc.fontRendererObj.drawString(left,
						((float) (new ScaledResolution(mc).getScaledWidth_double() / 2)
								- (mc.fontRendererObj.getStringWidth(left) / 2)),
						((float) (new ScaledResolution(mc).getScaledHeight_double() / 2)
								- (mc.fontRendererObj.FONT_HEIGHT - 18)),
						0xff2121, false);
			}
			
		}
		
		if (e instanceof EventSneaking) {
			
			if (e.isPre()) {
				
				if (SpicyClient.config.killaura.isEnabled() && SpicyClient.config.killaura.target != null) {
					return;
				}
				
				EventSneaking sneak = (EventSneaking) e;
				
				if (sneak.entity.onGround && sneak.entity instanceof EntityPlayer) {
					sneak.sneaking = true;
				}else {
					sneak.sneaking = false;
				}
				sneak.offset = -1D;
				sneak.revertFlagAfter = true;
				
			}
			
		}
		
		if (e instanceof EventReceivePacket && e.isPre()) {
			
			if (SpicyClient.config.killaura.isEnabled() && SpicyClient.config.killaura.target != null) {
				return;
			}
			
			if (((EventReceivePacket)e).packet instanceof S2FPacketSetSlot) {
				lastSlot = ((S2FPacketSetSlot)((EventReceivePacket)e).packet).slot;
				//e.setCanceled(true);
			}
			
		}
		
		if (e instanceof EventSendPacket & e.isPre()) {
			
			if (SpicyClient.config.killaura.isEnabled() && SpicyClient.config.killaura.target != null) {
				return;
			}
			
			if (((EventSendPacket)e).packet instanceof C09PacketHeldItemChange) {
				lastSlot = ((C09PacketHeldItemChange)((EventSendPacket)e).packet).getSlotId();
			}
			
		}
		
		if (e instanceof EventRender3D && e.isPre()) {
			
			BlockPos below = lastPlace;
			
			if (below == null) {
				return;
			}
			
			for (short i = 0; i < 5; i++) {
				
				RenderUtils.drawLine(below.getX(), below.getY(), below.getZ(), below.getX() + 1, below.getY(), below.getZ());
				RenderUtils.drawLine(below.getX(), below.getY() + 1, below.getZ(), below.getX() + 1, below.getY() + 1, below.getZ());
				RenderUtils.drawLine(below.getX(), below.getY(), below.getZ(), below.getX(), below.getY(), below.getZ() + 1);
				RenderUtils.drawLine(below.getX(), below.getY() + 1, below.getZ(), below.getX(), below.getY() + 1, below.getZ() + 1);
				RenderUtils.drawLine(below.getX(), below.getY(), below.getZ(), below.getX(), below.getY() + 1, below.getZ());
				RenderUtils.drawLine(below.getX(), below.getY() + 1, below.getZ(), below.getX(), below.getY() + 1, below.getZ());
				RenderUtils.drawLine(below.getX() + 1, below.getY(), below.getZ(), below.getX() + 1, below.getY() + 1, below.getZ());
				RenderUtils.drawLine(below.getX() + 1, below.getY() + 1, below.getZ(), below.getX() + 1, below.getY() + 1, below.getZ());
				RenderUtils.drawLine(below.getX(), below.getY(), below.getZ() + 1, below.getX(), below.getY() + 1, below.getZ() + 1);
				RenderUtils.drawLine(below.getX(), below.getY() + 1, below.getZ() + 1, below.getX(), below.getY() + 1, below.getZ() + 1);
				RenderUtils.drawLine(below.getX() + 1, below.getY(), below.getZ() + 1, below.getX(), below.getY(), below.getZ() + 1);
				RenderUtils.drawLine(below.getX() + 1, below.getY() + 1, below.getZ() + 1, below.getX(), below.getY() + 1, below.getZ() + 1);
				RenderUtils.drawLine(below.getX() + 1, below.getY(), below.getZ() + 1, below.getX() + 1, below.getY() + 1, below.getZ() + 1);
				RenderUtils.drawLine(below.getX() + 1, below.getY() + 1, below.getZ(), below.getX() + 1, below.getY() + 1, below.getZ() + 1);
				RenderUtils.drawLine(below.getX() + 1, below.getY(), below.getZ(), below.getX() + 1, below.getY(), below.getZ() + 1);
				
			}
			
		}
		
		if (e instanceof EventUpdate && e.isPre()) {
			
			// Event
			EventUpdate event = (EventUpdate)e;
			
			// prevents flags on hypixel
			if (!sprint.isEnabled()) {
				mc.thePlayer.setSprinting(false);
			}
			
			// Faster
			mc.timer.timerSpeed = ((float)timerBoost.getValue());
			
			// KeepY
			if (MovementUtils.isOnGround(0.00001)) {
				keepPosY = ((int)mc.thePlayer.posY) - 1;
			}
			
			// prevents flags on hypixel
			/*
			lastYaw += new Random().nextInt(30) - 15;
			lastPitch += new Random().nextInt(30) - 15;
			lastYaw = MathHelper.wrapAngleTo180_float(lastYaw);
			if (lastPitch >= 90) {
				lastPitch = 90;
			}
			else if (lastPitch <= -90) {
				lastPitch = -90;
			}
			*/
			
			// Keep rotations
			if (lastBlockPos != null && lastFacing != null && MovementUtils.isOnGround(0.5)) {
				float[] keepRots = getRotationsHypixel(lastBlockPos, lastFacing);
				lastYaw = keepRots[0];
				lastPitch = keepRots[1];
			}
			
			event.setYaw(lastYaw);
			event.setPitch(lastPitch);
			
			RenderUtils.setCustomYaw(lastYaw);
			RenderUtils.setCustomPitch(lastPitch);
			
			// Finds the block that the player will break
			BlockPos targetPos = null;
			
			// No extend
			if (extend.getValue() == 0.1) {
				
				targetPos = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1, mc.thePlayer.posZ);
				
				if (keepY.isEnabled() && !(mc.thePlayer.posY - 1 < keepPosY)) {
					targetPos.y = (int) keepPosY;
				}
				
				if (mc.theWorld.getBlockState(targetPos).getBlock() != Blocks.air) {
					targetPos = null;
				}
				
			}
			// Extend
			else {
				
				for (double extend = 0; extend <= this.extend.getValue(); extend += 0.1) {
					BlockPos temp = WorldUtils.getForwardBlock(extend).add(0, -1, 0);
					
					if (keepY.isEnabled() && !(mc.thePlayer.posY - 1 < keepPosY)) {
						temp.y = (int) keepPosY;
					}
					
					if (mc.theWorld.getBlockState(temp).getBlock() == Blocks.air) {
						targetPos = temp;
						break;
					}
					if (!MovementUtils.isMoving()) {
						break;
					}
				}
				
			}
			
			// Checks how many blocks you have
			int blocksLeft = 0;
			
			for (short g = 0; g < 9; g++) {
				
				if (mc.thePlayer.inventoryContainer.getSlot(g + 36).getHasStack()
						&& mc.thePlayer.inventoryContainer.getSlot(g + 36).getStack().getItem() instanceof ItemBlock
						&& mc.thePlayer.inventoryContainer.getSlot(g + 36).getStack().stackSize != 0
						&& !((ItemBlock) mc.thePlayer.inventoryContainer.getSlot(g + 36).getStack().getItem()).getBlock()
								.getLocalizedName().toLowerCase().contains("chest")
						&& !((ItemBlock) mc.thePlayer.inventoryContainer.getSlot(g + 36).getStack().getItem()).getBlock()
								.getLocalizedName().toLowerCase().contains("table")) {
					blocksLeft += mc.thePlayer.inventoryContainer.getSlot(g + 36).getStack().stackSize;
				}
				
			}
			
			// If it shouldn't place a block then don't try to
			if (targetPos == null || blocksLeft == 0) {
				return;
			}
			
			// For rendering
			lastPlace = targetPos;
			
			// Sets the item to hold
			ItemStack block = setStackToPlace();
			
			// Finds a block to place on
			BlockInfo info = findFacingAndBlockPosForBlock(targetPos);
			
			// Returns if it cannot find a block to place on
			if (info == null) {
				return;
			}
			
			// Places the block and sets the rots
			float[] rots = getRotationsHypixel(info.pos, info.facing);
			event.setYaw(rots[0] + RandomUtils.nextFloat(4, 10));
			event.setPitch(rots[1]);
			
//			This flags
//			if (extend.getValue() == 0.1) {
//                event.setYaw(mc.thePlayer.rotationYaw + RandomUtils.nextFloat(174, 179));
//                if (mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
//                	event.setPitch(70f);
//                }else {
//                	event.setPitch(90f);
//                }
//			}
			
			RenderUtils.setCustomYaw(event.yaw);
			RenderUtils.setCustomPitch(event.pitch);
			lastYaw = event.yaw;
			lastPitch = event.pitch;
			mc.playerController.onPlayerRightClickNoSync(mc.thePlayer, mc.theWorld, block, info.pos, info.facing, RotationUtils.getVectorForRotation(rots[1], rots[0]));
			mc.getNetHandler().getNetworkManager().sendPacket(new C0APacketAnimation());
			
		}
		
	}

	private BlockInfo findFacingAndBlockPosForBlock(BlockPos input) {

		if (SpicyClient.config.inventoryManager.isInventoryOpen) {
			//mc.thePlayer.sendQueue.addToSendQueue(new C0DPacketCloseWindow());
		}
		
		BlockInfo output = new BlockInfo();
		output.pos = input;
		
		// One block
		for (EnumFacing face : EnumFacing.VALUES) {
			
			if (mc.theWorld.getBlockState(output.pos.offset(face)).getBlock() != Blocks.air && shouldCancelCheck(face)) {

				output.pos = output.pos.offset(face);
				output.facing = face.getOpposite();
				output.targetPos = new BlockPos(input.getX(), input.getY(), input.getZ());
				if (keepY.isEnabled() && mc.thePlayer.posY - 1 >= keepPosY) {
					output.pos.y = (int) keepPosY;
					output.targetPos.y = (int) keepPosY;
				}
				return output;

			}

		}

		// Two blocks
		for (EnumFacing face : EnumFacing.VALUES) {

			if (mc.theWorld.getBlockState(output.pos.offset(face)).getBlock() == Blocks.air) {

				for (EnumFacing face1 : EnumFacing.VALUES) {

					if (mc.theWorld.getBlockState(output.pos.offset(face).offset(face1)).getBlock() != Blocks.air && shouldCancelCheck(face1)) {

						output.pos = output.pos.offset(face).offset(face1);
						output.facing = face.getOpposite();
						output.targetPos = output.pos.offset(face);
						if (keepY.isEnabled() && mc.thePlayer.posY - 1 >= keepPosY) {
							output.pos.y = (int) keepPosY;
							output.targetPos.y = (int) keepPosY;
						}
						return output;

					}

				}

			}

		}

		// Three blocks
		for (EnumFacing face2 : EnumFacing.VALUES) {

			for (EnumFacing face : EnumFacing.VALUES) {

				if (mc.theWorld.getBlockState(output.pos.offset(face).offset(face2)).getBlock() == Blocks.air) {

					for (EnumFacing face1 : EnumFacing.VALUES) {

						if (mc.theWorld.getBlockState(output.pos.offset(face).offset(face1).offset(face2))
								.getBlock() != Blocks.air && shouldCancelCheck(face1)) {

							output.pos = output.pos.offset(face).offset(face1).offset(face2);
							output.facing = face2.getOpposite();
							output.targetPos = output.pos.offset(face).offset(face2);
							if (keepY.isEnabled() && mc.thePlayer.posY - 1 >= keepPosY) {
								output.pos.y = (int) keepPosY;
								output.targetPos.y = (int) keepPosY;
							}
							return output;

						}

					}

				}

			}

		}

		// Four blocks
		for (EnumFacing face3 : EnumFacing.VALUES) {

			for (EnumFacing face2 : EnumFacing.VALUES) {

				for (EnumFacing face : EnumFacing.VALUES) {

					if (mc.theWorld.getBlockState(output.pos.offset(face).offset(face2).offset(face3))
							.getBlock() == Blocks.air) {

						for (EnumFacing face1 : EnumFacing.VALUES) {

							if (mc.theWorld
									.getBlockState(output.pos.offset(face).offset(face1).offset(face2).offset(face3))
									.getBlock() != Blocks.air && shouldCancelCheck(face1)) {

								output.pos = output.pos.offset(face).offset(face1).offset(face2).offset(face3);
								output.facing = face3.getOpposite();
								output.targetPos = output.pos.offset(face).offset(face2).offset(face3);
								if (keepY.isEnabled() && mc.thePlayer.posY - 1 >= keepPosY) {
									output.pos.y = (int) keepPosY;
									output.targetPos.y = (int) keepPosY;
								}
								return output;

							}

						}

					}

				}

			}

		}

		return null;

	}

	public float[] getRotationsHypixel(BlockPos paramBlockPos, EnumFacing paramEnumFacing) {
		
		double offsetX = 0, offsetZ = 0;
		
		offsetX = (double)paramEnumFacing.getFrontOffsetX() / 2.0D;
		offsetZ = (double)paramEnumFacing.getFrontOffsetZ() / 2.0D;
		
		if (paramEnumFacing.getFrontOffsetX() == 0 && paramEnumFacing.getFrontOffsetZ() == -1) {
			offsetZ = 0.5;
		}
		else if (paramEnumFacing.getFrontOffsetX() == -1 && paramEnumFacing.getFrontOffsetZ() == 0) {
			offsetX = 0.5;
		}
		
		lastBlockPos = paramBlockPos;
		lastFacing = paramEnumFacing;
		
        double d1 = (double)paramBlockPos.getX() - mc.thePlayer.posX + offsetX + 0.5;
        double d2 = (double)paramBlockPos.getZ() - mc.thePlayer.posZ + offsetZ + 0.5;
        double d3 = mc.thePlayer.posY + (double)mc.thePlayer.getEyeHeight() - ((double)paramBlockPos.getY() + ((double)paramEnumFacing.getFrontOffsetZ() / 2.0D));
        double d4 = (double)MathHelper.sqrt_double(d1 * d1 + d2 * d2);
        float f1 = (float)(Math.atan2(d2, d1) * 180.0D / 3.141592653589793D) - 90.0F;
        float f2 = (float)(Math.atan2(d3, d4) * 180.0D / 3.141592653589793D);
        if (f1 < 0.0F) {
            //f1 += 360.0F;
        }
        
        f1 = MathHelper.wrapAngleTo180_float(f1);
        f2 = MathHelper.wrapAngleTo180_float(f2);
        
        if (f2 > 90)
        	f2 = 89f + new Random().nextFloat();
        
        if (f2 < -90)
        	f2 = -89 + (new Random().nextFloat() * -1);
        
        return new float[]{f1, f2};
        
		
		//return (new Random().nextBoolean()) ? new float[] {mc.thePlayer.rotationYaw + 180, 83} : new float[] {mc.thePlayer.rotationYaw, 85};
		//return new float[] {mc.thePlayer.rotationYaw, 85};
		
//		paramBlockPos = paramBlockPos.offset(paramEnumFacing.getOpposite());
//
//		lastPos = new Vec3(paramBlockPos.getX(), paramBlockPos.getY(), paramBlockPos.getZ());
//
//		return RotationUtils.getRotationFromPosition(lastPos.xCoord, lastPos.zCoord, lastPos.yCoord);

	}
	
	public static ItemStack setStackToPlace() {
		
		ItemStack block = mc.thePlayer.getCurrentEquippedItem();
		
		if (block != null && block.getItem() != null && !(block.getItem() instanceof ItemBlock)) {
			block = null;
		}
		
		int slot = mc.thePlayer.inventory.currentItem;
		
		for (short g = 0; g < 9; g++) {
			
			if (mc.thePlayer.inventoryContainer.getSlot(g + 36).getHasStack()
					&& mc.thePlayer.inventoryContainer.getSlot(g + 36).getStack().getItem() instanceof ItemBlock
					&& mc.thePlayer.inventoryContainer.getSlot(g + 36).getStack().stackSize != 0
					&& !((ItemBlock) mc.thePlayer.inventoryContainer.getSlot(g + 36).getStack().getItem()).getBlock()
							.getLocalizedName().toLowerCase().contains("chest")
					&& !((ItemBlock) mc.thePlayer.inventoryContainer.getSlot(g + 36).getStack().getItem()).getBlock()
							.getLocalizedName().toLowerCase().contains("table")
					&& (block == null
					|| (block.getItem() instanceof ItemBlock && mc.thePlayer.inventoryContainer.getSlot(g + 36).getStack().stackSize >= block.stackSize))) {
				
				//mc.thePlayer.inventory.currentItem = g;
				slot = g;
				block = mc.thePlayer.inventoryContainer.getSlot(g + 36).getStack();
				
			}
			
		}
		if (lastSlot != slot) {
			mc.getNetHandler().getNetworkManager().sendPacket(new C09PacketHeldItemChange(slot));
			lastSlot = slot;
		}
		return block;
	}
	
	public boolean shouldCancelCheck(EnumFacing face) {
		if (keepY.isEnabled() && mc.thePlayer.posY - 1 >= keepPosY) {
			return !(face == EnumFacing.UP);
		}else {
			return true;
		}
	}
	
	private class BlockInfo {
		
		BlockPos pos, targetPos;
		EnumFacing facing;
		
	}
	
}