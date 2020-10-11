package spicy.modules.combat;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C02PacketUseEntity.Action;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.client.C13PacketPlayerAbilities;
import net.minecraft.network.play.server.S0CPacketSpawnPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import spicy.SpicyClient;
import spicy.chatCommands.Command;
import spicy.events.Event;
import spicy.events.listeners.EventMotion;
import spicy.events.listeners.EventPacket;
import spicy.events.listeners.EventPlayerUseItem;
import spicy.events.listeners.EventUpdate;
import spicy.files.FileManager;
import spicy.modules.Module;
import spicy.modules.movement.BlockFly;
import spicy.modules.movement.Sprint;
import spicy.settings.BooleanSetting;
import spicy.settings.ModeSetting;
import spicy.settings.NumberSetting;
import spicy.settings.Setting;
import spicy.settings.SettingChangeEvent;
import spicy.util.Timer;

public class Killaura extends Module {
	
	public static EntityLivingBase target = null;
	
	private NumberSetting range = new NumberSetting("Range", 4, 1, 6, 0.1);
	private NumberSetting aps = new NumberSetting("APS", 10, 0, 20, 1);
	private BooleanSetting noSwing = new BooleanSetting("NoSwing", false);
	private BooleanSetting disableOnDeath = new BooleanSetting("DisableOnDeath", false);
	public BooleanSetting dontHitDeadEntitys = new BooleanSetting("Don't hit dead entitys", true);
	public ModeSetting targetsSetting = new ModeSetting("Targets", "Players", "Players", "Animals", "Mobs", "Everything");
	public ModeSetting newAutoblock = new ModeSetting("Autoblock mode", "None", "None", "Vanilla", "Hypixel");
	public ModeSetting targetingMode = new ModeSetting("Targeting mode", "Single", "Single", "Switch");
	public NumberSetting switchTime = new NumberSetting("Switch Time", 2, 0.1, 10, 0.1);
	
	private static boolean blocking = false;
	
	// These settings are not used anymore but are still here so you can update old configs
	private BooleanSetting autoblock = new BooleanSetting("Autoblock", false);
	public ModeSetting targetModeSetting = new ModeSetting("Targets", "Players", "Players", "Animals", "Mobs", "Everything");
	
	public Killaura() {
		super("Killaura", Keyboard.KEY_NONE, Category.COMBAT);
		targetsSetting.index = targetsSetting.modes.size() - 1;
		resetSettings();
	}
	
	private static Timer targetSwitchTimer = new Timer();
	private static EntityLivingBase lastTarget = null;
	
	@Override
	public void resetSettings() {
		this.settings.clear();
		targetsSetting.index = targetsSetting.modes.size() - 1;
		this.addSettings(range, aps, noSwing, switchTime, disableOnDeath, dontHitDeadEntitys, targetsSetting, newAutoblock, targetingMode);
	}
	
	public void onEnable() {
		
	}
	
	public void onDisable() {
		
        if (mc.thePlayer != null && newAutoblock.is("Hypixel")) {
            if (mc.thePlayer.isBlocking() && newAutoblock.is("Hypixel") && mc.thePlayer.inventory.getCurrentItem().getItem() != null && mc.thePlayer.inventory.getCurrentItem().getItem() instanceof ItemSword) {
            	
                mc.gameSettings.keyBindUseItem.pressed = false;
                mc.playerController.onStoppedUsingItem(mc.thePlayer);
                
            }
        }

	}
	
	public Timer timer = new Timer();
	
	@Override
	public void onSettingChange(SettingChangeEvent e) {
		
		if (e.setting != null & e.setting.equals(targetingMode)) {
			
			if (targetingMode.is("Single") && settings.contains(switchTime)) {
				settings.remove(switchTime);
				this.settings.sort(Comparator.comparing(s -> s == keycode ? 1 : 0));
			}
			else if (targetingMode.is("Switch") && !settings.contains(switchTime)) {
				settings.add(switchTime);
				this.settings.sort(Comparator.comparing(s -> s == keycode ? 1 : 0));
			}
			
		}
		
	}
	
