package info.spicyclient.bypass.hypixel;

import java.text.DecimalFormat;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

import org.lwjgl.input.Keyboard;

import info.spicyclient.SpicyClient;
import info.spicyclient.chatCommands.Command;
import info.spicyclient.events.Event;
import info.spicyclient.events.listeners.EventKey;
import info.spicyclient.events.listeners.EventReceivePacket;
import info.spicyclient.events.listeners.EventRenderGUI;
import info.spicyclient.events.listeners.EventSendPacket;
import info.spicyclient.events.listeners.EventSneaking;
import info.spicyclient.events.listeners.EventTick;
import info.spicyclient.events.listeners.EventUpdate;
import info.spicyclient.modules.Module;
import info.spicyclient.notifications.Color;
import info.spicyclient.notifications.NotificationManager;
import info.spicyclient.notifications.Type;
import info.spicyclient.util.InventoryUtils;
import info.spicyclient.util.MovementUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.item.ItemEnderPearl;
import net.minecraft.item.ItemFireball;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S27PacketExplosion;

public class Fly {
	
	public static long disabledUntil = 0;
	private static CopyOnWriteArrayList<Packet> heldPackets = new CopyOnWriteArrayList<Packet>();
	private static boolean vanillaFly = false, fireballTeleport = false, pearlTeleport = false, shouldLagback = true, waitingForTeleport = false;
	private static transient double originalX, originalY, originalZ, originalMotionX, originalMotionY, originalMotionZ;
	
	public static void onEnable() {
		
		vanillaFly = System.currentTimeMillis() < disabledUntil;
		fireballTeleport = false;
		pearlTeleport = false;
		shouldLagback = !vanillaFly;
		waitingForTeleport = false;
		
		originalX = Minecraft.getMinecraft().thePlayer.posX;
		originalY = Minecraft.getMinecraft().thePlayer.posY;
		originalZ = Minecraft.getMinecraft().thePlayer.posZ;
		originalMotionX = Minecraft.getMinecraft().thePlayer.motionX;
		originalMotionY = Minecraft.getMinecraft().thePlayer.motionY;
		originalMotionZ = Minecraft.getMinecraft().thePlayer.motionZ;
		MovementUtils.setMotion(0);
		
	}
	
	public static void onDisable() {
		if (shouldLagback) {
			Minecraft.getMinecraft().thePlayer.setPosition(originalX, originalY, originalZ);
			Minecraft.getMinecraft().thePlayer.motionX = originalMotionX;
			Minecraft.getMinecraft().thePlayer.motionY = originalMotionY;
			Minecraft.getMinecraft().thePlayer.motionZ = originalMotionZ;
		}
	}
	
