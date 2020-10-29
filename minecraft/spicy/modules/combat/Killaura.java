package spicy.modules.combat;

import java.awt.Color;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C02PacketUseEntity.Action;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings;
import spicy.SpicyClient;
import spicy.chatCommands.Command;
import spicy.events.Event;
import spicy.events.listeners.EventMotion;
import spicy.events.listeners.EventRenderGUI;
import spicy.events.listeners.EventUpdate;
import spicy.modules.Module;
import spicy.modules.movement.Sprint;
import spicy.settings.BooleanSetting;
import spicy.settings.ModeSetting;
import spicy.settings.NumberSetting;
import spicy.settings.SettingChangeEvent;
import spicy.util.RotationUtils;
import spicy.util.Timer;

public class Killaura extends Module {
	
	public static EntityLivingBase target = null;
	
	public NumberSetting range = new NumberSetting("Range", 4, 1, 6, 0.1);
	private NumberSetting aps = new NumberSetting("APS", 10, 0, 20, 1);
	private BooleanSetting noSwing = new BooleanSetting("NoSwing", false);
	private BooleanSetting disableOnDeath = new BooleanSetting("DisableOnDeath", false);
	public BooleanSetting dontHitDeadEntitys = new BooleanSetting("Don't hit dead entitys", true);
	public ModeSetting targetsSetting = new ModeSetting("Targets", "Players", "Players", "Animals", "Mobs", "Everything");
	public ModeSetting rotationSetting = new ModeSetting("Rotation setting", "lock", "lock", "smooth");
	public ModeSetting newAutoblock = new ModeSetting("Autoblock mode", "None", "None", "Vanilla", "Hypixel");
	public ModeSetting targetingMode = new ModeSetting("Targeting mode", "Single", "Single", "Switch");
	public NumberSetting switchTime = new NumberSetting("Switch Time", 2, 0.1, 10, 0.1);
	public BooleanSetting hitOnHurtTime = new BooleanSetting("Hit on hurt time", false);
	
	private static boolean blocking = false;
	
	private static transient float lastSmoothYaw, lastSmoothPitch;
	
	private int[] randoms = {0,1,0};
	public static float sYaw, sPitch, aacB;
	
	// These settings are not used anymore but are still here so you can update old configs
	private BooleanSetting autoblock = new BooleanSetting("Autoblock", false);
	public ModeSetting targetModeSetting = new ModeSetting("Targets", "Players", "Players", "Animals", "Mobs", "Everything");
	
	public Killaura() {
		super("Killaura", Keyboard.KEY_NONE, Category.COMBAT);
		resetSettings();
	}
	
	private static Timer targetSwitchTimer = new Timer();
	private static EntityLivingBase lastTarget = null;
	
	@Override
	public void resetSettings() {
		this.settings.clear();
		this.addSettings(range, aps, noSwing, switchTime, disableOnDeath, dontHitDeadEntitys, targetsSetting, newAutoblock, targetingMode, rotationSetting, hitOnHurtTime);
	}
	
