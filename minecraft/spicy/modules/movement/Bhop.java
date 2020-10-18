package spicy.modules.movement;

import java.io.IOException;
import java.util.Comparator;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.network.EnumPacketDirection;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C13PacketPlayerAbilities;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.potion.Potion;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import spicy.SpicyClient;
import spicy.chatCommands.Command;
import spicy.events.Event;
import spicy.events.listeners.EventMotion;
import spicy.events.listeners.EventUpdate;
import spicy.events.listeners.EventOnLadder;
import spicy.events.listeners.EventPacket;
import spicy.events.listeners.EventSendPacket;
import spicy.modules.Module;
import spicy.settings.BooleanSetting;
import spicy.settings.ModeSetting;
import spicy.settings.NumberSetting;
import spicy.settings.SettingChangeEvent;
import spicy.util.MovementUtils;

public class Bhop extends Module {
	
	public ModeSetting mode = new ModeSetting("Mode", "Vanilla", "Vanilla", "PvpLands", "Hypixel", "Test", "Test 2", "Test 3");
	
	// For the hypixel glide
	public BooleanSetting glideEnabled = new BooleanSetting("Glide", false);
	public NumberSetting hypixelGlideAmount = new NumberSetting("Glide amount", 10, 4, 30, 1);
	
	private static double lastY;
	private static float rotate = 180;
	
	private static int lagbackCheck = 0;
	private static long lastLagback = System.currentTimeMillis();
	
	public Bhop() {
		super("Bhop", Keyboard.KEY_NONE, Category.MOVEMENT);
		resetSettings();
	}
	
	@Override
	public void resetSettings() {
		this.settings.clear();
		this.addSettings(glideEnabled, hypixelGlideAmount, mode);
	}
	
	@Override
	public void onSettingChange(SettingChangeEvent e) {
		
		if (e.setting != null) {
			
			if (e.setting.equals(mode)) {
				
				if (mode.is("Hypixel")) {
					
					if (!settings.contains(glideEnabled)) {
						settings.add(glideEnabled);
						this.settings.sort(Comparator.comparing(s -> s == keycode ? 1 : 0));
					}
					
					if (glideEnabled.enabled) {
						if (!settings.contains(hypixelGlideAmount)) {
							settings.add(hypixelGlideAmount);
							this.settings.sort(Comparator.comparing(s -> s == keycode ? 1 : 0));
						}
					}
					
				}else {
					
					if (settings.contains(glideEnabled)) {
						settings.remove(glideEnabled);
						this.settings.sort(Comparator.comparing(s -> s == keycode ? 1 : 0));
					}
					if (settings.contains(hypixelGlideAmount)) {
						settings.remove(hypixelGlideAmount);
						this.settings.sort(Comparator.comparing(s -> s == keycode ? 1 : 0));
					}
					
				}
				
			}
			else if (e.setting.equals(glideEnabled)) {
				
				if (glideEnabled.enabled) {
					
					if (!settings.contains(hypixelGlideAmount)) {
						settings.add(hypixelGlideAmount);
						this.settings.sort(Comparator.comparing(s -> s == keycode ? 1 : 0));
					}
					
				}else {
					
					if (settings.contains(hypixelGlideAmount)) {
						settings.remove(hypixelGlideAmount);
						this.settings.sort(Comparator.comparing(s -> s == keycode ? 1 : 0));
					}
					
				}
				
			}
			
		}
		
	}
	
	public void onEnable() {
		lastY = mc.thePlayer.posY;
	}
	
	public void onDisable() {
		mc.timer.ticksPerSecond = 20f;
		this.mc.timer.timerSpeed = 1.00f;
		status = 0;
	}
	
	private int status = 0;
	private boolean boosted = false;
	
