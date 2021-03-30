package info.spicyclient.modules.movement;

import java.text.DecimalFormat;
import java.util.Comparator;
import java.util.Random;

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
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

public class BlockFly extends Module {
	
	public BlockFly() {
		super("Block Fly", Keyboard.KEY_NONE, Category.MOVEMENT);
		resetSettings();
	}
	
	public static transient BlockPos lastPlace = null;
	
	public NumberSetting extend = new NumberSetting("Extend", 2.5, 0.1, 5, 0.1),
			xOffset = new NumberSetting("X Offset", 0, 0, 1, 0.05),
			zOffset = new NumberSetting("Z Offset", 0, 0, 1, 0.05);
	public BooleanSetting keepY = new BooleanSetting("Keep Y", false),
			hypixel = new BooleanSetting("Hypixel", false);
	
	private static transient double keepPosY = 0;
	
	@Override
	public void resetSettings() {
		
		this.settings.clear();
		//extend.setValue(0.1);
		this.addSettings(extend, keepY);
		
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
	}
	
	@Override
	public void onSettingChange(SettingChangeEvent e) {
		
		if (e.setting == hypixel) {
			
			if (hypixel.isEnabled()) {
				if (settings.contains(extend)) {
					settings.remove(extend);
				}
				extend.setValue(0.1);
			}else {
				if (!settings.contains(extend)) {
					settings.add(extend);
				}
				if (!settings.contains(keepY)) {
					settings.add(keepY);
				}
			}
			
			reorderSettings();
			
		}
		
	}
	
