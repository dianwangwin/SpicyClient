package info.spicyclient.bypass;

import java.text.DecimalFormat;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

import org.lwjgl.input.Keyboard;

import info.spicyclient.SpicyClient;
import info.spicyclient.chatCommands.Command;
import info.spicyclient.events.Event;
import info.spicyclient.events.listeners.EventMove;
import info.spicyclient.events.listeners.EventReceivePacket;
import info.spicyclient.events.listeners.EventRenderGUI;
import info.spicyclient.events.listeners.EventSendPacket;
import info.spicyclient.events.listeners.EventSneaking;
import info.spicyclient.events.listeners.EventUpdate;
import info.spicyclient.modules.Module;
import info.spicyclient.notifications.Color;
import info.spicyclient.notifications.NotificationManager;
import info.spicyclient.notifications.Type;
import info.spicyclient.util.InventoryUtils;
import info.spicyclient.util.MovementUtils;
import info.spicyclient.util.PlayerUtils;
import info.spicyclient.util.RandomUtils;
import info.spicyclient.util.RotationUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.init.Items;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemEnderPearl;
import net.minecraft.item.ItemFireball;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.login.server.S00PacketDisconnect;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition;
import net.minecraft.network.play.client.C03PacketPlayer.C05PacketPlayerLook;
import net.minecraft.network.play.client.C03PacketPlayer.C06PacketPlayerPosLook;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C07PacketPlayerDigging.Action;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0EPacketClickWindow;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.client.C13PacketPlayerAbilities;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S27PacketExplosion;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;

public class Hypixel {
	
	public static void hypixelShiftClick(Slot slotIn, int slotId, int windowId) {
		ItemStack itemstack = Minecraft.getMinecraft().thePlayer.openContainer.slotClick(slotId, 0, 1, Minecraft.getMinecraft().thePlayer);
		short short1 = Minecraft.getMinecraft().thePlayer.openContainer
				.getNextTransactionID(Minecraft.getMinecraft().thePlayer.inventory);
		Minecraft.getMinecraft().getNetHandler().getNetworkManager().sendPacketNoEvent(
				new C0EPacketClickWindow(windowId, slotId, 0, 1, itemstack, short1));
	}
	
