package info.spicyclient.modules.combat;

import java.awt.Color;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.lwjgl.input.Keyboard;

import info.spicyclient.SpicyClient;
import info.spicyclient.chatCommands.Command;
import info.spicyclient.chatCommands.commands.Friend;
import info.spicyclient.events.Event;
import info.spicyclient.events.listeners.EventMotion;
import info.spicyclient.events.listeners.EventRender3D;
import info.spicyclient.events.listeners.EventRenderGUI;
import info.spicyclient.events.listeners.EventUpdate;
import info.spicyclient.modules.Module;
import info.spicyclient.modules.movement.Sprint;
import info.spicyclient.settings.BooleanSetting;
import info.spicyclient.settings.ModeSetting;
import info.spicyclient.settings.NumberSetting;
import info.spicyclient.settings.SettingChangeEvent;
import info.spicyclient.util.MovementUtils;
import info.spicyclient.util.RenderUtils;
import info.spicyclient.util.RotationUtils;
import info.spicyclient.util.ServerUtils;
import info.spicyclient.util.Timer;
import info.spicyclient.util.pathfinding.PathFinder;
import net.minecraft.client.Minecraft;
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
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.client.C18PacketSpectate;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings;

public class TpAura extends Module {
	
	public static EntityLivingBase target = null;
	
	public NumberSetting range = new NumberSetting("Range", 60, 10, 120, 1);
	private NumberSetting aps = new NumberSetting("APS", 10, 0, 20, 1);
	private NumberSetting amountOfTargets = new NumberSetting("Amount Of Targets", 1, 1, 20, 1);
	private BooleanSetting noSwing = new BooleanSetting("NoSwing", false);
	private BooleanSetting disableOnDeath = new BooleanSetting("DisableOnDeath", false);
	public BooleanSetting dontHitDeadEntitys = new BooleanSetting("Don't hit dead entitys", true);
	public ModeSetting targetsSetting = new ModeSetting("Targets", "Players", "Players", "Animals", "Mobs", "Everything");
	public ModeSetting rotationSetting = new ModeSetting("Rotation setting", "lock", "lock", "smooth", "Hypixel");
	public ModeSetting newAutoblock = new ModeSetting("Autoblock mode", "None", "None", "Vanilla", "Hypixel1", "Hypixel2");
	public ModeSetting targetingMode = new ModeSetting("Targeting mode", "Single", "Single", "Switch");
	public NumberSetting switchTime = new NumberSetting("Switch Time", 2, 0.1, 10, 0.1);
	public BooleanSetting hitOnHurtTime = new BooleanSetting("Hit on hurt time", false);
	
	public static ArrayList<PathFinder> pathFinders = new ArrayList<PathFinder>();
	
	private static transient boolean blocking = false;
	
	private static transient float lastSmoothYaw, lastSmoothPitch, lastHypixelYaw, lastHypixelPitch;
	
	private static transient double dynamicAPS = 14;
	private static transient Timer dynamicAPSTimer = new Timer();
	
	private int[] randoms = {0,1,0};
	public static float sYaw, sPitch, upAndDownPitch = 0;
	
	// These settings are not used anymore but are still here so you can update old configs
	private BooleanSetting autoblock = new BooleanSetting("Autoblock", false);
	public ModeSetting targetModeSetting = new ModeSetting("Targets", "Players", "Players", "Animals", "Mobs", "Everything");
	
	public TpAura() {
		super("Tp Aura", Keyboard.KEY_NONE, Category.COMBAT);
		resetSettings();
	}
	
	private static Timer targetSwitchTimer = new Timer();
	private static EntityLivingBase lastTarget = null;
	
	@Override
	public void resetSettings() {
		this.settings.clear();
		this.addSettings(range, amountOfTargets, aps, noSwing, switchTime, disableOnDeath, dontHitDeadEntitys, targetsSetting, newAutoblock, targetingMode, rotationSetting, hitOnHurtTime);
	}
	
	public void onEnable() {
		
		lastSmoothYaw = mc.thePlayer.rotationYaw;
		lastSmoothPitch = mc.thePlayer.rotationPitch;
		
		lastHypixelYaw = mc.thePlayer.rotationYaw;
		lastHypixelPitch = mc.thePlayer.rotationPitch;
		
		SpicyClient.config.hudModConfig.targetHud1.healthBar = new ScaledResolution(mc).getScaledWidth() / 2 - 41;
		dynamicAPS = (randomNumber((int) aps.getValue(), ((int)aps.getValue() - 2)));
		upAndDownPitch = 0;
		
	}
	