	public void onEvent(Event e) {
		
		if (e instanceof EventUpdate) {
			
			if (e.isPre()) {
				
				this.additionalInformation = targetingMode.getMode();
				
			}
			
		}
		
		if (e instanceof EventMotion) {
			
			if (e.isPre()) {
				
				Sprint s = (Sprint) this.findModule(this.getModuleName(new Sprint()));
				
				if (mc.thePlayer.isDead && disableOnDeath.isEnabled()) {
					
					toggled = false;
					
				}
				
				target = null;
				
				EventMotion event = (EventMotion) e;
				
				List<EntityLivingBase> targets = (List<EntityLivingBase>) mc.theWorld.loadedEntityList.stream().filter(EntityLivingBase.class::isInstance).collect(Collectors.toList());
				targets.sort(Comparator.comparingDouble(entity -> ((EntityLivingBase)entity).getDistanceToEntity(mc.thePlayer)));
				
				List<EntityLivingBase> targetsToRemove = (List<EntityLivingBase>) mc.theWorld.loadedEntityList.stream().filter(EntityLivingBase.class::isInstance).collect(Collectors.toList());
				targetsToRemove.clear();
				
				if (dontHitDeadEntitys.enabled) {
					targets = targets.stream().filter(entity -> entity.getDistanceToEntity(mc.thePlayer) < range.getValue() && entity != mc.thePlayer && !entity.isDead && entity.getHealth() > 0).collect(Collectors.toList());
				}else {
					targets = targets.stream().filter(entity -> entity.getDistanceToEntity(mc.thePlayer) < range.getValue() && entity != mc.thePlayer && !entity.isDead).collect(Collectors.toList());
				}
				
				
				if (targets.isEmpty()) {
					
					stopBlocking();
					
					return;
				}
				
				if (!targets.isEmpty()) {
					
					for (EntityLivingBase a : targets) {
						if (a.getDistanceToEntity(mc.thePlayer) > range.getValue()) {
							targetsToRemove.add(a);
						}
					}
					
					int target_filter = targetsSetting.index;
					
					if (target_filter == 0) {
						// kill aura will only hit non invisible players
						//targets.stream().filter(EntityPlayer.class::isInstance).collect(Collectors.toList());
						for (EntityLivingBase a : targets) {
							if (a instanceof EntityPlayer) {
								
							}else {
								targetsToRemove.add(a);
							}
						}
					}
					else if (target_filter == 1) {
						// kill aura will only hit animals
						//targets.stream().filter(EntityAnimal.class::isInstance).collect(Collectors.toList());
						for (EntityLivingBase a : targets) {
							if (a instanceof EntityAnimal) {
								
							}else {
								targetsToRemove.add(a);
							}
						}
					}
					else if (target_filter == 2) {
						// kill aura will only hit mobs
						//targets.stream().filter(EntityMob.class::isInstance).collect(Collectors.toList());
						for (EntityLivingBase a : targets) {
							if (a instanceof EntityMob) {
								
							}else {
								targetsToRemove.add(a);
							}
						}
					}
					else if (target_filter == 3) {
						// kill aura will hit everything
						
					}else {
						
					}
					
					targets.removeAll(targetsToRemove);
					
					if (targets.isEmpty()) {
						stopBlocking();
						return;
					}
					
					startBlocking(false);
					
					target = targets.get(0);
					
					if (targetingMode.is("Switch")) {
						
						if (lastTarget != null && targets.contains(lastTarget)) {
							target = lastTarget;
							if (targetSwitchTimer.hasTimeElapsed((long) (switchTime.getValue()*1000), true)) {
								target = targets.get(0);
							}
							
						}
						
					}
					
					lastTarget = target;
					
					// This mostly removes a bug which would cause you get get kicked for invalid player movement
					if (target.posX == mc.thePlayer.posX && target.posY == mc.thePlayer.posY && target.posZ == mc.thePlayer.posZ) {
						
					}else {
						
						event.setYaw(getRotations(target)[0]+10);
						event.setPitch(getRotations(target)[1]);
						
					}
					
					Random random = new Random();
					
					startBlocking(false);
					
					mc.thePlayer.rotationYawHead = getRotations(target)[0];
					
					if (timer.hasTimeElapsed((long) (1000/aps.getValue()), true)) {
						
						if (s.toggled) {
							mc.thePlayer.setSprinting(true);
						}
						
						stopBlocking();
						
						if (noSwing.enabled) {
							mc.thePlayer.sendQueue.addToSendQueue(new C0APacketAnimation());
						}else {
							mc.thePlayer.swingItem();
						}
						
                        mc.thePlayer.sendQueue.addToSendQueue(new C02PacketUseEntity(target, C02PacketUseEntity.Action.ATTACK));
                        
                        float sharpLevel = EnchantmentHelper.func_152377_a(mc.thePlayer.inventory.getCurrentItem(), target.getCreatureAttribute());
                        if (sharpLevel > 0.0F) {
                            mc.thePlayer.onEnchantmentCritical(target);
                        }
                        
						if (s.toggled) {
							mc.thePlayer.setSprinting(true);
						}
						
    					if (random.nextInt(100) <= 10) {
    						
    						// This was removed
    						//startBlocking(true);
    						
    					}
    					
					}
					
				}else {
					
					stopBlocking();
					
		            return;
		            
				}
				
			}
			
		}
		
	}
	