	public static void onEvent(Event e, Module module, Minecraft mc) {
		
		if (e instanceof EventRenderGUI && e.isPre()) {
			short itemAmount = 0;
			
			for (short i = 0; i < 45; i++) {
				
				if (Minecraft.getMinecraft().thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
					ItemStack is = Minecraft.getMinecraft().thePlayer.inventoryContainer.getSlot(i).getStack();
					
					if ((is.getItem() instanceof ItemEnderPearl && SpicyClient.config.fly.hypixelUsePearl.isEnabled())
							|| (is.getItem() instanceof ItemFireball
									&& SpicyClient.config.fly.hypixelUseFireball.isEnabled())) {
						itemAmount += is.stackSize;
					}
					
				}
				
			}
			
			mc.fontRendererObj.drawString(
					"§fYou can fly §" + (itemAmount == 0 ? "c" : "f") + itemAmount + "§f more times",
					((float) (new ScaledResolution(mc).getScaledWidth_double() / 2)
							- (mc.fontRendererObj.getStringWidth(
									"§fYou can fly §" + (itemAmount == 0 ? "4" : "f") + itemAmount + "§f more times")
									/ 2)),
					40, -1, false);
		}
		
		if (e instanceof EventSneaking && e.isPre()) {
			((EventSneaking)e).sneaking = false;
		}
		
		if (vanillaFly) {
			
			if (e instanceof EventUpdate && e.isPre()) {
				module.additionalInformation = "Vanilla fly";
				doFlyMotion(mc);
				if (System.currentTimeMillis() > disabledUntil) {
					module.toggle();
					NotificationManager.getNotificationManager().createNotification("Fly", "Fly was disabled to prevent flags", true, 2500, Type.WARNING, Color.RED);
					return;
				}
			}
			else if (e instanceof EventRenderGUI && e.isPre()) {
				if (System.currentTimeMillis() < disabledUntil) {
					
					String timeLeft = new DecimalFormat("#.#")
							.format(((double) disabledUntil - System.currentTimeMillis()) / 1000);
					
					mc.fontRendererObj.drawString(timeLeft + " secs left",
							((float) (new ScaledResolution(mc).getScaledWidth_double() / 2)
									- (mc.fontRendererObj.getStringWidth(timeLeft + " secs left") / 2)),
							((float) (new ScaledResolution(mc).getScaledHeight_double() / 2)
									- (mc.fontRendererObj.FONT_HEIGHT - 18)),
							-1, false);
					
				} else {
					mc.fontRendererObj.drawString("0.0 secs left",
							((float) (new ScaledResolution(mc).getScaledWidth_double() / 2)
									- (mc.fontRendererObj.getStringWidth("0.00 secs left") / 2)),
							((float) (new ScaledResolution(mc).getScaledHeight_double() / 2)
									- (mc.fontRendererObj.FONT_HEIGHT - 18)),
							0xff2121, false);
				}
				
			}
			
		}
		else if (fireballTeleport || pearlTeleport) {
			
			if (e instanceof EventUpdate && e.isPre()) {
				module.additionalInformation = "Freecam fly";
				if (!waitingForTeleport) {
					doFlyMotion(mc);
				}
			}
			else if (e instanceof EventSendPacket && e.isBeforePre()) {
				EventSendPacket event = (EventSendPacket)e;
				
				if (event.packet instanceof C03PacketPlayer) {
					C03PacketPlayer c03PacketPlayer = (C03PacketPlayer)event.packet;
					heldPackets.add(event.packet);
					e.setCanceled(true);
				}
				
			}
			else if (e instanceof EventKey) {
				EventKey event = (EventKey)e;
				if (event.getKey() == SpicyClient.config.fly.hypixelTeleportBind.getKeycode()) {
					if (!doThrow(mc, true)) {
						module.toggle();
						NotificationManager.getNotificationManager().createNotification("Fly", "You need a a fireball or pearl to freecam fly", true, 5000, Type.WARNING, Color.PINK);
						return;
					}
					MovementUtils.setMotion(0);
					waitingForTeleport = true;
				}
			}
			else if (e instanceof EventReceivePacket && e.isPre() && waitingForTeleport) {
				
				if (((EventReceivePacket)e).packet instanceof S27PacketExplosion && fireballTeleport) {
					shouldLagback = false;
				}
				else if (((EventReceivePacket)e).packet instanceof S08PacketPlayerPosLook && pearlTeleport) {
					mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(((S08PacketPlayerPosLook)((EventReceivePacket)e).packet).getX(), ((S08PacketPlayerPosLook)((EventReceivePacket)e).packet).getY(), ((S08PacketPlayerPosLook)((EventReceivePacket)e).packet).getZ(), false));
					e.setCanceled(true);
					shouldLagback = false;
				}
				
			}
			else if (e instanceof EventTick && e.isPre()) {
				
				if (!shouldLagback) {
					for (Packet p : heldPackets) {
						mc.getNetHandler().getNetworkManager().sendPacketNoEvent(p);
					}
					heldPackets.clear();
					module.toggle();
					NotificationManager.getNotificationManager().createNotification("Fly", "You have been teleported", true, 5000, Type.INFO, Color.PINK);
					return;
				}
				
			}
			else if (e instanceof EventRenderGUI && e.isPre()) {
				mc.fontRendererObj.drawString("Press your " + Keyboard.getKeyName(SpicyClient.config.fly.hypixelTeleportBind.getKeycode()) + " key to teleport",
						((float) (new ScaledResolution(mc).getScaledWidth_double() / 2)
								- (mc.fontRendererObj.getStringWidth("Press your " + Keyboard.getKeyName(SpicyClient.config.fly.hypixelTeleportBind.getKeycode()) + " key to teleport") / 2)),
						((float) (new ScaledResolution(mc).getScaledHeight_double() / 2)
								- (mc.fontRendererObj.FONT_HEIGHT - 18)),
						-1, false);
			}
			
		}
		else {
			
			if (e instanceof EventUpdate &&e.isPre()) {
				module.additionalInformation = "Freecam fly";
				if (!doThrow(mc, false)) {
					module.toggle();
					NotificationManager.getNotificationManager().createNotification("Fly", "You need a a fireball or pearl to freecam fly", true, 5000, Type.WARNING, Color.PINK);
					if (MovementUtils.isOnGround(0.0001)) {
						mc.thePlayer.jump();
					}
					return;
				}
			}
			
		}
		
	}
	
	public static void onEventWhileDisabled(Event e, Module module, Minecraft mc) {
		
		if (module.getKey() == Keyboard.KEY_NONE) {
			return;
		}
		
		if (e instanceof EventRenderGUI && e.isPre()) {
			
			if (System.currentTimeMillis() < disabledUntil) {
				
				String timeLeft = new DecimalFormat("#.#")
						.format(((double) disabledUntil - System.currentTimeMillis()) / 1000);
				
				mc.fontRendererObj.drawString(timeLeft + " secs left",
						((float) (new ScaledResolution(mc).getScaledWidth_double() / 2)
								- (mc.fontRendererObj.getStringWidth(timeLeft + " secs left") / 2)),
						((float) (new ScaledResolution(mc).getScaledHeight_double() / 2)
								- (mc.fontRendererObj.FONT_HEIGHT - 18)),
						-1, false);
				
			}
			
			short itemAmount = 0;
			
			for (short i = 0; i < 45; i++) {
				
				if (Minecraft.getMinecraft().thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
					ItemStack is = Minecraft.getMinecraft().thePlayer.inventoryContainer.getSlot(i).getStack();
					
					if ((is.getItem() instanceof ItemEnderPearl && SpicyClient.config.fly.hypixelUsePearl.isEnabled())
							|| (is.getItem() instanceof ItemFireball
									&& SpicyClient.config.fly.hypixelUseFireball.isEnabled())) {
						itemAmount += is.stackSize;
					}
					
				}
				
			}
			
			mc.fontRendererObj.drawString(
					"§fYou can fly §" + (itemAmount == 0 ? "c" : "f") + itemAmount + "§f more times",
					((float) (new ScaledResolution(mc).getScaledWidth_double() / 2)
							- (mc.fontRendererObj.getStringWidth(
									"§fYou can fly §" + (itemAmount == 0 ? "4" : "f") + itemAmount + "§f more times")
									/ 2)),
					40, -1, false);
			
		}
		
	}
	
	private static void doFlyMotion(Minecraft mc) {
		
		if (mc.gameSettings.keyBindJump.isKeyDown()) {
			mc.thePlayer.motionY += SpicyClient.config.fly.hypixelFreecamVerticalFlySpeed.getValue();
		}			
		else if (mc.gameSettings.keyBindSneak.isKeyDown()) {
			mc.thePlayer.motionY -= SpicyClient.config.fly.hypixelFreecamVerticalFlySpeed.getValue();
		}
		else {
			mc.thePlayer.motionY = 0;
		}
		
		MovementUtils.setMotion(SpicyClient.config.fly.hypixelFreecamHorizontalFlySpeed.getValue());
		
		if (!MovementUtils.isMoving()) {
			MovementUtils.setMotion(0);
		}
		
	}
	
	private static boolean doThrow(Minecraft mc, boolean shouldThrow) {
		
		boolean threw = false;
		
		if (SpicyClient.config.fly.hypixelUsePearl.isEnabled() && !threw) {
			
			for (short i = 0; i < 45; i++) {
				
				if (Minecraft.getMinecraft().thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
					ItemStack is = Minecraft.getMinecraft().thePlayer.inventoryContainer.getSlot(i).getStack();
					
					if (is.getItem() instanceof ItemEnderPearl && !threw) {
						pearlTeleport = true;
						threw = true;
						
						int heldItemBeforeThrow = mc.thePlayer.inventory.currentItem;
						if (i - 36 < 0) {
							
							InventoryUtils.swap(i, 8);
							
							if (shouldThrow) {
								Minecraft.getMinecraft().getNetHandler().getNetworkManager()
									.sendPacketNoEvent(new C09PacketHeldItemChange(8));
							}
							
						}else {
							
							if (shouldThrow) {
								Minecraft.getMinecraft().getNetHandler().getNetworkManager()
								.sendPacketNoEvent(new C09PacketHeldItemChange(i - 36));
							}
							
						}
						
						Minecraft.getMinecraft().getNetHandler().getNetworkManager()
								.sendPacketNoEvent(new C03PacketPlayer.C05PacketPlayerLook(
										mc.thePlayer.rotationYaw, 88.99f + new Random().nextFloat(),
										MovementUtils.isOnGround(0.0001)));
						
						if (shouldThrow) {
							Minecraft.getMinecraft().getNetHandler().getNetworkManager()
									.sendPacketNoEvent(new C08PacketPlayerBlockPlacement(is));
							mc.thePlayer.inventory.currentItem = heldItemBeforeThrow;
							Minecraft.getMinecraft().getNetHandler().getNetworkManager()
									.sendPacketNoEvent(new C09PacketHeldItemChange(heldItemBeforeThrow));
						}
						
						return true;
						
					}
					
				}
				
			}
		}
		
		if (SpicyClient.config.fly.hypixelUseFireball.isEnabled() && !threw) {
			
			for (short i = 0; i < 45; i++) {
				
				if (Minecraft.getMinecraft().thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
					ItemStack is = Minecraft.getMinecraft().thePlayer.inventoryContainer.getSlot(i).getStack();
					
					if (is.getItem() instanceof ItemFireball && !threw) {
						fireballTeleport = true;
						threw = true;
						
						int heldItemBeforeThrow = mc.thePlayer.inventory.currentItem;
						if (i - 36 < 0) {
							
							InventoryUtils.swap(i, 8);
							
							if (shouldThrow) {
								Minecraft.getMinecraft().getNetHandler().getNetworkManager()
									.sendPacketNoEvent(new C09PacketHeldItemChange(8));
							}
							
						}else {
							
							if (shouldThrow) {
								Minecraft.getMinecraft().getNetHandler().getNetworkManager()
								.sendPacketNoEvent(new C09PacketHeldItemChange(i - 36));
							}
							
						}
						
						Minecraft.getMinecraft().getNetHandler().getNetworkManager()
								.sendPacketNoEvent(new C03PacketPlayer.C05PacketPlayerLook(
										mc.thePlayer.rotationYaw, 88.99f + new Random().nextFloat(),
										MovementUtils.isOnGround(0.0001)));
						
						if (shouldThrow) {
							Minecraft.getMinecraft().getNetHandler().getNetworkManager()
									.sendPacketNoEvent(new C08PacketPlayerBlockPlacement(is));
							mc.thePlayer.inventory.currentItem = heldItemBeforeThrow;
							Minecraft.getMinecraft().getNetHandler().getNetworkManager()
									.sendPacketNoEvent(new C09PacketHeldItemChange(heldItemBeforeThrow));
						}
						
						return true;
						
					}
					
				}
				
			}
			
		}
		
		return false;
		
	}
	
}
