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
		targetsSetting.index = targetsSetting.modes.size() - 1;
		resetSettings();
	}
	
	private static Timer targetSwitchTimer = new Timer();
	private static EntityLivingBase lastTarget = null;
	
	@Override
	public void resetSettings() {
		this.settings.clear();
		targetsSetting.index = targetsSetting.modes.size() - 1;
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
		
		if (e instanceof EventRenderGUI && target != null) {
			
			/*
			 * 
			 * THIS TAKEN FROM GITHUB
			 * This was made by KtntKot
			 * https://github.com/KtntKot
			 * 
			 */
			
			ScaledResolution sr = new ScaledResolution(mc); 
			FontRenderer fr = mc.fontRendererObj;
			
			Gui.drawRect((sr.getScaledWidth()/1.8f - 5) - 40, (sr.getScaledHeight()/1.5f - 5), (sr.getScaledWidth()/1.8f)+ 120, (sr.getScaledHeight()/1.5f)+40, 0x50000000);
			//Gui.drawRect((sr.getScaledWidth()/1.8f - 6), (sr.getScaledHeight()/1.5f - 6), (sr.getScaledWidth()/1.8f)+ 121, (sr.getScaledHeight()/1.5f)+41, 0x50000000);
			Gui.drawRect((sr.getScaledWidth()/1.8f - 4 ), (sr.getScaledHeight()/1.5f - 2), (sr.getScaledWidth()/1.8f - 3), (sr.getScaledHeight()/1.5f)+37, 0xffff0000);
			
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			GuiInventory.drawEntityOnScreen((int)(sr.getScaledWidth()/1.8f - 5 - 20), (int)(sr.getScaledHeight()/1.5f)+38, 19, 1f, 1f, target);
			
			fr.drawString(target.getName(), (sr.getScaledWidth()/1.8f), (sr.getScaledHeight()/1.5f), -1, false);
			fr.drawString(" HP: " + (int)target.getHealth() + " HurtTime: " + target.hurtTime, (sr.getScaledWidth()/1.8f - 4.5f), (sr.getScaledHeight()/1.5f + fr.FONT_HEIGHT + 2), -1, false);
			if(mc.thePlayer.getHealth() > target.getHealth())
			{
				fr.drawString(" Win chance: Winning ", (sr.getScaledWidth()/1.8f - 4.5f), (sr.getScaledHeight()/1.5f + fr.FONT_HEIGHT + 2 + 10), 0x1fff4e, false);
			}else if (mc.thePlayer.getHealth() < target.getHealth())
			{
				if((target.getHealth() - mc.thePlayer.getHealth()) < 17)
				{
					fr.drawString(" Win chance: Losing ", (sr.getScaledWidth()/1.8f - 4.5f), (sr.getScaledHeight()/1.5f + fr.FONT_HEIGHT + 2 + 10), 0xff1f1f, false);
				}
				
				if((target.getHealth() - mc.thePlayer.getHealth()) > 17)
				{
					fr.drawString(" Win chance: Lost ", (sr.getScaledWidth()/1.8f - 4.5f), (sr.getScaledHeight()/1.5f + fr.FONT_HEIGHT + 2 + 10), 0xff1f1f, false);
				}
			}else if (mc.thePlayer.getHealth() == target.getHealth())
			{
				fr.drawString(" Win chance: 50/50 ", (sr.getScaledWidth()/1.8f - 4.5f), (sr.getScaledHeight()/1.5f + fr.FONT_HEIGHT + 2 + 10), 0x1fff4e, false);
			}
			
			/*
			 * 
			 * THAT TAKEN FROM GITHUB
			 * That was made by KtntKot
			 * https://github.com/KtntKot
			 * 
			 */
			
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
		mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(new BlockPos(-0.410153517, -0.4083644, -0.4186343), 255, mc.thePlayer.getHeldItem(), 0, 0, 0));
		//Command.sendPrivateChatMessage("Old: " + getHypixelBlockpos(mc.getSession().getUsername()) + " New: " + getHypixelBlockpos(mc.getSession().getUsername()).add(0, -0.1083644, 0));
		//Command.sendPrivateChatMessage("Old: " + getHypixelBlockpos(mc.getSession().getUsername()) + " New: " + target.getPosition());
		//mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(getHypixelBlockpos(mc.getSession().getUsername()), 255, mc.thePlayer.inventory.getCurrentItem(), 0,0,0));
		//mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(getHypixelBlockpos(mc.getSession().getUsername()), 255, mc.thePlayer.inventory.getCurrentItem(), 0,0,0));
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
	
	public static BlockPos getHypixelBlockpos(String str){
    	int val = 89;
    	if(str != null && str.length() > 1){
    		char[] chs = str.toCharArray();
        	
        	int lenght = chs.length;
        	for(int i = 0; i < lenght; i++)
        		val += (int)chs[i] * str.length()* str.length() + (int)str.charAt(0) + (int)str.charAt(1);
        	val/=str.length();
    	}
    	return new BlockPos(val, -val%255, val);
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
    public static Long randomNumber(final Long max, final Long min) {
        return Math.round(min + Math.random() * (max - min));
    }
    
    private void getSmoothRotations(EventMotion e) throws NullPointerException {
    	
    	// Value 0.25 to 10
        float yawFactor = 80;
        float pitchFactor = 80;
        
        // Value 0.01 to 1
        double xz = 0;
        double y = 0;
        float targetYaw = RotationUtils.getYawChange(target.posX + randomNumber() * xz, target.posZ + randomNumber() * xz, this.lastSmoothYaw, e.getX(), e.getZ());

        if (targetYaw > 0 && targetYaw > yawFactor) {
            //mc.thePlayer.rotationYaw += yawFactor;
        	e.setYaw(this.lastSmoothYaw += yawFactor);
        } else if (targetYaw < 0 && targetYaw < -yawFactor) {
            //mc.thePlayer.rotationYaw -= yawFactor;
        	e.setYaw(this.lastSmoothYaw -= yawFactor);
        } else {
            //mc.thePlayer.rotationYaw += targetYaw;
            e.setYaw(this.lastSmoothYaw += targetYaw);
        }

        float targetPitch = RotationUtils.getPitchChange(target, target.posY + randomNumber() * y, this.lastSmoothPitch, e.getX(), e.getZ());

        if (targetPitch > 0 && targetPitch > pitchFactor) {
            //mc.thePlayer.rotationPitch += pitchFactor;
        	e.setPitch(this.lastSmoothPitch += pitchFactor);
        } else if (targetPitch < 0 && targetPitch < -pitchFactor) {
            //mc.thePlayer.rotationPitch -= pitchFactor;
        	e.setPitch(this.lastSmoothPitch -= pitchFactor);
        } else {
            //mc.thePlayer.rotationPitch += targetPitch;
        	e.setPitch(this.lastSmoothPitch += targetPitch);
        }
        
        this.lastSmoothYaw = e.yaw;
        this.lastSmoothPitch = e.pitch;
        
        //mc.thePlayer.rotationYawHead = e.yaw;
        
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