	// This does not work anymore
	public static void damageHypixel(double damage) {
		
		Minecraft mc = Minecraft.getMinecraft();
		
		if (damage > MathHelper.floor_double(mc.thePlayer.getMaxHealth()))
			damage = MathHelper.floor_double(mc.thePlayer.getMaxHealth());

		double offset = 0.0625;
		//offset = 0.015625;
		if (mc.thePlayer != null && mc.getNetHandler() != null && mc.thePlayer.onGround) {
			for (short i = 0; i <= ((3 + damage) / offset); i++) {
				mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX,
						mc.thePlayer.posY + ((offset / 2) * 1), mc.thePlayer.posZ, false));
				mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX,
						mc.thePlayer.posY + ((offset / 2) * 2), mc.thePlayer.posZ, false));
				mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX,
						mc.thePlayer.posY, mc.thePlayer.posZ, (i == ((3 + damage) / offset))));
				//mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C06PacketPlayerPosLook(mc.thePlayer.posX,
						//mc.thePlayer.posY, mc.thePlayer.posZ, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch, (i == ((3 + damage) / offset))));
			}
		}
		
	}
	// This does not work anymore
	
	private static transient boolean disabled = false, watchdog = false, shouldCancelPackets = false,
			threwEnderPearl = false, fireball = false, paper = false, shouldToggleOnGround = false;
	private static transient double originalX, originalY, originalZ, originalMotionX, originalMotionY, originalMotionZ;
	private static transient int status = 0;
	private static transient CopyOnWriteArrayList<Packet> packets = new CopyOnWriteArrayList<Packet>();
	public static transient long disabledUntil = System.currentTimeMillis();
	
	public static void onFlyEnable() {
		
		disabled = false;
		watchdog = false;
		shouldCancelPackets = false;
		threwEnderPearl = false;
		fireball = false;
		paper = false;
		shouldToggleOnGround = false;
		packets.clear();
		status = 0;
		
		originalX = Minecraft.getMinecraft().thePlayer.posX;
		originalY = Minecraft.getMinecraft().thePlayer.posY;
		originalZ = Minecraft.getMinecraft().thePlayer.posZ;
		originalMotionX = Minecraft.getMinecraft().thePlayer.motionX;
		originalMotionY = Minecraft.getMinecraft().thePlayer.motionY;
		originalMotionZ = Minecraft.getMinecraft().thePlayer.motionZ;
		MovementUtils.setMotion(0);
		
		if (System.currentTimeMillis() < disabledUntil) {
			disabled = true;
			watchdog = true;
			shouldCancelPackets = true;
			fireball = true;
			paper = true;
			threwEnderPearl = true;
			MovementUtils.strafe(3f);
		}else {
			disabledUntil = System.currentTimeMillis();
		}
		
		// Removed
		/*
		if (!RandomUtils.isPosSolid(Minecraft.getMinecraft().thePlayer.getPosition().add(0, -1, 0))) {
			SpicyClient.config.fly.toggle();
			NotificationManager.getNotificationManager().createNotification("Fly", "Please stand on a solid block",
					true, 2500, Type.WARNING, Color.RED);
		}
		*/
		
	}
	
	public static void onFlyDisable() {
		
		threwEnderPearl = false;
		
		if (!disabled) {
			Minecraft.getMinecraft().thePlayer.setPosition(originalX, originalY, originalZ);
			Minecraft.getMinecraft().thePlayer.motionX = originalMotionX;
			Minecraft.getMinecraft().thePlayer.motionY = originalMotionY;
			Minecraft.getMinecraft().thePlayer.motionZ = originalMotionZ;
		}
		
	}
	
	public static void onFlyEvent(Event e, Module module, Minecraft mc) {

		if (e instanceof EventUpdate && e.isPre()) {
			module.additionalInformation = "Fast";
		}
		
		if (e instanceof EventUpdate && e.isPre() && shouldCancelPackets) {
			
			if (mc.gameSettings.keyBindJump.isKeyDown()) {
				//mc.thePlayer.motionY += SpicyClient.config.fly.hypixelFreecamVerticalFlySpeed.getValue();
			}			
			else if (mc.gameSettings.keyBindSneak.isKeyDown()) {
				//mc.thePlayer.motionY -= SpicyClient.config.fly.hypixelFreecamVerticalFlySpeed.getValue();
			}
			else {
				//mc.thePlayer.motionY = 0;
			}
			
			//mc.getNetHandler().addToSendQueue(new C03PacketPlayer(true));
			//mc.thePlayer.onGround = false;
			
			MovementUtils.strafe(MovementUtils.getSpeed());
			//MovementUtils.strafe();
			
			if (shouldToggleOnGround && MovementUtils.isOnGround(0.0001)) {
				module.toggle();
			}
			
			if (SpicyClient.config.targetStrafe.isEnabled() && SpicyClient.config.killaura.isEnabled() && SpicyClient.config.killaura.target != null) {
				MovementUtils.strafe(3f);
				EventMove temp = new EventMove(mc.thePlayer.motionX, mc.thePlayer.motionY, mc.thePlayer.motionZ);
				SpicyClient.config.targetStrafe.onEvent(temp);
				mc.thePlayer.motionX = temp.x;
				mc.thePlayer.motionY = temp.y;
				mc.thePlayer.motionZ = temp.z;
			}
			
			if (!MovementUtils.isMoving()) {
				MovementUtils.setMotion(0);
			}
			
		}
		else if (e instanceof EventUpdate && e.isPre() && !shouldCancelPackets) {
			Minecraft.getMinecraft().thePlayer.setPosition(originalX, originalY, originalZ);
		}
		
		if (e instanceof EventSneaking && e.isPre()) {
			((EventSneaking)e).sneaking = false;
		}
		
		if (e instanceof EventRenderGUI && e.isPre() && disabled) {
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
		
		if (e instanceof EventUpdate && e.isPre() && disabled && packets.size() > 0) {
			
			if (paper) {
				MovementUtils.setMotion(0);
			}
			
			double tpX = originalX, tpY = originalY, tpZ = originalZ;
			
			for (Packet p : packets) {
				mc.getNetHandler().getNetworkManager().sendPacketNoEvent(p);
				if (p instanceof C03PacketPlayer) {
					tpX = ((C03PacketPlayer)p).getPositionX();
					tpY = ((C03PacketPlayer)p).getPositionY();
					tpZ = ((C03PacketPlayer)p).getPositionZ();
				}
			}
			
			packets.clear();
			
			//mc.thePlayer.setPosition(tpX, tpY, tpZ);
			
			if (threwEnderPearl || fireball) {
				shouldToggleOnGround = true;
			}
			
		}
		
		if (e instanceof EventSendPacket && e.isPre() && !disabled) {
			
            if (e.isPre()) {
            	
            	if ((((EventSendPacket)e).packet instanceof C09PacketHeldItemChange || ((EventSendPacket)e).packet instanceof C07PacketPlayerDigging || ((EventSendPacket)e).packet instanceof C08PacketPlayerBlockPlacement) && paper && !disabled) {
            		e.setCanceled(true);
            	}
            	
                if (((EventSendPacket)e).packet instanceof C04PacketPlayerPosition || ((EventSendPacket)e).packet instanceof C06PacketPlayerPosLook) {
                    if (watchdog && shouldCancelPackets) {
                    	packets.add(((EventSendPacket)e).packet);
                        e.setCanceled(true);
                    }
                }
                
            }
            
		}
		
		if (e instanceof EventReceivePacket && e.isPre() && !disabled) {
			
            if (e.isPre()) {
            	
            	//You are paper-thin! WOOSH!
            	
            	if (((EventReceivePacket)e).packet instanceof S02PacketChat && paper) {
            		
            		if (((S02PacketChat)((EventReceivePacket)e).packet).getChatComponent().getUnformattedText().equalsIgnoreCase("You are paper-thin! WOOSH!"))
            		
            		mc.playerController.syncCurrentPlayItem();
                	Minecraft.getMinecraft().thePlayer.setPosition(originalX, originalY, originalZ);
                	disabled = true;
        			MovementUtils.setMotion(SpicyClient.config.fly.hypixelFreecamHorizontalFlySpeed.getValue());
        			mc.thePlayer.motionY = SpicyClient.config.fly.hypixelFreecamVerticalFlySpeed.getValue() * 6;
                    disabledUntil = System.currentTimeMillis() + 1000;
            	}
            	else if (((EventReceivePacket)e).packet instanceof S08PacketPlayerPosLook && threwEnderPearl) {
                	
                    if (watchdog) {
                    	Minecraft.getMinecraft().thePlayer.setPosition(originalX, originalY, originalZ);
                    	disabled = true;
            			Minecraft.getMinecraft().thePlayer.setPosition(((S08PacketPlayerPosLook)((EventReceivePacket)e).packet).getX(), ((S08PacketPlayerPosLook)((EventReceivePacket)e).packet).getY(), ((S08PacketPlayerPosLook)((EventReceivePacket)e).packet).getZ());
            			MovementUtils.setMotion(SpicyClient.config.fly.hypixelFreecamHorizontalFlySpeed.getValue());
            			mc.thePlayer.motionY = SpicyClient.config.fly.hypixelFreecamVerticalFlySpeed.getValue() * 6;
            			//mc.thePlayer.jump();
            			MovementUtils.strafe((float) SpicyClient.config.fly.hypixelFreecamHorizontalFlySpeed.getValue());
                        disabledUntil = System.currentTimeMillis() + 2000;
                        mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(((S08PacketPlayerPosLook)((EventReceivePacket)e).packet).getX(), ((S08PacketPlayerPosLook)((EventReceivePacket)e).packet).getY(), ((S08PacketPlayerPosLook)((EventReceivePacket)e).packet).getZ(), false));
                        e.setCanceled(true);
                    }else {
                    	module.toggle();
                    	NotificationManager.getNotificationManager().createNotification("Fly", "Fly was disabled to prevent flags", true, 5000, Type.WARNING, Color.RED);
                    	return;
                    }
                    
                }
                else if (((EventReceivePacket)e).packet instanceof S27PacketExplosion && fireball) {
                	Minecraft.getMinecraft().thePlayer.setPosition(originalX, originalY, originalZ);
                	disabled = true;
        			MovementUtils.setMotion(SpicyClient.config.fly.hypixelFreecamHorizontalFlySpeed.getValue());
        			mc.thePlayer.motionY = SpicyClient.config.fly.hypixelFreecamVerticalFlySpeed.getValue() * 6;
                	//mc.thePlayer.jump();
                	MovementUtils.strafe((float) SpicyClient.config.fly.hypixelFreecamHorizontalFlySpeed.getValue());
                    disabledUntil = System.currentTimeMillis() + 2000;
                }
			
            }
            
		}
		
		if (e instanceof EventMove && disabled) {
			EventMove move = (EventMove)e;
			//move.setSpeed(MovementUtils.getSpeed(), mc.thePlayer.rotationYaw);
		}
		
		if (e instanceof EventUpdate && e.isPre() && !disabled) {
			
			if (!watchdog) {
                if (MovementUtils.isOnGround(0.001) && mc.thePlayer.isCollidedVertically) {
                    double x = mc.thePlayer.posX;
                    double y = mc.thePlayer.posY;
                    double z = mc.thePlayer.posZ;
                    //mc.thePlayer.sendQueue.getNetworkManager().sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(x, y, z, true));
                    //mc.thePlayer.sendQueue.getNetworkManager().sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(x, y + 0.21D, z, true));
                    //mc.thePlayer.sendQueue.getNetworkManager().sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(x, y + 0.11D, z, true));
                    //mc.thePlayer.motionY = 0.21;
                    watchdog = true;
                    //mc.thePlayer.jump();
                    
                }
            }
			else if (mc.thePlayer.motionY <= 0 && watchdog) {
				shouldCancelPackets = true;
				
				if (SpicyClient.config.fly.hypixelUsePearl.isEnabled() && !threwEnderPearl && !fireball && !paper) {
					
					for (short i = 0; i < 45; i++) {
						
						if (Minecraft.getMinecraft().thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
							ItemStack is = Minecraft.getMinecraft().thePlayer.inventoryContainer.getSlot(i).getStack();
							
							if (is.getItem() instanceof ItemEnderPearl && !threwEnderPearl) {
								threwEnderPearl = true;
								NotificationManager.getNotificationManager().createNotification("Fly",
										"Found pearl, throwing it", true, 3000, Type.INFO, Color.BLUE);
								
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
								Minecraft.getMinecraft().getNetHandler().getNetworkManager()
										.sendPacketNoEvent(new C08PacketPlayerBlockPlacement(is));
								mc.thePlayer.inventory.currentItem = heldItemBeforeThrow;
								Minecraft.getMinecraft().getNetHandler().getNetworkManager()
									.sendPacketNoEvent( new C09PacketHeldItemChange(heldItemBeforeThrow));
							}
							
						}
						
					}
				}
				if (SpicyClient.config.fly.hypixelUseFireball.isEnabled() && !threwEnderPearl && !fireball && !paper) {
					
					for (short i = 0; i < 45; i++) {
						
						if (Minecraft.getMinecraft().thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
							ItemStack is = Minecraft.getMinecraft().thePlayer.inventoryContainer.getSlot(i).getStack();
							
							if (is.getItem() instanceof ItemFireball && !fireball) {
								fireball = true;
								NotificationManager.getNotificationManager().createNotification("Fly",
										"Found fireball, throwing it", true, 3000, Type.INFO, Color.BLUE);
								
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
												Minecraft.getMinecraft().thePlayer.rotationYaw,
												88.99f + new Random().nextFloat(), MovementUtils.isOnGround(0.0001)));
								Minecraft.getMinecraft().getNetHandler().getNetworkManager()
										.sendPacketNoEvent(new C08PacketPlayerBlockPlacement(is));
								mc.thePlayer.inventory.currentItem = heldItemBeforeThrow;
								Minecraft.getMinecraft().getNetHandler().getNetworkManager()
									.sendPacketNoEvent( new C09PacketHeldItemChange(heldItemBeforeThrow));
								
							}
							
						}
						
					}
					
				}				
				if (!threwEnderPearl && !fireball && !paper) {
					NotificationManager.getNotificationManager().createNotification("Fly", "Please get a fireball or pearl to fly", true, 3000, Type.WARNING, Color.RED);
					module.toggle();
				}
				
			}
			
		}
		
	}
	
	public static void onFlyEventWhileDisabled(Event e, Module module, Minecraft mc) {
		
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
	
}