	public static transient float lastYaw = 0, lastPitch = 0;
	public Vec3 lastPos = null;
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
				e.setCanceled(true);
			}
			
		}
		
		if (e instanceof EventSendPacket & e.isPre()) {
			
			if (SpicyClient.config.killaura.isEnabled() && SpicyClient.config.killaura.target != null) {
				return;
			}
			
			if (((EventSendPacket)e).packet instanceof C03PacketPlayer) {
				((C03PacketPlayer)((EventSendPacket)e).packet).setYaw(lastYaw);
				((C03PacketPlayer)((EventSendPacket)e).packet).setPitch(lastPitch);
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
		
		if (e instanceof EventUpdate && e.isPre() && MovementUtils.isOnGround(0.4)) {
			
			if (((int)mc.thePlayer.posY) - 1 < keepPosY) {
				keepPosY = ((int)mc.thePlayer.posY) - 3;
			}
			
			if (MovementUtils.isOnGround(0.00001)) {
				keepPosY = ((int)mc.thePlayer.posY) - 1;
				//Command.sendPrivateChatMessage(keepPosY);
			}
			
			if (SpicyClient.config.killaura.isEnabled() && SpicyClient.config.killaura.target != null) {
				return;
			}
			
			//mc.thePlayer.setSprinting(false);
			
			//mc.thePlayer.onGround = false;
			
			RenderUtils.setCustomYaw(lastYaw);
			RenderUtils.setCustomPitch(lastPitch);
			
			EventUpdate update = (EventUpdate)e;
			
		}
		
		if (e instanceof EventMotion && e.isPost()) {
			
			if (SpicyClient.config.killaura.isEnabled() && SpicyClient.config.killaura.target != null) {
				return;
			}
			
			EventMotion event = (EventMotion) e;
			
			//mc.thePlayer.setSprinting(false);
			
			event.setYaw(lastYaw);
			event.setYaw(lastPitch);
			
			if (lastPos != null) {
				
				event.setYaw(RotationUtils.getRotationFromPosition(lastPos.xCoord,
						lastPos.zCoord, lastPos.yCoord)[0]);
				
				event.setPitch(RotationUtils.getRotationFromPosition(lastPos.xCoord,
						lastPos.zCoord, lastPos.yCoord)[1]);
				
				lastYaw = event.yaw;
				lastPitch = event.pitch;
				
				RenderUtils.setCustomYaw(lastYaw);
				RenderUtils.setCustomPitch(lastPitch);
				
			}
			
			ItemStack i = mc.thePlayer.getCurrentEquippedItem();
			BlockPos below = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1D, mc.thePlayer.posZ);
			
			boolean shouldPlace = false;
			
			for (double h = 0; h < extend.getValue(); h += 0.1) {
				below = WorldUtils.getForwardBlock(h).add(0, -1, 0);
				if (below == null || mc.theWorld.getBlockState(below).getBlock() == Blocks.air) {
					shouldPlace = true;
				}
			}
			
			if (shouldPlace) {
				
				if (!timer.hasTimeElapsed(80, true)) {
					if (extend.getValue() > 0.5) {
						return;
					}
				}
				
				for (EnumFacing facing : EnumFacing.VALUES) {
					
					switch (facing) {
					case UP:
						
						for (double k = 0; k < extend.getValue(); k += 0.1) {
							BlockPos underBelow = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 2D, mc.thePlayer.posZ);
							if (MovementUtils.isMoving()) {
								underBelow = WorldUtils.getForwardBlock(k).add(0, -2, 0);
							}
							
							if (mc.theWorld.getBlockState(underBelow).getBlock() != Blocks.air) {
								
								ItemStack block = setStackToPlace();
								if (block == null || block.getItem() == null || !(block.getItem() instanceof ItemBlock)) {
									return;
								}
								
								if (keepY.isEnabled() && underBelow.y != keepPosY - 1) {
									break;
								}
								
								//mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C08PacketPlayerBlockPlacement(underBelow, EnumFacing.UP.getIndex(), block, 0, 0, 0));
								if (mc.playerController.onPlayerRightClickNoSync(mc.thePlayer, mc.theWorld, block,
										underBelow, EnumFacing.UP,
										RotationUtils.getVectorForRotation(
												getRotationsHypixel(underBelow, EnumFacing.UP)[1],
												getRotationsHypixel(underBelow, EnumFacing.UP)[1]))
										&& block != null && block.getItem() != null
										&& block.getItem() instanceof ItemBlock) {
									mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C0APacketAnimation());
									event.setYaw(getRotationsHypixel(underBelow, facing)[0]);
									event.setPitch(getRotationsHypixel(underBelow, facing)[1]);
									lastYaw = event.yaw;
									lastPitch = event.pitch;
									RenderUtils.setCustomYaw(lastYaw);
									RenderUtils.setCustomPitch(lastPitch);
									lastPlace = underBelow.add(0, 1, 0);
									//mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
									return;
								}
								
							}
						}
						
						break;
					case NORTH:
					case EAST:
					case SOUTH:
					case WEST:
						
						for (double k = 0; k < extend.getValue(); k += 0.1) {
							BlockPos keepY = WorldUtils.getForwardBlock(k).add(0, -1, 0);
							if (this.keepY.isEnabled()) {
								keepY.y = (int) keepPosY;
							}
							BlockInfo defaultPos = findFacingAndBlockPosForBlock(keepY);
							
							if (defaultPos == null)
								return;
							
							if (mc.theWorld.getBlockState(defaultPos.pos).getBlock() != Blocks.air) {
								
								ItemStack block = setStackToPlace();
								if (block == null || block.getItem() == null || !(block.getItem() instanceof ItemBlock)) {
									return;
								}
								
								boolean endAfter = false;
								
								if (mc.theWorld.getBlockState(WorldUtils.getForwardBlock(k).add(0, -1, 0)).getBlock() == Blocks.air) {
									endAfter = true;
								}
								
								if (this.keepY.isEnabled()) {
									if (defaultPos.facing == EnumFacing.UP || defaultPos.facing == EnumFacing.DOWN) {
										break;
									}
								}
								
								//mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C08PacketPlayerBlockPlacement(defaultPos.pos, defaultPos.facing.getIndex(), block, 0, 0, 0));
								if (defaultPos.pos.y != keepPosY - 1 && (mc.thePlayer.motionX != 0 || mc.thePlayer.motionZ != 0
										|| (mc.thePlayer.motionY != 0 && mc.thePlayer.fallDistance > 2))
										&& block != null && block.getItem() != null
										&& block.getItem() instanceof ItemBlock) {
									if (mc.playerController.onPlayerRightClickNoSync(mc.thePlayer, mc.theWorld, block,
											defaultPos.pos, defaultPos.facing,
											RotationUtils.getVectorForRotation(
													getRotationsHypixel(defaultPos.pos.offset(defaultPos.facing), defaultPos.facing)[0],
													getRotationsHypixel(defaultPos.pos.offset(defaultPos.facing), defaultPos.facing)[1]))) {
										//mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C0APacketAnimation());
										mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C0APacketAnimation());
										event.setYaw(getRotationsHypixel(defaultPos.pos.offset(defaultPos.facing), defaultPos.facing)[0]);
										event.setPitch(getRotationsHypixel(defaultPos.pos.offset(defaultPos.facing), defaultPos.facing)[1]);
										lastYaw = event.yaw;
										lastPitch = event.pitch;
										RenderUtils.setCustomYaw(lastYaw);
										RenderUtils.setCustomPitch(lastPitch);
										lastPlace = defaultPos.targetPos;
										//mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
										if (endAfter) {
											return;
										}
									}
								}
								
							}else {
								RenderUtils.setCustomYaw(lastYaw);
								RenderUtils.setCustomPitch(lastPitch);
								event.setYaw(lastYaw);
								event.setPitch(lastPitch);
							}
						}
						
						break;
						
					}
					
				}
				
			}
			
		}
		
	}

	private BlockInfo findFacingAndBlockPosForBlock(BlockPos input) {

		if (SpicyClient.config.inventoryManager.isInventoryOpen) {
			mc.thePlayer.sendQueue.addToSendQueue(new C0DPacketCloseWindow());
		}
		
		BlockInfo output = new BlockInfo();
		output.pos = input;
		
		// One block
		for (EnumFacing face : EnumFacing.VALUES) {

			if (mc.theWorld.getBlockState(output.pos.offset(face)).getBlock() != Blocks.air) {

				output.pos = output.pos.offset(face);
				output.facing = face.getOpposite();
				output.targetPos = new BlockPos(input.getX(), input.getY(), input.getZ());
				if (keepY.isEnabled()) {
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

					if (mc.theWorld.getBlockState(output.pos.offset(face).offset(face1)).getBlock() != Blocks.air) {

						output.pos = output.pos.offset(face).offset(face1);
						output.facing = face.getOpposite();
						output.targetPos = output.pos.offset(face);
						if (keepY.isEnabled()) {
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
								.getBlock() != Blocks.air) {

							output.pos = output.pos.offset(face).offset(face1).offset(face2);
							output.facing = face2.getOpposite();
							output.targetPos = output.pos.offset(face).offset(face2);
							if (keepY.isEnabled()) {
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
									.getBlock() != Blocks.air) {

								output.pos = output.pos.offset(face).offset(face1).offset(face2).offset(face3);
								output.facing = face3.getOpposite();
								output.targetPos = output.pos.offset(face).offset(face2).offset(face3);
								if (keepY.isEnabled()) {
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
		
		/*
		double offsetX = 0.6, offsetZ = 0.4;
		
        double d1 = (double)paramBlockPos.getX() + offsetX - mc.thePlayer.posX + (double)paramEnumFacing.getFrontOffsetX() / 2.0D;
        double d2 = (double)paramBlockPos.getZ() + offsetZ - mc.thePlayer.posZ + (double)paramEnumFacing.getFrontOffsetZ() / 2.0D;
        double d3 = mc.thePlayer.posY + (double)mc.thePlayer.getEyeHeight() - ((double)paramBlockPos.getY() + 0.5D);
        double d4 = (double)MathHelper.sqrt_double(d1 * d1 + d2 * d2);
        float f1 = (float)(Math.atan2(d2, d1) * 180.0D / 3.141592653589793D) - 90.0F;
        float f2 = (float)(Math.atan2(d3, d4) * 180.0D / 3.141592653589793D);
        if (f1 < 0.0F) {
            f1 += 360.0F;
        }
        
        //f1 += 180;
        //f1 = MathHelper.wrapAngleTo180_float(f1);
        //f2 += 30;
        //f2 -= 30;
        f2 += 25 + new Random().nextInt(5);
        
        if (f2 > 90)
        	f2 = 89f + new Random().nextFloat();
        
        if (f2 < -90)
        	f2 = -89 + (new Random().nextFloat() * -1);
        
        //Command.sendPrivateChatMessage(f1 + " : " + f2);
        
        return new float[]{f1, f2};
        
		
		//return (new Random().nextBoolean()) ? new float[] {mc.thePlayer.rotationYaw + 180, 83} : new float[] {mc.thePlayer.rotationYaw, 85};
		//return new float[] {mc.thePlayer.rotationYaw, 85};
		*/
		
		paramBlockPos = paramBlockPos.offset(paramEnumFacing.getOpposite());
		
		lastPos = new Vec3(paramBlockPos.getX() + new Random().nextDouble(), paramBlockPos.getY(), paramBlockPos.getZ() + new Random().nextDouble());
		
		return RotationUtils.getRotationFromPosition(lastPos.xCoord,
				lastPos.zCoord, lastPos.yCoord);
		
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
			mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C09PacketHeldItemChange(slot));
			lastSlot = slot;
		}
		return block;
	}
	
	private class BlockInfo {
		
		BlockPos pos, targetPos;
		EnumFacing facing;
		
	}
	
}