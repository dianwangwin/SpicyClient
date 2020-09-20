package spicy.modules.combat;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.lwjgl.input.Keyboard;

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
import net.minecraft.network.play.server.S0CPacketSpawnPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
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
import spicy.util.Timer;

public class Killaura extends Module {
	
	public static EntityLivingBase target = null;
	
	private NumberSetting range = new NumberSetting("Range", 4, 1, 6, 0.1);
	private NumberSetting aps = new NumberSetting("APS", 10, 0, 20, 1);
	private BooleanSetting noSwing = new BooleanSetting("NoSwing", false);
	private BooleanSetting disableOnDeath = new BooleanSetting("DisableOnDeath", false);
	private BooleanSetting autoblock = new BooleanSetting("Autoblock", false);
	private ModeSetting targetModeSetting = new ModeSetting("Targets", "Players", "Players", "Animals", "Mobs", "Everything");
	
	public Killaura() {
		super("Killaura", Keyboard.KEY_NONE, Category.COMBAT);
		targetModeSetting.index = targetModeSetting.modes.size() - 1;
		resetSettings();
	}
	
	@Override
	public void resetSettings() {
		this.settings.clear();
		targetModeSetting.index = targetModeSetting.modes.size() - 1;
		this.addSettings(range, aps, noSwing, disableOnDeath, autoblock, targetModeSetting);
	}
	
	public void onEnable() {
		
	}
	
	public void onDisable() {
		
        if (mc.thePlayer != null) {
            mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
        }

		
	}
	
	public Timer timer = new Timer();
	
	public void onEvent(Event e) {
		
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
				
				// This was changed so it would work on servers like mineplex where they spoof the players health to always be 0
				//targets = targets.stream().filter(entity -> entity.getDistanceToEntity(mc.thePlayer) < range.getValue() && entity != mc.thePlayer && !entity.isDead && entity.getHealth() > 0).collect(Collectors.toList());
				
				targets = targets.stream().filter(entity -> entity.getDistanceToEntity(mc.thePlayer) < range.getValue() && entity != mc.thePlayer && !entity.isDead).collect(Collectors.toList());
				
				
				if (targets.isEmpty()) {
					return;
				}
				
				if (!targets.isEmpty()) {
					
					for (EntityLivingBase a : targets) {
						if (a.getDistanceToEntity(mc.thePlayer) > range.getValue()) {
							targetsToRemove.add(a);
						}
					}
					
					int target_filter = targetModeSetting.index;
					
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
						return;
					}
					
					target = targets.get(0);
					
					// This removes a bug which would cause you get get kicked for invalid player movement
					if (target.posX == mc.thePlayer.posX && target.posY == mc.thePlayer.posY && target.posZ == mc.thePlayer.posZ) {
						
					}else {
						
						event.setYaw(getRotations(target)[0]+10);
						event.setPitch(getRotations(target)[1]);
						
					}
					
                    if (autoblock.enabled && (mc.thePlayer.inventory.getCurrentItem() != null) && ((mc.thePlayer.inventory.getCurrentItem().getItem() instanceof ItemSword))) {
                        mc.playerController.sendUseItem(mc.thePlayer, mc.theWorld, mc.thePlayer.inventory.getCurrentItem());
                    }
					
					mc.thePlayer.rotationYawHead = getRotations(target)[0];
					
					if (timer.hasTimeElapsed((long) (1000/aps.getValue()), true)) {
						
						if (s.toggled) {
							mc.thePlayer.setSprinting(true);
						}
						
                        if (mc.thePlayer.isBlocking() && autoblock.enabled) {
                            mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                        }
                        
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
                        
                        if (mc.thePlayer.isBlocking() && autoblock.enabled || mc.gameSettings.keyBindUseItem.pressed && mc.thePlayer.getCurrentEquippedItem() != null) {
                            mc.playerController.sendUseItem(mc.thePlayer, mc.theWorld, mc.thePlayer.getCurrentEquippedItem());
                        }
						
						if (s.toggled) {
							mc.thePlayer.setSprinting(true);
						}
						
					}
					
				}else {
					
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
	
}