	public void onDisable() {
		
		SpicyClient.config.hudModConfig.targetHud1.healthBar = new ScaledResolution(mc).getScaledWidth() / 2 - 41;
		
        if (mc.thePlayer != null && (newAutoblock.is("Hypixel1") || newAutoblock.is("Hypixel2"))) {
        	try {
                if (blocking && (newAutoblock.is("Hypixel1") || newAutoblock.is("Hypixel2")) && mc.thePlayer.inventory.getCurrentItem().getItem() != null && mc.thePlayer.inventory.getCurrentItem().getItem() instanceof ItemSword) {
                    mc.gameSettings.keyBindUseItem.pressed = false;
                    mc.playerController.onStoppedUsingItem(mc.thePlayer);
                }
			} catch (NullPointerException e) {
				// TODO: handle exception
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
		
		if (e instanceof EventRender3D) {
			for (PathFinder pathFinder : pathFinders) {
				pathFinder.renderPath();
			}
		}
		
		if (e instanceof EventUpdate) {
			
			if (e.isPre()) {
				
				//this.additionalInformation = targetingMode.getMode() + SpicyClient.hud.separator + rotationSetting.getMode();
				this.additionalInformation = "R: " + range.getValue() + SpicyClient.hud.separator + "APS: " + aps.getValue();
				
			}
			
		}
		
		if (e instanceof EventMotion) {
			
			if (e.isPre()) {
				
				Sprint s = SpicyClient.config.sprint;
				
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
					
					SpicyClient.config.hudModConfig.targetHud1.healthBar = new ScaledResolution(mc).getScaledWidth() / 2 - 41;
					stopBlocking();
					return;
				}
				
				if (!targets.isEmpty()) {
					
					for (EntityLivingBase a : targets) {
						if (a.getDistanceToEntity(mc.thePlayer) > range.getValue()) {
							targetsToRemove.add(a);
						}
					}
					
					for (EntityLivingBase a : targets) {
						
						if (Friend.friends.contains(a.getName().toLowerCase())) {
							targetsToRemove.add(a);
						}
						
					}
					
					if (SpicyClient.config.teams.isEnabled()) {
						
						for (EntityLivingBase a : targets) {
							
							if (Teams.isOnSameTeam(a)) {
								targetsToRemove.add(a);
							}
							
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
					
					if (newAutoblock.is("Vanilla") || newAutoblock.is("Hypixel1")) {
						startBlocking();
					}
					else if (newAutoblock.getMode() == "Hypixel2") {
						mc.thePlayer.setItemInUse(mc.thePlayer.getCurrentEquippedItem(), 7);
					}
					
					target = targets.get(0);
					
					if (target instanceof EntityPlayer && SpicyClient.config.antibot.isEnabled() && !mc.isSingleplayer() && mc.getCurrentServerData().serverIP.toLowerCase().contains("hypixel")) {
						
	                    try {
	                    	
	                    	new Thread("Bot checker thread") {
	                    		public void run() {

	                    			try {
	        	                        if (mc.getNetHandler().getPlayerInfo(((EntityPlayer)target).getUniqueID()).responseTime > 1) {
	        	                        	Command.sendPrivateChatMessage("A watchdog bot was removed from your game (ping check)");
	        	                        	mc.theWorld.removeEntity(target);
	        	                        	return;
	        	                        }
	    							} catch (Exception e2) {
	    								
	    							}
	                				
	                				try {
	        	                        if (mc.getNetHandler()
	    										.getPlayerInfo(((EntityPlayer) target).getUniqueID()) == null) {
	    									Command.sendPrivateChatMessage(
	    											"A watchdog bot was removed from your game (null npi check)");
	    									mc.theWorld.removeEntity(target);
	    									return;
	    								}
	    							} catch (Exception e2) {
	    								
	    							}
	                				
	                				try {
	        	                        if (mc.getNetHandler()
	    										.getPlayerInfo(((EntityPlayer) target).getUniqueID())
	    										.getGameProfile() == null) {
	    									Command.sendPrivateChatMessage(
	    											"A watchdog bot was removed from your game (null game profile check)");
	    									mc.theWorld.removeEntity(target);
	    									return;
	    								}
	    							} catch (Exception e2) {
	    								
	    							}

	                    		};
	                    	}.start();
						} catch (NullPointerException e1) {
							
							//e1.printStackTrace();
							
						}
						
					}
					
					if (targetingMode.is("Switch")) {
						
						if (lastTarget != null && targets.contains(lastTarget)) {
							target = lastTarget;
							if (targetSwitchTimer.hasTimeElapsed((long) (switchTime.getValue()*1000), true)) {
								target = targets.get(0);
							}
							
						}
						
					}
					
					if (target != lastTarget) {
						lastTarget = target;
						
						try {
							
							/*
							Command.sendPrivateChatMessage("F: " + target.getDisplayName().getFormattedText());
							Command.sendPrivateChatMessage("U: " + target.getDisplayName().getUnformattedText());
							Command.sendPrivateChatMessage("UC: " + target.getDisplayName().getUnformattedTextForChat());
							Command.sendPrivateChatMessage("C: " + target.getCustomNameTag());
							
							if (target instanceof EntityPlayer) {
								
								Command.sendPrivateChatMessage("Ping: " + mc.getNetHandler().getPlayerInfo(((EntityPlayer)target).getUniqueID()).responseTime);
								
							}
							*/
							
						} catch (Exception e2) {
							// TODO: handle exception
						}
						
						if (rotationSetting.is("Hypixel") || rotationSetting.getMode() == "Hypixel") {
							
							float[] rotations = RotationUtils.getRotations(target);
							
							float lockRots = mc.thePlayer.rotationYaw + ((mc.thePlayer.rotationYaw - rotations[0]) / 2);
							//Command.sendPrivateChatMessage(lockRots);
							event.setYaw(lockRots);
							RenderUtils.setCustomYaw(event.yaw);
							RenderUtils.setCustomPitch(event.pitch);
							return;
							
						}
						
					}
					
					// if (mc.netHandler.getPlayerInfo(entity.asEntityPlayer().uniqueID)?.responseTime == 0)
	                // return true
					
					// This mostly removes a bug which would cause you get get kicked for invalid player movement
					if (target.posX == mc.thePlayer.posX && target.posY == mc.thePlayer.posY && target.posZ == mc.thePlayer.posZ) {
						
					}else {
						
						if (rotationSetting.is("lock") || rotationSetting.getMode() == "lock") {
							
							//event.setYaw(getRotations(target)[0]+10);
							//event.setPitch(RotationUtils.getRotations(target)[1]);
							
                            float[] rotations = RotationUtils.getRotations(target);
                            event.setYaw(rotations[0]);
                            event.setPitch(rotations[1]);
                            
                            if (event.pitch < -90) {
                            	event.setPitch(-90);
                            }
                            
                            //Command.sendPrivateChatMessage(aacB);
                            
						}
						else if (rotationSetting.is("smooth") || rotationSetting.getMode() == "smooth") {
							
                    		upAndDownPitch/=2;
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
						else if (rotationSetting.is("Hypixel") || rotationSetting.getMode() == "Hypixel") {
							
							hypixelRots(event);
							
							/*
                            float[] rotations = RotationUtils.getRotations(target);
                            event.setYaw(rotations[0]);
                            event.setPitch(rotations[1]);
                            */
							
                            if (event.pitch < -90) {
                            	event.setPitch(-90);
                            }
							
						}
						
						RenderUtils.setCustomYaw(event.yaw);
						RenderUtils.setCustomPitch(event.pitch);
						
					}
					
					Random random = new Random();
					
					startBlocking();
					
					if (hitOnHurtTime.isEnabled()) {
						if (target.hurtTime > 0) {
							return;
						}
					}
					
					//if (timer.hasTimeElapsed((long) (1000/(aps.getValue() + new Random().nextFloat())), true)) {
					//if (timer.hasTimeElapsed((long) (1000/dynamicAPS), true)) {
					Killaura.target = target;
					if (timer.hasTimeElapsed((long) (1000/aps.getValue()), true)) {
						pathFinders.clear();
						dynamicAPS = (randomNumber((int) aps.getValue(), ((int)aps.getValue() - 2)));
						for (int i = 0; i < amountOfTargets.getValue(); i++) {
							try {
								target = targets.get(i);
								PathFinder pathFinder = new PathFinder(4, false, false);
								pathFinder.createPath(mc.thePlayer.getPosition(), target.getPosition().add(1, 0, 0));
								pathFinders.add(pathFinder);
								for (BlockPos pos : pathFinder.path) {
									mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(pos.getX(), pos.getY(), pos.getZ(), true));
								}
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
								
								if (newAutoblock.is("Hypixel1") && !blocking) {
									
									blockHypixel(target);
									
									//startBlocking(true);
									//Random r = new Random();
									//mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(new BlockPos(-0.410153517, -0.4083644, -0.4186343), 255, mc.thePlayer.getHeldItem(), 0, 0, 0));
								}
								if (newAutoblock.is("Hypixel2") && !blocking) {
									mc.thePlayer.setItemInUse(mc.thePlayer.getCurrentEquippedItem(), 7);
								}
								
								if (!ServerUtils.isOnBrokenlens()) {
									Collections.reverse(pathFinder.path);
									for (BlockPos pos : pathFinder.path) {
										mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(pos.getX(), pos.getY(), pos.getZ(), true));
									}
									Collections.reverse(pathFinder.path);
								}
								
							} catch (Exception e2) {
								e2.printStackTrace();
								break;
							}
						}
						
					}
					
				}else {
					
					SpicyClient.config.hudModConfig.targetHud1.healthBar = new ScaledResolution(mc).getScaledWidth() / 2 - 41;
					stopBlocking();
					
		            return;
		            
				}
				
			}
			
		}
		
	}
	
    public boolean sendUseItem(EntityPlayer playerIn, World worldIn, ItemStack itemStackIn)
    {
    	
    	if (newAutoblock.is("None") || newAutoblock.getMode() == "None") {
    		return false;
    	}
    	
        if (mc.playerController.currentGameType == WorldSettings.GameType.SPECTATOR)
        {
            return false;
        }
        else
        {
        	
        	if (itemStackIn == null) {
        		return false;
        	}
        	
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
		
		try {
			if (blocking && newAutoblock.is("Hypixel1") && mc.thePlayer.inventory.getCurrentItem().getItem() != null && mc.thePlayer.inventory.getCurrentItem().getItem() instanceof ItemSword) {
	        	
//	        	mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
				mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, new BlockPos(-0.8, -0.8, -0.8), EnumFacing.DOWN));
	            mc.gameSettings.keyBindUseItem.pressed = false;
	            
	        }
		} catch (NullPointerException e) {
			blocking = false;
		}
        
        blocking = false;
        
		return;
		
	}
	
	private int interactBlock = 0;
	
	private void startBlocking() {
		
		if(newAutoblock.getMode() == "None") {
			return;
		}
		
		if (blocking) {
			if ((mc.thePlayer.inventory.getCurrentItem() != null) && ((mc.thePlayer.inventory.getCurrentItem().getItem() instanceof ItemSword))) {
				sendUseItem(mc.thePlayer, mc.theWorld, mc.thePlayer.getCurrentEquippedItem());
			}
			
			return;
		}
		
		blocking = true;
		
        if (newAutoblock.is("Hypixel1") && (mc.thePlayer.inventory.getCurrentItem() != null) && ((mc.thePlayer.inventory.getCurrentItem().getItem() instanceof ItemSword))) {
        	
        	blockHypixel(target);
        	
        }
        else if (newAutoblock.is("Vanilla") && (mc.thePlayer.inventory.getCurrentItem() != null) && ((mc.thePlayer.inventory.getCurrentItem().getItem() instanceof ItemSword))) {
            mc.playerController.sendUseItem(mc.thePlayer, mc.theWorld, mc.thePlayer.inventory.getCurrentItem());
        }
        
	}
	
	private void blockHypixel(EntityLivingBase ent) {
		
		if (ent == null) {
			return;
		}
		
		sendUseItem(mc.thePlayer, mc.theWorld, mc.thePlayer.getCurrentEquippedItem());
		
		float[] rotations = RotationUtils.getRotations(target);
//		mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C02PacketUseEntity(ent, RotationUtils.getVectorForRotation(rotations[0], rotations[1])));
//		mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C02PacketUseEntity(ent, Action.INTERACT));
		mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), 255, mc.thePlayer.getHeldItem(), 0, 0, 0));
		
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
    	
        if (target == null) {
        	
        	lastSmoothYaw = mc.thePlayer.rotationYaw;
        	lastSmoothPitch = mc.thePlayer.rotationPitch;
        	
        	return;
        	
        }
        
        float yawSpeed = (RotationUtils.getRotations(target)[0] - lastSmoothYaw) / 1.1f,
        		pitchSpeed = (RotationUtils.getRotations(target)[1] - lastSmoothPitch) / 1.1f;
        
        yawSpeed = 70;
        pitchSpeed = 70;
        
        if (yawSpeed < 0)
        	yawSpeed *= -1;
        
        if (pitchSpeed < 0) {
        	pitchSpeed *= -1;
        }
        
        float sYaw = (float) updateRotation((float) lastSmoothYaw, (float) RotationUtils.getRotations(target)[0], yawSpeed);
		float sPitch = (float) updateRotation((float) lastSmoothPitch, (float) RotationUtils.getRotations(target)[1], pitchSpeed);
		
		//Command.sendPrivateChatMessage("Old: " + sYaw + " : " + sPitch);
		
		//double move = 5;
		//move = new Random().nextInt(3) + 1;
		
		//move += new Random().nextDouble();
        //sYaw = (float) updateRotation((float) lastSmoothYaw, (float) RotationUtils.getRotations(target)[0], (float)((lastSmoothYaw - RotationUtils.getRotations(target)[0]) / move));
		//sPitch = (float) updateRotation((float) lastSmoothPitch, (float) RotationUtils.getRotations(target)[1], (float)((lastSmoothPitch - RotationUtils.getRotations(target)[1]) / move));
		
		//Command.sendPrivateChatMessage("New: " + sYaw + " : " + sPitch);
		
		lastSmoothYaw = updateRotation(lastSmoothYaw, sYaw, 360);
		//lastSmoothYaw = sYaw;
		lastSmoothPitch = updateRotation(lastSmoothPitch, sPitch, 360);
		
        if(lastSmoothPitch > 90) {
        	lastSmoothPitch = 90;
        } else if (lastSmoothPitch < -90) {
        	lastSmoothPitch = -90;
        }
        
        //Command.sendPrivateChatMessage("Done: " + sYaw + " : " + sPitch);
        //Command.sendPrivateChatMessage("Current: " + sYaw + " : " + sPitch);
        //Command.sendPrivateChatMessage("Intended: " + RotationUtils.getRotations(target)[0] + " : " + RotationUtils.getRotations(target)[1]);
        //Command.sendPrivateChatMessage(" ");
        
        em.setYaw(lastSmoothYaw);
        em.setPitch(lastSmoothPitch);
        
    }
    
	public static float updateRotation(float current, float intended, float factor) {
		float var4 = MathHelper.wrapAngleTo180_float(intended - current);

		if (var4 > factor) {
			var4 = factor;
		}

		if (var4 < -factor) {
			var4 = -factor;
		}

		return current + var4;
	}
	
	public void hypixelRots(EventMotion em) {
		
		if (target == null)
			return;
		
		float[] rotations = RotationUtils.getRotations(target);
		em.setYaw(rotations[0]);
		em.setPitch(rotations[1]);
        
        if (em.pitch < -90) {
        	em.setPitch(-90);
        }
        
        upAndDownPitch += 1;
        
        double
        	MaxPitch = RotationUtils.getRotationFromPosition(target.posX, target.posZ, target.boundingBox.maxY + 1.2 - 0.15)[1],
        	MinPitch = RotationUtils.getRotationFromPosition(target.posX, target.posZ, target.boundingBox.minY + 1.2 + 0.15)[1],
        	PitchRange = MaxPitch - MinPitch,
        	Percent = 0,
        	Pitch;
        
        if (MaxPitch <= MinPitch) {
        	double temp = MinPitch;
        	MaxPitch = MinPitch;
        	MaxPitch = temp;
        	//PitchRange = MaxPitch - MinPitch;
        }
        
        if (upAndDownPitch < 100) {
        	
        	Percent = upAndDownPitch;
        	
        }
        else if (upAndDownPitch >= 100) {
        	
        	Percent = (100 - (upAndDownPitch - 100));
        	
        }
        
        if (PitchRange <= 0) {
        	PitchRange *= -1;
        }
        
        Pitch = MinPitch + ((PitchRange / 100) * Percent);
        
        //Command.sendPrivateChatMessage(Pitch);
        
        em.setPitch((float) Pitch);
        
        if (upAndDownPitch >= 200) {
        	upAndDownPitch = 0;
        }
        
        if (em.pitch < -90) {
        	em.setPitch(-90);
        }
        else if (em.pitch > 90) {
        	em.setPitch(90);
        }
        
	}
	
}
