package info.spicyclient.modules.player;

import java.util.Random;

import org.lwjgl.input.Keyboard;

import info.spicyclient.SpicyClient;
import info.spicyclient.bypass.Hypixel;
import info.spicyclient.chatCommands.Command;
import info.spicyclient.events.Event;
import info.spicyclient.events.listeners.EventMotion;
import info.spicyclient.events.listeners.EventUpdate;
import info.spicyclient.modules.Module;
import info.spicyclient.notifications.Color;
import info.spicyclient.notifications.NotificationManager;
import info.spicyclient.notifications.Type;
import info.spicyclient.settings.ModeSetting;
import info.spicyclient.util.InventoryUtils;
import info.spicyclient.util.MovementUtils;
import info.spicyclient.util.RenderUtils;
import info.spicyclient.util.RotationUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemEnderPearl;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.client.C0CPacketBoatInput;
import net.minecraft.network.play.client.C13PacketPlayerAbilities;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public class NoFall extends Module {
	
	public ModeSetting noFallMode = new ModeSetting("NoFall Mode", "Vanilla", "Vanilla", "Packet", "Hypixel");
	
	public NoFall() {
		super("No Fall", Keyboard.KEY_NONE, Category.PLAYER);
		resetSettings();
	}
	
	@Override
	public void resetSettings() {
		this.settings.clear();
		this.addSettings(noFallMode);
	}
	
	public void onEnable() {
		hasLandedAfterFly = true;
	}
	
	public void onDisable() {
		
	}
	
	public static transient boolean hasLandedAfterFly = true;
	
	public void onEvent(Event e) {
		
		if (e instanceof EventMotion && e.isPre() && noFallMode.is("Vanilla") && mc.thePlayer.fallDistance > 3) {
			
			EventMotion event = (EventMotion) e;
			event.onGround = true;
			
		}
		else if (e instanceof EventMotion && e.isPre() && mc.thePlayer.fallDistance >= 3 && noFallMode.is("Hypixel") && !SpicyClient.config.fly.isEnabled()) {
			
			for (double down = 0; down < 1; down += 0.01) {
				
				if (mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ).add(0, -down, 0)).getBlock() != Blocks.air && hasLandedAfterFly) {
					EventMotion event = (EventMotion)e;
					float[] rots = RotationUtils.getRotationFromPosition(mc.thePlayer.posX, mc.thePlayer.posY - down, mc.thePlayer.posZ);
					//mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C03PacketPlayer.C05PacketPlayerLook(rots[0], rots[1], true));
					//event.yaw = RotationUtils.getRotationFromPosition(mc.thePlayer.posX, mc.thePlayer.posY - down, mc.thePlayer.posZ)[0];
					//event.pitch = RotationUtils.getRotationFromPosition(mc.thePlayer.posX, mc.thePlayer.posY - down, mc.thePlayer.posZ)[1];
					//RenderUtils.setCustomYaw(rots[0]);
					//RenderUtils.setCustomPitch(rots[1]);
					
					for (short i = 0; i < 45; i++) {
						
						if (Minecraft.getMinecraft().thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
							ItemStack is = Minecraft.getMinecraft().thePlayer.inventoryContainer.getSlot(i).getStack();
							
							if (is.getItem() instanceof ItemBlock) {
								
								int heldItemBeforeThrow = mc.thePlayer.inventory.currentItem;
								if (i - 36 < 0) {
									
									InventoryUtils.swap(i, 8);
									
									Minecraft.getMinecraft().getNetHandler().getNetworkManager()
											.sendPacketNoEvent(new C09PacketHeldItemChange(8));
									
								}else {
									
									Minecraft.getMinecraft().getNetHandler().getNetworkManager()
											.sendPacketNoEvent(new C09PacketHeldItemChange(i - 36));
									
								}
								
								Minecraft.getMinecraft().getNetHandler().getNetworkManager()
										.sendPacketNoEvent(new C03PacketPlayer.C05PacketPlayerLook(
												Minecraft.getMinecraft().thePlayer.rotationYaw, 88.99f + new Random().nextFloat(),
												MovementUtils.isOnGround(0.0001)));
								AxisAlignedBB oldBox = mc.thePlayer.getEntityBoundingBox();
								mc.thePlayer.boundingBox = new AxisAlignedBB(mc.thePlayer.posX, mc.thePlayer.posY + 1, mc.thePlayer.posZ, mc.thePlayer.posX, mc.thePlayer.posY + 1, mc.thePlayer.posZ);
								mc.thePlayer.fallDistance = 0;
								mc.thePlayer.onGround = false;
								mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C0APacketAnimation());
								mc.playerController.onPlayerRightClickNoSync(mc.thePlayer, mc.theWorld, is, new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ).add(0, -down, 0), EnumFacing.UP, RotationUtils.getVectorForRotation(90, mc.thePlayer.rotationYaw));
								mc.thePlayer.boundingBox = oldBox;
								Minecraft.getMinecraft().getNetHandler().getNetworkManager()
									.sendPacketNoEvent( new C09PacketHeldItemChange(heldItemBeforeThrow));
								mc.thePlayer.inventory.currentItem = heldItemBeforeThrow;
								RenderUtils.setCustomPitch(90);
								RenderUtils.setCustomYaw(mc.thePlayer.rotationYaw);
								return;
							}
							
						}
						
					}
					
				}
				
			}
			
		}
		
		if (e instanceof EventUpdate) {
			
			if (e.isPre()) {
				
				this.additionalInformation = noFallMode.getMode();
				
				if (mc.thePlayer.fallDistance >= 3 && noFallMode.is("Packet") && !SpicyClient.config.fly.isEnabled()) {
					
					mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer(true));
					
				}
				else if (mc.thePlayer.fallDistance >= 3 && noFallMode.is("Hypixel") && !SpicyClient.config.fly.isEnabled() && mc.thePlayer.motionY >= -0.4) {
					mc.thePlayer.motionY = -0.4;
					if (SpicyClient.config.fly.isEnabled()) {
						hasLandedAfterFly = false;
					}
					
					if (MovementUtils.isOnGround(0.0001)) {
						hasLandedAfterFly = true;
					}
					
				}
				
			}
			
		}
		
	}
	
}