	public void onEnable() {
		aacB = 0;
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
		
		// For the target hud
		if (e instanceof EventRenderGUI && target != null) {
			
			if (target == null) {
				return;
			}
			
			ScaledResolution sr = new ScaledResolution(mc);
			FontRenderer fr = mc.fontRendererObj;
			
			GlStateManager.color(1, 1, 1);
			Gui.drawRect(sr.getScaledWidth() / 2 - 110, sr.getScaledHeight() / 2 + 100, sr.getScaledWidth() / 2 + 110, sr.getScaledHeight() / 2 + 170, 0x50000000);
			GlStateManager.color(1, 1, 1);
			RenderHelper.enableGUIStandardItemLighting();
			Gui.drawRect(sr.getScaledWidth() / 2 - 110, sr.getScaledHeight() / 2 + 100, sr.getScaledWidth() / 2 - 110 + (((220) / (target.getMaxHealth())) * (target.getHealth())), sr.getScaledHeight() / 2 + 96, 0xff00ff00);
			GlStateManager.enableBlend();
			GlStateManager.color(1, 1, 1);
			GuiInventory.drawEntityOnScreen(sr.getScaledWidth() / 2 - 75, sr.getScaledHeight() / 2 + 165, 25, 1, 1f, target);
			fr.drawString(target.getName(), sr.getScaledWidth() / 2 - 40, sr.getScaledHeight() / 2 + 110, -1);
			fr.drawString("HP: §a" + target.getHealth() + "§f/§a" + target.getMaxHealth(), sr.getScaledWidth() / 2 - 40, sr.getScaledHeight() / 2 + 125, -1);
			RenderHelper.enableGUIStandardItemLighting();
			mc.getRenderItem().renderItemAndEffectIntoGUI(target.getHeldItem(), sr.getScaledWidth() / 2 - 40, sr.getScaledHeight() / 2 + 143);
			mc.getRenderItem().renderItemAndEffectIntoGUI(target.getCurrentArmor(3), sr.getScaledWidth() / 2 - 10, sr.getScaledHeight() / 2 + 143);
			mc.getRenderItem().renderItemAndEffectIntoGUI(target.getCurrentArmor(2), sr.getScaledWidth() / 2 + 20, sr.getScaledHeight() / 2 + 143);
			mc.getRenderItem().renderItemAndEffectIntoGUI(target.getCurrentArmor(1), sr.getScaledWidth() / 2 + 50, sr.getScaledHeight() / 2 + 143);
			mc.getRenderItem().renderItemAndEffectIntoGUI(target.getCurrentArmor(0), sr.getScaledWidth() / 2 + 80, sr.getScaledHeight() / 2 + 143);
			
			
			//Gui.drawRect(sr.getScaledWidth() / 2, sr.getScaledHeight() / 2 + 100, sr.getScaledWidth() / 2 + 10, sr.getScaledHeight() / 2 + 150, 0x50000000);
			
		}
		
		if (e instanceof EventUpdate) {
			
			if (e.isPre()) {
				
				//this.additionalInformation = targetingMode.getMode() + SpicyClient.hud.separator + rotationSetting.getMode();
				this.additionalInformation = "R: " + range.getValue() + SpicyClient.hud.separator + "APS: " + aps.getValue();
				
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
						
						if (rotationSetting.is("lock") || rotationSetting.getMode() == "lock") {
							
							//event.setYaw(getRotations(target)[0]+10);
							//event.setPitch(getRotations(target)[1]);
							
                            float[] rotations = RotationUtils.getRotations(target);
                            event.setYaw(rotations[0]);
                            event.setPitch(rotations[1]);
							
						}
						else if (rotationSetting.is("smooth") || rotationSetting.getMode() == "smooth") {
							
                    		aacB/=2;
                    		customRots(event, target);
							
							/*
							try {
								getSmoothRotations(event);
							} catch (NullPointerException e2) {
								
								this.lastSmoothPitch = event.getPitch();
								this.lastSmoothYaw = event.getYaw();
								
								try {
									getSmoothRotations(event);
								} catch (NullPointerException e3) {
									return;
								}
								
							}
							*/
							
						}
						
					}
					
					Random random = new Random();
					
					startBlocking(false);
					
					if (rotationSetting.is("lock") || rotationSetting.getMode() == "lock") {
						
                        float[] rotations = RotationUtils.getRotations(target);
                        mc.thePlayer.rotationYawHead = rotations[0];
                        
						//mc.thePlayer.rotationYawHead = getRotations(target)[0];
                        
					}
					
					if (hitOnHurtTime.isEnabled()) {
						if (target.hurtTime > 2) {
							return;
						}
					}
					
					if (timer.hasTimeElapsed((long) (1000/aps.getValue()), true)) {
						
            			int XR = randomNumber(1, -1);
                    	int YR = randomNumber(1, -1);
                    	int ZR = randomNumber(1, -1);
                    	randoms[0] = XR;
                    	randoms[1] = YR;
                    	randoms[2] = ZR;
                    	
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
                        
                        if (SpicyClient.config.criticals.isEnabled() && mc.thePlayer.onGround) {
                        	
                        	mc.thePlayer.onCriticalHit(target);
                        	
                        }
                        
						if (s.toggled) {
							mc.thePlayer.setSprinting(true);
						}
						if (newAutoblock.is("Hypixel") && mc.thePlayer.isSwingInProgress && mc.thePlayer.isBlocking()) {
							
							blockHypixel(target);
							
							//startBlocking(true);
							//Random r = new Random();
							//mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(new BlockPos(-0.410153517, -0.4083644, -0.4186343), 255, mc.thePlayer.getHeldItem(), 0, 0, 0));
						}
						
					}
					
				}else {
					
					stopBlocking();
					
		            return;
		            
				}
				
			}
			
		}
		
	}
	
	private void blockHypixel(EntityLivingBase ent) {
		//isBlocking = true;
		
		if (ent == null) {
			return;
		}
		
		sendUseItem(mc.thePlayer, mc.theWorld, mc.thePlayer.getCurrentEquippedItem());
		
		mc.thePlayer.sendQueue.addToSendQueue(new C02PacketUseEntity(ent, new Vec3((double)randomNumber(-50, 50)/100, (double)randomNumber(0, 200)/100, (double)randomNumber(-50, 50)/100)));
		mc.thePlayer.sendQueue.addToSendQueue(new C02PacketUseEntity(ent, Action.INTERACT));
		//mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(new BlockPos(-0.410153517, -0.4583644, -0.4186343), 255, mc.thePlayer.getHeldItem(), 0, 0, 0));
		
		// Used to find new autoblock
		//mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(new BlockPos(-0.410153517, -0.8083644 - ((float)(((float)(mc.getSession().getUsername().length())) / 1000)), -0.7186343), 255, mc.thePlayer.getHeldItem(), 0, 0, 0));
		
		// New autoblock
		mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(new BlockPos(-0.610153517F, -0.8153644002160668F, -0.7186343F), 255, mc.thePlayer.getHeldItem(), 0, 0, 0));
		
		//mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(new BlockPos(-0.910153517, -0.9083644, -0.9186343), 255, mc.thePlayer.getHeldItem(), 0, 0, 0));
	}
	
    public boolean sendUseItem(EntityPlayer playerIn, World worldIn, ItemStack itemStackIn)
    {
        if (mc.playerController.currentGameType == WorldSettings.GameType.SPECTATOR)
        {
            return false;
        }
        else
        {
            mc.playerController.syncCurrentPlayItem();
            int i = itemStackIn.stackSize;
            ItemStack itemstack = itemStackIn.useItemRightClick(worldIn, playerIn);

            if (itemstack != itemStackIn || itemstack != null && itemstack.stackSize != i)
            {
                playerIn.inventory.mainInventory[playerIn.inventory.currentItem] = itemstack;

                if (itemstack.stackSize == 0)
                {
                    playerIn.inventory.mainInventory[playerIn.inventory.currentItem] = null;
                }

                return true;
            }
            else
            {
                return false;
            }
        }
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
        	
        	blockHypixel(target);
        	
        	/*
        	if (target != null && interactAutoblock) {
        		//mc.thePlayer.sendQueue.addToSendQueue(new C02PacketUseEntity(target, new Vec3(randomNumber(-50, 50) / 100.0, randomNumber(0, 200) / 100.0, randomNumber(-50, 50) / 100.0)));
        		mc.thePlayer.sendQueue.addToSendQueue(new C02PacketUseEntity(target, C02PacketUseEntity.Action.INTERACT));
        		mc.thePlayer.sendQueue.addToSendQueue(new C02PacketUseEntity(target, C02PacketUseEntity.Action.INTERACT));
        		mc.thePlayer.sendQueue.addToSendQueue(new C02PacketUseEntity(target, C02PacketUseEntity.Action.INTERACT));
        		mc.thePlayer.sendQueue.addToSendQueue(new C02PacketUseEntity(target, C02PacketUseEntity.Action.INTERACT));
        	}
        	
        	mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(getHypixelBlockpos(mc.getSession().getUsername()), 255, mc.thePlayer.inventory.getCurrentItem(), 0.0f, 0.0f, 0.0f));
        	//mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(mc.thePlayer.inventory.getCurrentItem()));
        	
        	mc.thePlayer.setItemInUse(mc.thePlayer.getCurrentEquippedItem(), 10);
        	//mc.gameSettings.keyBindUseItem.pressed = true;
        	//mc.playerController.sendUseItem(mc.thePlayer, mc.theWorld, mc.thePlayer.getCurrentEquippedItem());
            //mc.playerController.sendUseItem(mc.thePlayer, mc.theWorld, mc.thePlayer.inventory.getCurrentItem());
             * 
             */
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
    
    private int randomNumber() {
        return -1 + (int) (Math.random() * ((1 - (-1)) + 1));
    }
    
    public float[] getCustomRotsChange(float yaw, float pitch, double x, double y, double z){
    	
        double xDiff = x - mc.thePlayer.posX;
        double zDiff = z - mc.thePlayer.posZ;
        double yDiff = y - mc.thePlayer.posY;
        
        double dist = MathHelper.sqrt_double(xDiff * xDiff + zDiff * zDiff);
    	double mult =  (1/(dist+0.0001)) * 2;
    	if(mult > 0.2)
    		mult = 0.2;
    	if(!mc.theWorld.getEntitiesWithinAABBExcludingEntity(mc.thePlayer, mc.thePlayer.boundingBox).contains(target)){
        	x += 0.3 * randoms[0];
        	y -= 0.4 + mult * randoms[1];
        	z += 0.3 * randoms[2];
    	}
    	xDiff = x - mc.thePlayer.posX;
        zDiff = z - mc.thePlayer.posZ;
        yDiff = y - mc.thePlayer.posY;
        float yawToEntity = (float) (Math.atan2(zDiff, xDiff) * 180.0D / 3.141592653589793D) - 90.0F;
        float pitchToEntity = (float) -(Math.atan2(yDiff, dist) * 180.0D / 3.141592653589793D);
        return new float[]{MathHelper.wrapAngleTo180_float(-(yaw- (float) yawToEntity)), -MathHelper.wrapAngleTo180_float(pitch - (float) pitchToEntity) - 2.5F};
    }
    
    public void customRots(EventMotion em, EntityLivingBase ent) {
        double randomYaw = 0.05;
		double randomPitch = 0.05;
		float[] rotsN = getCustomRotsChange(sYaw, sPitch, target.posX + randomNumber(1,-1) * randomYaw, target.posY+ randomNumber(1,-1) * randomPitch, target.posZ+ randomNumber(1,-1) * randomYaw);
		float targetYaw = rotsN[0];
		float yawFactor = targetYaw*targetYaw/(4.7f * targetYaw);
		if(targetYaw < 5){
			yawFactor = targetYaw*targetYaw/(3.7f * targetYaw);
		}
		if(Math.abs(yawFactor) > 7){
			aacB = yawFactor*7;
			yawFactor = targetYaw*targetYaw/(3.7f * targetYaw);
		}else{
			yawFactor = targetYaw*targetYaw/(6.7f * targetYaw) + aacB;
		}
		
	
		em.setYaw(sYaw + yawFactor);
		sYaw += yawFactor;
		float targetPitch = rotsN[1];
		float pitchFactor = targetPitch / 3.7F;
		em.setPitch(sPitch + pitchFactor);
		sPitch += pitchFactor;
    }
    
}