	public float[] getRotations(Entity e) {
		
		double deltaX = e.posX + (e.posX - e.lastTickPosX) - mc.thePlayer.posX,
				deltaY = e.posY - 3.5 + e.getEyeHeight() - mc.thePlayer.posY + mc.thePlayer.getEyeHeight(),
				deltaZ = e.posZ + (e.posZ - e.lastTickPosZ) - mc.thePlayer.posZ,
				distance = Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaZ, 2));
		
		float yaw = (float) Math.toDegrees(-Math.atan(deltaX / deltaZ)),
				pitch = (float) -Math.toDegrees(Math.atan(deltaY / distance));
		
		
		if (deltaX < 0 && deltaZ < 0) {
			
			yaw = (float) (90 + Math.toDegrees(Math.atan(deltaZ/deltaX)));
			
		}else if (deltaX > 0 && deltaZ < 0) {
			
			yaw = (float) (-90 + Math.toDegrees(Math.atan(deltaZ/deltaX)));
			
		}
		
		return new float[] { yaw, pitch };
		
	}
	
	private void stopBlocking() {
		
        if (mc.thePlayer.isBlocking() && newAutoblock.is("Hypixel") && mc.thePlayer.inventory.getCurrentItem().getItem() != null && mc.thePlayer.inventory.getCurrentItem().getItem() instanceof ItemSword) {
        	
        	mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
            //mc.gameSettings.keyBindUseItem.pressed = false;
            //mc.playerController.onStoppedUsingItem(mc.thePlayer);
            
        }
		return;
		
	}
	
	private int interactBlock = 0;
	
	private void startBlocking(boolean interactAutoblock) {
		
        if (newAutoblock.is("Hypixel") && (mc.thePlayer.inventory.getCurrentItem() != null) && ((mc.thePlayer.inventory.getCurrentItem().getItem() instanceof ItemSword))) {
        	
        	if (target != null && interactAutoblock && interactBlock == 0) {
        		mc.thePlayer.sendQueue.addToSendQueue(new C02PacketUseEntity(target, new Vec3(randomNumber(-50, 50) / 100.0, randomNumber(0, 200) / 100.0, randomNumber(-50, 50) / 100.0)));
        		mc.thePlayer.sendQueue.addToSendQueue(new C02PacketUseEntity(target, C02PacketUseEntity.Action.INTERACT));
        		interactBlock++;
        		Command.sendPrivateChatMessage("Interact packets sent :)");
        	}
        	else if (interactBlock < 3 && interactAutoblock) {
        		interactBlock++;
        	}
        	else if (interactBlock == 3 && interactAutoblock) {
        		interactBlock = 0;
        		Command.sendPrivateChatMessage("Interact packets canceled :)");
        	}
        	
        	mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(getHypixelBlockpos(mc.getSession().getUsername()), 255, mc.thePlayer.inventory.getCurrentItem(), 0.0f, 0.0f, 0.0f));
        	//mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(mc.thePlayer.inventory.getCurrentItem()));
        	
        	mc.thePlayer.setItemInUse(mc.thePlayer.getCurrentEquippedItem(), 10);
        	//mc.gameSettings.keyBindUseItem.pressed = true;
        	//mc.playerController.sendUseItem(mc.thePlayer, mc.theWorld, mc.thePlayer.getCurrentEquippedItem());
            //mc.playerController.sendUseItem(mc.thePlayer, mc.theWorld, mc.thePlayer.inventory.getCurrentItem());
        }
        else if (newAutoblock.is("Vanilla") && (mc.thePlayer.inventory.getCurrentItem() != null) && ((mc.thePlayer.inventory.getCurrentItem().getItem() instanceof ItemSword))) {
        	if (target != null && interactAutoblock) {
        		mc.thePlayer.sendQueue.addToSendQueue(new C02PacketUseEntity(target, new Vec3(randomNumber(-50, 50) / 100.0, randomNumber(0, 200) / 100.0, randomNumber(-50, 50) / 100.0)));
        		mc.thePlayer.sendQueue.addToSendQueue(new C02PacketUseEntity(target, C02PacketUseEntity.Action.INTERACT));
        	}
            mc.playerController.sendUseItem(mc.thePlayer, mc.theWorld, mc.thePlayer.inventory.getCurrentItem());
        }
		
	}
	
	// I found these methods on github somewhere
    public static int randomNumber(final int max, final int min) {
        return Math.round(min + (float)Math.random() * (max - min));
    }
    public static Long randomNumber(final Long max, final Long min) {
        return Math.round(min + Math.random() * (max - min));
    }
    
    public static BlockPos getHypixelBlockpos(final String str) {
        int val = 89;
        if (str != null && str.length() > 1) {
            final char[] chs = str.toCharArray();
            for (int lenght = chs.length, i = 0; i < lenght; ++i) {
                val += chs[i] * str.length() * str.length() + str.charAt(0) + str.charAt(1);
            }
            val /= str.length();
        }
        return new BlockPos(val, -val % 255, val);
    }
    
}
