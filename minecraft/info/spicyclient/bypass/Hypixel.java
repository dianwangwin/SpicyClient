package info.spicyclient.bypass;

import java.text.DecimalFormat;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

import info.spicyclient.SpicyClient;
import info.spicyclient.chatCommands.Command;
import info.spicyclient.events.Event;
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
import net.minecraft.item.ItemEnderPearl;
import net.minecraft.item.ItemFireball;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.login.server.S00PacketDisconnect;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition;
import net.minecraft.network.play.client.C03PacketPlayer.C05PacketPlayerLook;
import net.minecraft.network.play.client.C03PacketPlayer.C06PacketPlayerPosLook;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.client.C13PacketPlayerAbilities;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S27PacketExplosion;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;

public class Hypixel {
	
	// This does not work anymore
	public static void damageHypixel(double damage) {
		
		Minecraft mc = Minecraft.getMinecraft();
		
		if (damage > MathHelper.floor_double(mc.thePlayer.getMaxHealth()))
			damage = MathHelper.floor_double(mc.thePlayer.getMaxHealth());

		double offset = 0.0625;
		//offset = 0.015625;
		if (mc.thePlayer != null && mc.getNetHandler() != null && mc.thePlayer.onGround) {
			for (int i = 0; i <= ((3 + damage) / offset); i++) {
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
			threwEnderPearl = false, fireball = false, shouldToggleOnGround = false;
	private static transient double originalX, originalY, originalZ, originalMotionX, originalMotionY, originalMotionZ;
	private static transient int status = 0;
	private static transient CopyOnWriteArrayList<Packet> packets = new CopyOnWriteArrayList<Packet>();
	private static transient long disabledUntil = System.currentTimeMillis();
	
	public static void onFlyEnable() {
		
		disabled = false;
		watchdog = false;
		shouldCancelPackets = false;
		threwEnderPearl = false;
		fireball = false;
		shouldToggleOnGround = false;
		packets.clear();
		status = 0;
		disabledUntil = System.currentTimeMillis();
		
		originalX = Minecraft.getMinecraft().thePlayer.posX;
		originalY = Minecraft.getMinecraft().thePlayer.posY;
		originalZ = Minecraft.getMinecraft().thePlayer.posZ;
		originalMotionX = Minecraft.getMinecraft().thePlayer.motionX;
		originalMotionY = Minecraft.getMinecraft().thePlayer.motionY;
		originalMotionZ = Minecraft.getMinecraft().thePlayer.motionZ;
		MovementUtils.setMotion(0);
		
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
			module.additionalInformation = "Hypixel Freecam";
		}
		
		if (e instanceof EventUpdate && e.isPre() && shouldCancelPackets) {
			
			if (mc.gameSettings.keyBindJump.isKeyDown()) {
				mc.thePlayer.motionY += SpicyClient.config.fly.hypixelFreecamVerticalFlySpeed.getValue();
			}			
			else if (mc.gameSettings.keyBindSneak.isKeyDown()) {
				mc.thePlayer.motionY -= SpicyClient.config.fly.hypixelFreecamVerticalFlySpeed.getValue();
			}
			else {
				mc.thePlayer.motionY = 0;
			}
			
			mc.getNetHandler().addToSendQueue(new C03PacketPlayer(true));
			mc.thePlayer.onGround = false;
			
			MovementUtils.setMotion(SpicyClient.config.fly.hypixelFreecamHorizontalFlySpeed.getValue());
			
			if (shouldToggleOnGround && MovementUtils.isOnGround(0.0001)) {
				module.toggle();
			}
			
			if (disabled) {
				switch (status) {
				case 0:
				case 1:
					mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + 10E-12D, mc.thePlayer.posZ);
					status++;
					break;
				case 2:
					mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY - 10E-12D, mc.thePlayer.posZ);
					status = 0;
					break;

				default:
					break;
				}
			}
			
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
		
		if (e instanceof EventUpdate && e.isPre() && disabled && packets.size() > 0) {
			
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
            	
                if (((EventReceivePacket)e).packet instanceof S08PacketPlayerPosLook) {
                	
                    if (watchdog) {
                    	disabled = true;
                        NotificationManager.getNotificationManager().createNotification("Fly", "Teleporting you to your current position", true, 5000, Type.INFO, Color.PINK);
                        disabledUntil = System.currentTimeMillis() + 5000;
                        if (threwEnderPearl) {
                        	disabledUntil = System.currentTimeMillis() + 3000;
                        }
                        mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(((S08PacketPlayerPosLook)((EventReceivePacket)e).packet).getX(), ((S08PacketPlayerPosLook)((EventReceivePacket)e).packet).getY(), ((S08PacketPlayerPosLook)((EventReceivePacket)e).packet).getZ(), false));
                        e.setCanceled(true);
                    }else {
                    	module.toggle();
                    	NotificationManager.getNotificationManager().createNotification("Fly", "Fly was disabled to prevent flags", true, 5000, Type.WARNING, Color.RED);
                    	return;
                    }
                    
                }
                else if (((EventReceivePacket)e).packet instanceof S27PacketExplosion && fireball) {
                	disabled = true;
                    NotificationManager.getNotificationManager().createNotification("Fly", "Teleporting you to your current position", true, 5000, Type.INFO, Color.PINK);
                    disabledUntil = System.currentTimeMillis() + 3000;
                }
			
            }
            
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
                    mc.thePlayer.motionY = 0.21;
                    watchdog = true;
                    NotificationManager.getNotificationManager().createNotification("Fly", "Please wait 5s.", true, 5000, Type.INFO, Color.PINK);
                    //mc.thePlayer.jump();
                    
                }
            }
			else if (mc.thePlayer.motionY <= 0 && watchdog) {
				shouldCancelPackets = true;
				
				if (!threwEnderPearl && !fireball) {
					
					for (int i = 0; i < 45; i++) {
						
						if (Minecraft.getMinecraft().thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
							ItemStack is = Minecraft.getMinecraft().thePlayer.inventoryContainer.getSlot(i).getStack();
							
							if (is.getItem() instanceof ItemEnderPearl && !threwEnderPearl) {
								threwEnderPearl = true;
								NotificationManager.getNotificationManager().createNotification("Fly",
										"Found ender pearl, throwing it", true, 3000, Type.INFO, Color.BLUE);
								
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
				if (!threwEnderPearl && !fireball) {
					
					// Bans
					/*
					for (int i = 0; i < 45; i++) {
						
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
					*/
				}
				
			}
			else if (shouldCancelPackets) {
                //mc.thePlayer.motionX = 0;
                //mc.thePlayer.motionY = 0;
                //mc.thePlayer.motionZ = 0;
                //mc.thePlayer.jumpMovementFactor = 0;
                //mc.thePlayer.noClip = true;
                //mc.thePlayer.onGround = false;
            }
			
		}
		
	}
	
}