	public void onEvent(Event e) {
		
		if (e instanceof EventPacket) {
			
			if (e.isPre()) {
				
				EventPacket packetEvent = (EventPacket) e;
				
				if (packetEvent.packet instanceof S08PacketPlayerPosLook) {
					
					if (lagbackCheck >= 3) {
						
						lagbackCheck = 0;
						lastLagback = System.currentTimeMillis() - (5*1000);
						this.toggle();
						Command.sendPrivateChatMessage(this.name + " has been disabled due to lagbacks");
						
					}else {
						
						lastLagback = System.currentTimeMillis();
						lagbackCheck++;
						
					}
					
				}
				
			}
			
		}
		
		if (e instanceof EventUpdate) {
			
			if (e.isPre()) {
				
				if (lastLagback + (5*1000) < System.currentTimeMillis()) {
					lagbackCheck = 0;
					lastLagback = System.currentTimeMillis() + (10*1000);
					
				}
				
			}
			
		}
		
		BlockFly b = (BlockFly) this.findModule(this.getModuleName(new BlockFly()));
		
		if (e instanceof EventUpdate) {
			if (e.isPre()) {
				
				this.additionalInformation = mode.getMode();
				
				if (mode.getMode().equalsIgnoreCase("Vanilla") && mc.gameSettings.keyBindForward.pressed) {
					if (mc.thePlayer.onGround) {
						mc.gameSettings.keyBindJump.pressed = false;
						mc.thePlayer.jump();
						mc.thePlayer.setSprinting(true);
					}
				}
			}
		}
		
		if (e instanceof EventMotion) {
			
			if (e.isBeforePost()) {
				
				EventMotion event = (EventMotion) e;
				if (b == null) {
					
				}
				else if (mode.getMode().equalsIgnoreCase("Pvplands") && !b.isEnabled() && !mc.thePlayer.isInWater()) {
					
					if (mc.thePlayer.onGround && mc.gameSettings.keyBindForward.pressed) {
						mc.thePlayer.setSprinting(true);
						mc.gameSettings.keyBindJump.pressed = false;
			            mc.thePlayer.jump();
			            mc.thePlayer.jump();
			            mc.thePlayer.setSprinting(true);
			            //mc.thePlayer.motionY = 0.1f;
					}
					else if (mc.gameSettings.keyBindForward.pressed) {
			            float f = mc.thePlayer.rotationYaw * 0.017453292F;
			            mc.thePlayer.motionX -= (double)(MathHelper.sin(f) * 0.035F);
			            mc.thePlayer.motionZ += (double)(MathHelper.cos(f) * 0.035F);
					}
					
				}
				else if (mode.getMode().equalsIgnoreCase("Pvplands") && !b.isEnabled() && mc.thePlayer.isInWater()) {
					if (mc.thePlayer.onGround) {
						mc.gameSettings.keyBindJump.pressed = false;
						mc.thePlayer.jump();
						mc.thePlayer.setSprinting(true);
					}
				}
				else if (mode.is("Hypixel") && !b.isEnabled() && !mc.thePlayer.isInWater() && (mc.gameSettings.keyBindForward.pressed || mc.gameSettings.keyBindBack.pressed || mc.gameSettings.keyBindLeft.pressed || mc.gameSettings.keyBindRight.pressed)) {
					
					mc.timer.ticksPerSecond = 23.2f;
					
					rotate = 180;
					
					if (!mc.gameSettings.keyBindForward.pressed && !mc.gameSettings.keyBindBack.pressed && mc.gameSettings.keyBindRight.pressed && mc.gameSettings.keyBindLeft.pressed) {
						
					}
					else if (mc.thePlayer.onGround) {
						mc.thePlayer.setSprinting(true);
						
						mc.thePlayer.motionY += 0.5;
						
			            //mc.thePlayer.jump();
					}
					else if (mc.thePlayer.motionY <= -0.00001 && mc.thePlayer.fallDistance < 5 && glideEnabled.enabled) {
						
						mc.thePlayer.motionY -= mc.thePlayer.motionY / hypixelGlideAmount.getValue();
						
						// Debug crap
						// Command.sendPrivateChatMessage(mc.thePlayer.motionY / 5 + "");
						
					}
					
					if (mc.gameSettings.keyBindLeft.pressed && !mc.gameSettings.keyBindRight.pressed) {
						
						if (!mc.gameSettings.keyBindForward.pressed && !mc.gameSettings.keyBindRight.pressed && !mc.gameSettings.keyBindBack.pressed) {
							rotate -= 90;
						}
						else if (mc.gameSettings.keyBindForward.pressed) {
							rotate -= 10;
						}else {
							rotate += 45;
						}
						
					}
					
					if (mc.gameSettings.keyBindRight.pressed && !mc.gameSettings.keyBindLeft.pressed) {
						
						if (!mc.gameSettings.keyBindForward.pressed && !mc.gameSettings.keyBindLeft.pressed && !mc.gameSettings.keyBindBack.pressed) {
							rotate += 90;
						}
						else if (mc.gameSettings.keyBindForward.pressed) {
							rotate += 90;
						}else {
							rotate -= 45;
						}
						
					}
					
					if (mc.gameSettings.keyBindBack.pressed) {
						
						if (mc.gameSettings.keyBindForward.pressed) {
							rotate += 45;
						}else {
							rotate -= 180;
						}
						
					}
					
					if (mc.gameSettings.keyBindForward.pressed) {
						
						if (rotate != 180) {
							
							if (rotate < 0) {
								rotate += 45;
							}else {
								rotate -= 45;
							}
							
						}
						
					}
					
					float f = (mc.thePlayer.rotationYaw + rotate) * 0.017453292F;
					
					if (!mc.gameSettings.keyBindForward.pressed && !mc.gameSettings.keyBindBack.pressed && mc.gameSettings.keyBindRight.pressed && mc.gameSettings.keyBindLeft.pressed) {
						
					}else {
						mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer(true));
						event.onGround = true;
			            mc.thePlayer.motionX = (double)(MathHelper.sin(f) * 0.295F);
			            mc.thePlayer.motionZ = (double)(MathHelper.cos(f) * 0.295F) * -1;
			            event.setCanceled(true);
					}
					
				}
				else if (mode.is("Test") && !b.isEnabled() && !mc.thePlayer.isInWater() && (mc.gameSettings.keyBindForward.pressed || mc.gameSettings.keyBindBack.pressed || mc.gameSettings.keyBindLeft.pressed || mc.gameSettings.keyBindRight.pressed)) {
					
					this.mc.timer.timerSpeed = 1.18f;
					
					if (MovementUtils.isMoving()) {
			            if (mc.thePlayer.onGround) {
			            	mc.thePlayer.jump();
			            	mc.thePlayer.jump();
			            	mc.thePlayer.jump();
			            	mc.thePlayer.jump();
			            	mc.thePlayer.jump();
			            	mc.thePlayer.jump();
			            	mc.thePlayer.jump();
			            	mc.thePlayer.jump();
			            	mc.thePlayer.jump();
			            	mc.thePlayer.jump();
			                
			                float speed = 0;
			                
			                if (MovementUtils.getSpeed() < 0.56f) {
			                	speed = MovementUtils.getSpeed() * 1.045f;
			                }else {
			                	speed = 0.56f;
			                }
			                
			                MovementUtils.strafe(speed);
			                
			            } else if (mc.thePlayer.motionY < 0.20) {
			            	mc.thePlayer.motionY -= 0.02f;
			            }
			            MovementUtils.strafe(MovementUtils.getSpeed() * 1.01889f);
			        } else {
			            mc.thePlayer.motionZ = 0.0f;
			            mc.thePlayer.motionX = mc.thePlayer.motionZ;
			        }
					
				}
				
			}
			
		}
		
	}
	
}
