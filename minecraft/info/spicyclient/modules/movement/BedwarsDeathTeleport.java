package info.spicyclient.modules.movement;

import java.util.ArrayList;

import org.lwjgl.input.Keyboard;

import info.spicyclient.SpicyClient;
import info.spicyclient.bypass.Hypixel;
import info.spicyclient.chatCommands.Command;
import info.spicyclient.events.Event;
import info.spicyclient.events.listeners.EventKey;
import info.spicyclient.events.listeners.EventReceivePacket;
import info.spicyclient.events.listeners.EventRender3D;
import info.spicyclient.events.listeners.EventRenderGUI;
import info.spicyclient.events.listeners.EventSendPacket;
import info.spicyclient.events.listeners.EventUpdate;
import info.spicyclient.modules.Module;
import info.spicyclient.notifications.Color;
import info.spicyclient.notifications.NotificationManager;
import info.spicyclient.notifications.Type;
import info.spicyclient.settings.BooleanSetting;
import info.spicyclient.settings.KeybindSetting;
import info.spicyclient.settings.NumberSetting;
import info.spicyclient.util.MovementUtils;
import info.spicyclient.util.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.network.Packet;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.login.client.C00PacketLoginStart;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.server.S45PacketTitle;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;

public class BedwarsDeathTeleport extends Module {
	
	public static BlockPos spawnPos = BlockPos.ORIGIN, teleportPos = BlockPos.ORIGIN, revertPos = BlockPos.ORIGIN;
	public static boolean pathSet = false, isSettingPath = false, damaged = false;
	public static ArrayList<Packet> packets = new ArrayList<Packet>();
	private transient static ArrayList<Vec3> trailList = new ArrayList<Vec3>();
	
	public BedwarsDeathTeleport() {
		super("BedwarsDeathTeleport", Keyboard.KEY_NONE, Category.MOVEMENT);
		resetSettings();
	}
	
	private KeybindSetting teleportBind = new KeybindSetting("Teleport bind", Keyboard.KEY_NONE);
	private BooleanSetting damageOnGround = new BooleanSetting("DamageOnGround", false);
	private NumberSetting horizontalSpeed = new NumberSetting("Horizontal Speed", 4, 2, 18, 0.2);
	private NumberSetting verticalSpeed = new NumberSetting("Vertical Speed", 0.4, 0.2, 1, 0.01);
	
	@Override
	public void resetSettings() {
		this.settings.clear();
		this.addSettings(horizontalSpeed, verticalSpeed, teleportBind, damageOnGround);
	}
	
	@Override
	public void onEnable() {
		trailList.clear();
		if (spawnPos == BlockPos.ORIGIN) {
			NotificationManager.getNotificationManager().createNotification(this.name, "Spawn coordinates not set, try jumping into the void and trying again", true, 10000, Type.WARNING, Color.RED);
			this.toggle();
			return;
		}
		revertPos = mc.thePlayer.getPosition();
		mc.thePlayer.setPosition(spawnPos.getX(), spawnPos.getY(), spawnPos.getZ());
		pathSet = false;
		isSettingPath = false;
		damaged = false;
		packets.clear();
		NotificationManager.getNotificationManager().createNotification(this.name, "Fly to where you want to teleport", true, 5000, Type.INFO, Color.GREEN);
	}
	
	@Override
	public void onDisable() {
		if (!pathSet && revertPos != BlockPos.ORIGIN) {
			mc.thePlayer.setPosition(revertPos.getX(), revertPos.getY(), revertPos.getZ());
		}
	}
	
	@Override
	public void onEvent(Event e) {
		
		doStuff(e);
		
		// Imported from the trail module
		if (e instanceof EventUpdate && e.isPre()) {
			if (mc.thePlayer.ticksExisted < 5) {
				trailList.clear();
			}
		}
		else if (e instanceof EventRender3D) {
			EventRender3D render3d = (EventRender3D) e;
			
			if (trailList.size() == 0 && isSettingPath && !pathSet) {
				trailList.add(new Vec3(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ));
			}
			else if (trailList.get(trailList.size() - 1).xCoord == mc.thePlayer.posX && trailList.get(trailList.size() - 1).yCoord == mc.thePlayer.posY && trailList.get(trailList.size() - 1).zCoord == mc.thePlayer.posZ) {
				
			}
			else if (isSettingPath && !pathSet) {
				trailList.add(new Vec3(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ));
			}
			Vec3 lastLoc = null;
			for (Vec3 loc: trailList) {
				
				if (lastLoc == null) {
					lastLoc = loc;
				}else {
					RenderUtils.drawLine(lastLoc.xCoord, lastLoc.yCoord, lastLoc.zCoord, loc.xCoord, loc.yCoord, loc.zCoord);
					lastLoc = loc;
				}
			}
		}
		// Imported from the trail module
		
		if (e instanceof EventUpdate && e.isPre()) {
			if (!pathSet && !isSettingPath && spawnPos != BlockPos.ORIGIN) {
				mc.thePlayer.setPosition(spawnPos.getX(), spawnPos.getY(), spawnPos.getZ());
				isSettingPath = true;
			}
			else if (isSettingPath && !pathSet) {
				if (mc.gameSettings.keyBindJump.isKeyDown()) {
					mc.thePlayer.motionY += verticalSpeed.getValue();
				}			
				else if (mc.gameSettings.keyBindSneak.isKeyDown()) {
					mc.thePlayer.motionY -= verticalSpeed.getValue();
				}
				else {
					mc.thePlayer.motionY = 0;
				}
				
				MovementUtils.setMotion(horizontalSpeed.getValue());
				
				if (!MovementUtils.isMoving()) {
					MovementUtils.setMotion(0);
				}
			}
			else if (pathSet && !isSettingPath && !damaged && MovementUtils.isOnGround(0.0001)) {
				if (damageOnGround.isEnabled()) {
					Hypixel.damageHypixel(30);
					damaged = true;
				}
			}
		}
		else if (e instanceof EventSendPacket && e.isPre()) {
			if (isSettingPath && !pathSet) {
				EventSendPacket event = (EventSendPacket)e;
				if (event.packet instanceof C0FPacketConfirmTransaction || event.packet instanceof C00Handshake || event.packet instanceof C00PacketKeepAlive || event.packet instanceof C00PacketLoginStart)
					return;
				packets.add(event.packet);
				e.setCanceled(true);
			}
		}
		else if (e instanceof EventKey) {
			EventKey event = (EventKey)e;
			if (event.key == teleportBind.getKeycode() && isSettingPath && !pathSet) {
				teleportPos = mc.thePlayer.getPosition();
				MovementUtils.setMotion(0);
				mc.thePlayer.setPosition(revertPos.getX(), revertPos.getY(), revertPos.getZ());
				isSettingPath = false;
				pathSet = true;
				NotificationManager.getNotificationManager().createNotification(this.name, "Kill yourself to teleport", true, 5000, Type.INFO, Color.GREEN);
			}
		}
		else if (e instanceof EventReceivePacket && e.isPre() && ((EventReceivePacket)e).packet instanceof S45PacketTitle) {
			
			try {
				S45PacketTitle packet = ((S45PacketTitle)((EventReceivePacket)e).packet);
				if (packet.getMessage().getFormattedText().toLowerCase().contains("respawned")) {
					
					if (spawnPos == BlockPos.ORIGIN || !pathSet) {
						return;
					}
					
					new Thread(() -> {
						try {
							Thread.sleep(20);
							for (Packet p : packets) {
								mc.getNetHandler().getNetworkManager().sendPacketNoEvent(p);
							}
							packets.clear();
							mc.thePlayer.setPosition(teleportPos.getX(), teleportPos.getY(), teleportPos.getZ());
							NotificationManager.getNotificationManager().createNotification(this.name, "You have been teleported", true, 5000, Type.INFO, Color.GREEN);
						} catch (Exception e1) {
						}
					}).start();

					this.toggle();
				}
			} catch (Exception e2) {
				
			}
			
		}
		else if (e instanceof EventRenderGUI && e.isPre() && isSettingPath && !pathSet) {
			mc.fontRendererObj.drawString("Press your " + Keyboard.getKeyName(teleportBind.getKeycode()) + " key to teleport",
					((float) (new ScaledResolution(mc).getScaledWidth_double() / 2)
							- (mc.fontRendererObj.getStringWidth("Press your " + Keyboard.getKeyName(SpicyClient.config.fly.hypixelTeleportBind.getKeycode()) + " key to teleport") / 2)),
					((float) (new ScaledResolution(mc).getScaledHeight_double() / 2)
							- (mc.fontRendererObj.FONT_HEIGHT - 18)),
					-1, false);
		}
		
	}
	
	@Override
	public void onEventWhenDisabled(Event e) {
		doStuff(e);
	}
	
	private void doStuff(Event e) {
		
		if (e instanceof EventUpdate && e.isPre()) {
			if (mc.thePlayer.ticksExisted < 5) {
				spawnPos = BlockPos.ORIGIN;
				if (this.isEnabled()) {
					pathSet = true;
					this.toggle();
				}
				return;
			}
		}
		else if (e instanceof EventReceivePacket && e.isPre() && ((EventReceivePacket)e).packet instanceof S45PacketTitle) {
			
			try {
				S45PacketTitle packet = ((S45PacketTitle)((EventReceivePacket)e).packet);
				String scoreTitle = Minecraft.getMinecraft().theWorld.getScoreboard().getObjectiveInDisplaySlot(1).getDisplayName();
				
				if (scoreTitle.toLowerCase().contains("bed wars")) {
					if (packet.getMessage().getFormattedText().toLowerCase().contains("respawned")) {
						
						try {
							new Thread(() -> {
								try {
									Thread.sleep(14);
									while (mc.thePlayer.capabilities.allowFlying) {
										
									}
									spawnPos = mc.thePlayer.getPosition();
								} catch (Exception e1) {
								}
							}).start();
						} catch (Exception e2) {
							return;
						}
						
					}
				}
				else if (scoreTitle.toLowerCase().contains("capture the wool")) {
					if (packet.getMessage().getFormattedText().toLowerCase().contains("respawned")) {
						
						try {
							new Thread(() -> {
								try {
									Thread.sleep(14);
									while (mc.thePlayer.capabilities.allowFlying || (!MovementUtils.isOnGround(0.0001))) {
										
									}
									spawnPos = mc.thePlayer.getPosition();
								} catch (Exception e1) {
								}
							}).start();
						} catch (Exception e2) {
							return;
						}
						
					}
				}
				
			} catch (Exception e2) {
				
			}
			
		}
		
	}
	
}
