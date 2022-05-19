package info.spicyclient.modules.movement;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;

import org.lwjgl.input.Keyboard;

import com.ibm.icu.math.BigDecimal;

import info.spicyclient.SpicyClient;
import info.spicyclient.bypass.Hypixel;
import info.spicyclient.chatCommands.Command;
import info.spicyclient.events.Event;
import info.spicyclient.events.listeners.EventMotion;
import info.spicyclient.events.listeners.EventMove;
import info.spicyclient.events.listeners.EventReceivePacket;
import info.spicyclient.events.listeners.EventRenderGUI;
import info.spicyclient.events.listeners.EventSendPacket;
import info.spicyclient.events.listeners.EventUpdate;
import info.spicyclient.modules.Module;
import info.spicyclient.notifications.Color;
import info.spicyclient.notifications.NotificationManager;
import info.spicyclient.notifications.Type;
import info.spicyclient.settings.BooleanSetting;
import info.spicyclient.settings.KeybindSetting;
import info.spicyclient.settings.ModeSetting;
import info.spicyclient.settings.NumberSetting;
import info.spicyclient.settings.SettingChangeEvent;
import info.spicyclient.util.MovementUtils;
import info.spicyclient.util.PlayerUtils;
import info.spicyclient.util.RandomUtils;
import info.spicyclient.util.RotationUtils;
import info.spicyclient.util.Timer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockGlass;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.network.Packet;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.login.client.C00PacketLoginStart;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition;
import net.minecraft.network.play.client.C03PacketPlayer.C06PacketPlayerPosLook;
import net.minecraft.network.play.client.C13PacketPlayerAbilities;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S18PacketEntityTeleport;
import net.minecraft.network.play.server.S27PacketExplosion;
import net.minecraft.potion.Potion;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;

public class Fly extends Module {
	
	public NumberSetting speed = new NumberSetting("Speed", 0.1, 0.01, 2, 0.1);
	public ModeSetting mode = new ModeSetting("Mode", "Vanilla", "Vanilla", "Hypixel", "BrokenLens", "Verus", "Test");
	public BooleanSetting viewBobbingSetting = new BooleanSetting("View Bobbing", false);
	public BooleanSetting stopOnDisable = new BooleanSetting("Stop on disable", true);
	
	public NumberSetting hypixelFreecamHorizontalFlySpeed = new NumberSetting("Horizontal Speed", 5.6, 2, 18, 0.2);
	public NumberSetting hypixelFreecamVerticalFlySpeed = new NumberSetting("Vertical Speed", 0.4, 0.2, 1, 0.01);
	public BooleanSetting hypixelDamage = new BooleanSetting("Damage disabler", false);
	public BooleanSetting hypixelUseFireball = new BooleanSetting("Fireball disabler", false);
	public BooleanSetting hypixelUsePearl = new BooleanSetting("Pearl disabler", true);
	public BooleanSetting hypixelPaperChallenge = new BooleanSetting("Paper Challenge disabler", false);
	public KeybindSetting hypixelTeleportBind = new KeybindSetting("Teleport Bind", Keyboard.KEY_NONE);
	
	/*
	public BooleanSetting hypixelBlink = new BooleanSetting("Blink", true);
	public BooleanSetting hypixelTimerBoost = new BooleanSetting("Hypixel timer boost", true);
	public NumberSetting hypixelSpeed = new NumberSetting("Speed", 0.18, 0.05, 0.2, 0.005);
	public NumberSetting hypixelBoostSpeed = new NumberSetting("Fall speed boost", 2.2, 1.0, 10, 0.1);
	
	public NumberSetting hypixelFastFly1Speed = new NumberSetting("Speed", 0.2675, 0.01, 1.0, 0.0025);
	public BooleanSetting hypixelFastFly1StopOnDisable = new BooleanSetting("Stop on disable", true);
	public BooleanSetting hypixelFastFly1Blink = new BooleanSetting("Blink", false);
	public NumberSetting hypixelFastFly1Decay = new NumberSetting("Decay", 18, 2, 35, 0.01);
	*/
	
	public static ArrayList<Packet> hypixelPackets = new ArrayList<Packet>();
	public static ArrayList<Packet> hypixelFastFly1Packets = new ArrayList<Packet>();
	
	public Fly() {
		super("Fly", Keyboard.KEY_NONE, Category.MOVEMENT);
		resetSettings();
	}

	@Override
	public void resetSettings() {
		this.settings.clear();
		this.addSettings(speed, mode, viewBobbingSetting, stopOnDisable);
	}
	
	@Override
	public void onSettingChange(SettingChangeEvent e) {
		
		if (e.setting.getSetting() == mode) {
			
			if (this.settings.contains(speed)) {
				this.settings.remove(speed);
			}
			
			if (this.settings.contains(hypixelFreecamHorizontalFlySpeed)) {
				this.settings.remove(hypixelFreecamHorizontalFlySpeed);
			}
			
			if (this.settings.contains(hypixelFreecamVerticalFlySpeed)) {
				this.settings.remove(hypixelFreecamVerticalFlySpeed);
			}
			
			if (this.settings.contains(hypixelUseFireball)) {
				this.settings.remove(hypixelUseFireball);
			}
			
			if (this.settings.contains(hypixelUsePearl)) {
				this.settings.remove(hypixelUsePearl);
			}
			
			if (this.settings.contains(hypixelDamage)) {
				this.settings.remove(hypixelDamage);
			}
			
			if (this.settings.contains(hypixelPaperChallenge)) {
				this.settings.remove(hypixelPaperChallenge);
			}
			
			if (this.settings.contains(hypixelTeleportBind)) {
				this.settings.remove(hypixelTeleportBind);
			}
			
			/*
			if (this.settings.contains(hypixelBlink)) {
				this.settings.remove(hypixelBlink);
			}
			
			if (this.settings.contains(hypixelTimerBoost)) {
				this.settings.remove(hypixelTimerBoost);
			}
			
			if (this.settings.contains(hypixelSpeed)) {
				this.settings.remove(hypixelSpeed);
			}
			
			if (this.settings.contains(hypixelBoostSpeed)) {
				this.settings.remove(hypixelBoostSpeed);
			}
			*/
			
			reorderSettings();
			if (mode.is("BrokenLens") || mode.getMode() == "BrokenLens") {
				
			}
			else if (mode.is("Verus") || mode.getMode() == "Verus") {
				
			}
			else if (mode.is("Hypixel") || mode.getMode() == "Hypixel") {
				
				if (!this.settings.contains(hypixelFreecamHorizontalFlySpeed)) {
					this.settings.add(hypixelFreecamHorizontalFlySpeed);
				}
				
				if (!this.settings.contains(hypixelFreecamVerticalFlySpeed)) {
					this.settings.add(hypixelFreecamVerticalFlySpeed);
				}
				
				if (!this.settings.contains(hypixelUseFireball)) {
					this.settings.add(hypixelUseFireball);
				}
				
				if (!this.settings.contains(hypixelUsePearl)) {
					this.settings.add(hypixelUsePearl);
				}
				
				if (!this.settings.contains(hypixelTeleportBind)) {
					this.settings.add(hypixelTeleportBind);
				}
				
				if (!this.settings.contains(hypixelDamage)) {
					//this.settings.add(hypixelDamage);
				}
				
				if (!this.settings.contains(hypixelPaperChallenge)) {
					//this.settings.add(hypixelPaperChallenge);
				}
				
			}
			else {
				if (!this.settings.contains(speed)) {
					this.settings.add(speed);
				}
			}
			
			reorderSettings();
			
		}
		
	}
	
	public static transient int hypixelStage = 0, verusStage = 0;
	public static transient boolean hypixelDamaged = false;
	public static transient float lastPlayerHealth;
	
	public void onEnable() {
		
		hypixelDamaged = false;
		if (mode.getMode().equals("Vanilla")) {
			original_fly_speed = mc.thePlayer.capabilities.getFlySpeed();
			
			if (MovementUtils.isOnGround(0.0001)) {
				mc.thePlayer.posY += 0.5;
			}
			
		}
		else if (mode.is("Test") || mode.getMode() == "Test") {
			
			onTestEnable();
			
		}
		else if (mode.is("BrokenLens") || mode.getMode() == "BrokenLens") {
			
			onBrokenLensEnable();
			
		}
		else if (mode.is("Hypixel") || mode.getMode() == "Hypixel") {
			
			info.spicyclient.bypass.hypixel.Fly.onEnable();
			
		}
		
		verusStage = (int) mc.thePlayer.posY;
		
	}
	
	public void onDisable() {
		
		if (stopOnDisable.isEnabled()) {
			MovementUtils.setMotion(0);
			mc.thePlayer.motionY = 0;
		}
		
		hypixelDamaged = false;
		mc.thePlayer.stepHeight = 0.6f;
		
		if (mode.getMode().equals("Vanilla")) {
			mc.thePlayer.capabilities.setFlySpeed(original_fly_speed);
			mc.thePlayer.capabilities.isFlying = false;
			mc.thePlayer.capabilities.allowFlying = false;
		}
		else if (mode.is("Test") || mode.getMode() == "Test") {
			
			onTestDisable();
			
		}
		else if (mode.is("BrokenLens") || mode.getMode() == "BrokenLens") {
			
			onBrokenLensDisable();
			
		}
		else if (mode.is("Hypixel") || mode.getMode() == "Hypixel") {
			
			info.spicyclient.bypass.hypixel.Fly.onDisable();
			
		}
		
	}
	
	private static float original_fly_speed;
	private static transient int viewBobbing = 0, hypixelLagback = 0;

	private transient long hypixelStartTime = System.currentTimeMillis();

	private transient Timer timer = new Timer();

	public void onEvent(Event e) {
		
		if (mode.is("Test") || mode.getMode() == "Test") {
			
			onTestEvent(e);
			
		}
		else if (mode.is("BrokenLens") || mode.getMode() == "BrokenLens") {
			
			onBrokenLensEvent(e);
			
		}
		else if (mode.is("Hypixel") || mode.getMode() == "Hypixel") {
			
			info.spicyclient.bypass.hypixel.Fly.onEvent(e, this, mc);
			
		}
		
		// For the viewbobbing
		if (e instanceof EventMotion && e.isPre() && (mc.gameSettings.keyBindForward.isKeyDown() || mc.gameSettings.keyBindBack.isKeyDown() || mc.gameSettings.keyBindLeft.isKeyDown() || mc.gameSettings.keyBindRight.isKeyDown()) && viewBobbingSetting.isEnabled()) {
			
			switch (viewBobbing) {
			case 0:
				mc.thePlayer.cameraYaw = 0.105F;
				mc.thePlayer.cameraPitch = 0.105F;
				viewBobbing++;
				break;
			case 1:
				viewBobbing++;
				break;
			case 2:
				viewBobbing = 0;
				break;
			}
			
		}
		
		if (e instanceof EventUpdate && e.isPre()) {

			if (timer.hasTimeElapsed(2000 + new Random().nextInt(500), true)) {
				// SpicyClient.config.blink.toggle();
			}
			
		}
		
		if (e instanceof EventUpdate || e instanceof EventMotion) {
			
			if (mode.getMode().equals("Verus")) {
				
				mc.thePlayer.onGround = true;
				mc.thePlayer.motionY = 0;
				//MovementUtils.setMotion(speed.getValue());
				
			}
			
		}
		
		if (mode.getMode().equals("Vanilla")) {
			try {
				mc.thePlayer.capabilities.setFlySpeed(original_fly_speed);
			} catch (NullPointerException e2) {
				e2.printStackTrace();
			}
			try {
				mc.thePlayer.capabilities.isFlying = false;
			} catch (NullPointerException e2) {
				e2.printStackTrace();
			}
			try {
				mc.thePlayer.capabilities.allowFlying = false;
			} catch (NullPointerException e2) {
				e2.printStackTrace();
			}
			
		}
		
		if (mode.getMode().equals("Vanilla")) {
			mc.thePlayer.capabilities.isFlying = true;
			mc.thePlayer.capabilities.setFlySpeed((float) speed.getValue());
		}
		
	}
	
	@Override
	public void onEventWhenDisabled(Event e) {
		
		if (mode.is("Hypixel") || mode.getMode() == "Hypixel") {
			
			info.spicyclient.bypass.hypixel.Fly.onEventWhileDisabled(e, this, mc);
			
		}
		
	}
	
	// Found on github
    public void damage(){
    	
    	int damage = 1;
		if (damage > MathHelper.floor_double(mc.thePlayer.getMaxHealth()))
			damage = MathHelper.floor_double(mc.thePlayer.getMaxHealth());

		double offset = 0.0625;
		if (mc.thePlayer != null && mc.getNetHandler() != null && mc.thePlayer.onGround) {
			for (short i = 0; i <= ((3 + damage) / offset); i++) {
				mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX,
						mc.thePlayer.posY + offset, mc.thePlayer.posZ, false));
				mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX,
						mc.thePlayer.posY, mc.thePlayer.posZ, (i == ((3 + damage) / offset))));
			}
		}
    	
    	/*
    	for (int i = 0; i < 10; i++) {
            mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, true));
        }

        float fallDistance = 3.0125f; //does half a heart of damage
    	//float fallDistance = 8.0125f;

        while (fallDistance > 0) {
            mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.0624986421, mc.thePlayer.posZ, false));
            mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.0625, mc.thePlayer.posZ, false));
            mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.0624986421, mc.thePlayer.posZ, false));
            mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.0000013579, mc.thePlayer.posZ, false));
            
            fallDistance -= 0.0624986421f;
        }

        mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, true));

        mc.thePlayer.jump();

        mc.thePlayer.posY += 0.42f;
        
    	/*
        double fallDistance = 0;
        double offset = 0.41999998688698;
        while (fallDistance < 6)
        {
            sendPacket(offset,false);
            sendPacket(0, fallDistance + offset >= 4);
            fallDistance += offset;
        }
        */
		
    }
    void sendPacket(double addY,boolean ground){
        mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(
                mc.thePlayer.posX,mc.thePlayer.posY+addY,mc.thePlayer.posZ,ground
        ));
    }
    
    
    // Hypixel fast fly code
    
	public int hypixelFastFlyStatus = 0, hypixelFastFly1 = 0;
	public double speedAndStuff = 0;
	public static transient boolean hypixelFastFly1Damaged = false;
	/*
	public void onEnableHypixelFastfly1() {
		
		hypixelFastFlyStatus = 0;
		hypixelFastFly1 = 0;
		speedAndStuff = 0;
		hypixelFastFly1Damaged = false;
		
        //PlayerCapabilities playerCapabilities = new PlayerCapabilities();
        //playerCapabilities.isFlying = true;
        //playerCapabilities.allowFlying = true;
        //playerCapabilities.setFlySpeed((float) ((Math.random() * (9.0 - 0.1)) + 0.1));
        //playerCapabilities.isCreativeMode = true;
        //mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C13PacketPlayerAbilities(playerCapabilities));
		
		//SpicyClient.config.fly.damage();
		
		double damage = 0;
		
		if (mode.is("HypixelFast1") || mode.getMode() == "HypixelFast1") {
			damage = 2;
		}
		
		else if (mode.is("HypixelFast2") || mode.getMode() == "HypixelFast2") {
			
			damage = 1;
			
		}
		
		Hypixel.damageHypixel(damage);
		
		mc.thePlayer.onGround = false;
		MovementUtils.setMotion(0);
		mc.thePlayer.jumpMovementFactor = 0;
		
		hypixelFastFly1Packets.clear();
		
	}
	
	public void onDisablehypixelFastFly1() {
		
		if (hypixelFastFly1StopOnDisable.isEnabled()) {
			
			mc.thePlayer.motionX = 0;
			mc.thePlayer.motionZ = 0;
			
		}
		
		if (hypixelFastFly1Blink.isEnabled()) {
			
			for (Packet p : hypixelFastFly1Packets) {
				
				if (mc.isSingleplayer()) {
					
				}else {
					//mc.getNetHandler().getNetworkManager().sendPacketNoEvent(p);
				}
				
			}
			hypixelFastFly1Packets.clear();
			
		}
		
	}
	
    public void onEventHypixelFastfly1(Event e) {
    	
    	if (e instanceof EventSendPacket & e.isPre()) {
    		
			if (((EventSendPacket)e).packet instanceof C03PacketPlayer && hypixelFastFly1Damaged) {
				((C03PacketPlayer)((EventSendPacket)e).packet).setOnGround(true);
			}
			
		}
    	
		if (e instanceof EventSendPacket) {
			
			Block block = mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 0.2, mc.thePlayer.posZ)).getBlock();
			
			if (e.isPre() && !MovementUtils.isOnGround(0.0000001) && !block.isFullBlock() && !(block instanceof BlockGlass) && hypixelFastFly1Damaged) {
				
				if (((EventSendPacket)e).packet instanceof C00PacketKeepAlive || ((EventSendPacket)e).packet instanceof C00Handshake || ((EventSendPacket)e).packet instanceof C00PacketLoginStart) {
					return;
				}
				
				EventSendPacket sendPacket = (EventSendPacket) e;
				
				if (hypixelFastFly1Blink.isEnabled()) {
					hypixelFastFly1Packets.add(sendPacket.packet);
					sendPacket.setCanceled(true);
				}
				
			}
			
		}
    	
		if (e instanceof EventUpdate) {
            EventUpdate em = (EventUpdate) e;
            
            //double speed = Math.max(hypixelFastFly1Speed.getValue(), 0.2873D);
            double speed = hypixelFastFly1Speed.getValue();
            
            if (true) {
            	if(!em.isPre())
            		return;
            	hypixelFastFly1++;
                if (true) {
                    if (mc.thePlayer.onGround && mc.thePlayer.isCollidedVertically && MovementUtils.isOnGround(0.01)) {
                        
                    	if(mc.thePlayer.hurtResistantTime == 19){
                    		//MovementUtils.setMotion(0.3 + 0 * 0.05f);
                    		//mc.thePlayer.motionY = 0.41999998688698f + 0*0.1;
                    		hypixelFastFly1 = 25;
                    		speedAndStuff = 13;
                    		hypixelFastFly1Damaged = true;
                    	}else if(hypixelFastFly1 < 25){
                    		mc.thePlayer.motionX = 0;
                            mc.thePlayer.motionZ = 0;
                            mc.thePlayer.jumpMovementFactor = 0;
                            mc.thePlayer.onGround = false;
                            //mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX + ((new Random().nextDouble() - 0.5) / 10), mc.thePlayer.posY, mc.thePlayer.posZ + ((new Random().nextDouble() - 0.5) / 10), MovementUtils.isOnGround(0.0001)));
                    	}
                    	else if (hypixelFastFly1Damaged) {
                    		
                    		Double randSpeed = new Random().nextDouble() / 1000000000;
                    		//Command.sendPrivateChatMessage(randSpeed);
                    		MovementUtils.setMotion(0.3 - randSpeed);
                    		mc.thePlayer.motionY = 0.40999998688698f;
                    		
                    	}
                    	
                    }
                    
                }
                Block block = mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 0.2, mc.thePlayer.posZ)).getBlock();
                if (!MovementUtils.isOnGround(0.0000001) && !block.isFullBlock() && !(block instanceof BlockGlass)) {
                    mc.thePlayer.motionY = 0;
                    mc.thePlayer.motionX = 0;
                    mc.thePlayer.motionZ = 0;
                    float speedf = (float) (hypixelFastFly1Speed.getValue() + 0 * 0.06f);
                    if (speedAndStuff > 0) {
                        if ((mc.thePlayer.moveForward == 0 && mc.thePlayer.moveStrafing == 0) || mc.thePlayer.isCollidedHorizontally)
                            speedAndStuff = 0;
                        
                        //speedf += speedAndStuff / 18;
                        speedf += speedAndStuff / hypixelFastFly1Decay.getValue();
                        
                        //dub-= 0.175 + 0*0.006; //0.152
                        
                        speedAndStuff-= 0.155;
                        
                        //if(((Options)settings.get("dubMODE").getValue()).getSelected().equalsIgnoreCase("OldFast")){
                        	//dub-= 1.3;
                        //}else if(((Options)settings.get("dubMODE").getValue()).getSelected().equalsIgnoreCase("Fast3")){
                        	//dub-= 0.175 + 0*0.006; //0.152
                        //}else{
                        	//dub-= 0.155 + 0*0.006; //0.152
                        //}
                    
                    }else {
                    	double baseSpeed = 0.2873D;
                        if (mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
                           int amplifier = mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier();
                           baseSpeed *= 1.0D + 0.2D * (double)(amplifier + 1);
                        }
                    	speedf = (float) baseSpeed;
                    }
                    
                    //setSpecialMotion(speedf);
                    
                    double forward = mc.thePlayer.movementInput.moveForward;
                    double strafe = mc.thePlayer.movementInput.moveStrafe;
                    float yaw = mc.thePlayer.rotationYaw;
                    if ((forward == 0.0D) && (strafe == 0.0D)) {
                    	mc.thePlayer.motionX = 0;
                    	mc.thePlayer.motionZ = 0;
                    } else {
                        if (forward != 0.0D) {
                        	if(speedAndStuff <= 0)
                        	 if (strafe > 0.0D) {
                                 yaw += (forward > 0.0D ? -45 : 45);
                             } else if (strafe < 0.0D) {
                                 yaw += (forward > 0.0D ? 45 : -45);
                             }
                             strafe = 0.0D;
                            if (forward > 0.0D) {
                                forward = 1;
                            } else if (forward < 0.0D) {
                                forward = -1;
                            }
                        }
                        mc.thePlayer.motionX = forward * speedf * Math.cos(Math.toRadians(yaw + 90.0F)) + strafe * speedf * Math.sin(Math.toRadians(yaw + 90.0F));
                        mc.thePlayer.motionZ = forward * speedf * Math.sin(Math.toRadians(yaw + 90.0F)) - strafe * speedf * Math.cos(Math.toRadians(yaw + 90.0F));
                    }
                    
                   // MovementUtils.setMotion(speedf);
                    
                    mc.thePlayer.jumpMovementFactor = 0;
                    mc.thePlayer.onGround = false;
                    if (mc.gameSettings.keyBindJump.pressed) {
                        mc.thePlayer.motionY = 0.4;
                    }
                    
                    hypixelFastFlyStatus = Hypixel.cycleFlyHypixel(hypixelFastFlyStatus);
                    
                }

            }
            
		}
		
	}
    
    public void onEventHypixelFastfly2(Event e) {
    	
    	if (e instanceof EventSendPacket & e.isPre()) {
    		
			if (((EventSendPacket)e).packet instanceof C03PacketPlayer && hypixelFastFly1Damaged) {
				((C03PacketPlayer)((EventSendPacket)e).packet).setOnGround(true);
			}
			
		}
    	
    	if (e instanceof EventUpdate && e.isPre() && mc.thePlayer.fallDistance >= 3) {
    		
    		MovementUtils.setMotion(4);
			this.additionalInformation = "MEGA SPEED BOOST!!!";
			mc.thePlayer.motionY = -0.005;
			
            if (mc.gameSettings.keyBindJump.pressed) {
                mc.thePlayer.motionY = 0.4;
            }
			
			//mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C03PacketPlayer(true));
			hypixelLagback = 0;
			
			hypixelFastFlyStatus = Hypixel.cycleFlyHypixel(hypixelFastFlyStatus);
			
    	}else {
    		
    		if (e instanceof EventSendPacket) {
    			
    			Block block = mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 0.2, mc.thePlayer.posZ)).getBlock();
    			
    			if (e.isPre() && !MovementUtils.isOnGround(0.0000001) && !block.isFullBlock() && !(block instanceof BlockGlass) && hypixelFastFly1Damaged) {
    				
    				if (((EventSendPacket)e).packet instanceof C00PacketKeepAlive || ((EventSendPacket)e).packet instanceof C00Handshake || ((EventSendPacket)e).packet instanceof C00PacketLoginStart) {
    					return;
    				}
    				
    				EventSendPacket sendPacket = (EventSendPacket) e;
    				
    				if (hypixelFastFly1Blink.isEnabled()) {
    					hypixelFastFly1Packets.add(sendPacket.packet);
    					sendPacket.setCanceled(true);
    				}
    				
    			}
    			
    		}
        	
    		if (e instanceof EventUpdate) {
                EventUpdate em = (EventUpdate) e;
                
                //double speed = Math.max(hypixelFastFly1Speed.getValue(), 0.2873D);
                double speed = hypixelFastFly1Speed.getValue();
                
                if (true) {
                	if(!em.isPre())
                		return;
                	hypixelFastFly1++;
                    if (true) {
                        if (mc.thePlayer.onGround && mc.thePlayer.isCollidedVertically && MovementUtils.isOnGround(0.01)) {
                            
                        	if(mc.thePlayer.hurtResistantTime == 19){
                        		
                        		hypixelFastFly1 = 25;
                        		speedAndStuff = 20;
                        		hypixelFastFly1Damaged = true;
                        		
                        	}else if(hypixelFastFly1 < 25){
                        		//mc.thePlayer.motionX = 0;
                                //mc.thePlayer.motionZ = 0;
                                //mc.thePlayer.jumpMovementFactor = 0;
                                //mc.thePlayer.onGround = false;
                        	}
                        	else if (hypixelFastFly1Damaged) {
                        		
                        		Double randSpeed = new Random().nextDouble() / 1000000000;
                        		//Command.sendPrivateChatMessage(randSpeed);
                        		MovementUtils.setMotion(0.3 - randSpeed);
                        		mc.thePlayer.motionY = 0.40999998688698f;
                        		
                        	}
                        	
                        }
                    }
                    Block block = mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 0.2, mc.thePlayer.posZ)).getBlock();
                    if (!MovementUtils.isOnGround(0.0000001) && !block.isFullBlock() && !(block instanceof BlockGlass)) {
                        mc.thePlayer.motionY = 0;
                        mc.thePlayer.motionX = 0;
                        mc.thePlayer.motionZ = 0;
                        float speedf = (float) (hypixelFastFly1Speed.getValue() + 0 * 0.06f);
                        if (speedAndStuff > 0) {
                            if ((mc.thePlayer.moveForward == 0 && mc.thePlayer.moveStrafing == 0) || mc.thePlayer.isCollidedHorizontally)
                                speedAndStuff = 0;
                            
                            //speedf += speedAndStuff / 18;
                            speedf += speedAndStuff / hypixelFastFly1Decay.getValue();
                            
                            double baseSpeed = 0.2873D;
                            if (mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
                               int amplifier = mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier();
                               baseSpeed *= 1.0D + 0.2D * (double)(amplifier + 1);
                            }
                            
                            if (speedf < baseSpeed) {
                            	
                            	speedf = (float) baseSpeed;
                            	
                            }
                            
                            //dub-= 0.175 + 0*0.006; //0.152
                            
                            speedAndStuff-= 0.155;
                            
                            //if(((Options)settings.get("dubMODE").getValue()).getSelected().equalsIgnoreCase("OldFast")){
                            	//dub-= 1.3;
                            //}else if(((Options)settings.get("dubMODE").getValue()).getSelected().equalsIgnoreCase("Fast3")){
                            	//dub-= 0.175 + 0*0.006; //0.152
                            //}else{
                            	//dub-= 0.155 + 0*0.006; //0.152
                            //}
                        
                        }else {
                            double baseSpeed = 0.2873D;
                            if (mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
                               int amplifier = mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier();
                               baseSpeed *= 1.0D + 0.2D * (double)(amplifier + 1);
                            }
                            
                            speedf = (float) baseSpeed;
                            
                        }
                        
                        //setSpecialMotion(speedf);
                        
                        double forward = mc.thePlayer.movementInput.moveForward;
                        double strafe = mc.thePlayer.movementInput.moveStrafe;
                        float yaw = mc.thePlayer.rotationYaw;
                        if ((forward == 0.0D) && (strafe == 0.0D)) {
                        	//mc.thePlayer.motionX = 0;
                        	//mc.thePlayer.motionZ = 0;
                        } else {
                        	MovementUtils.strafe(speedf);
                        }
                        
                       // MovementUtils.setMotion(speedf);
                        
                        mc.thePlayer.jumpMovementFactor = 0;
                        mc.thePlayer.onGround = false;
                        if (mc.gameSettings.keyBindJump.pressed) {
                            mc.thePlayer.motionY = 0.4;
                        }
                        
                        hypixelFastFlyStatus = Hypixel.cycleFlyHypixel(hypixelFastFlyStatus);
                        
                        //Command.sendPrivateChatMessage(new DecimalFormat("#.######################################################################").format(mc.thePlayer.posY));
                        
                    }

                }
                
    		}
    		
    	}
    	
	}
    */
    public static transient boolean BrokenLens = false;
    
    public void onBrokenLensEnable() {
    	
    }
    
    public void onBrokenLensDisable() {
    	
    }
    
    public void onBrokenLensEvent(Event e) {
    	
		if (e instanceof EventReceivePacket) {
			
			Packet p = ((EventReceivePacket)e).packet;
			
			if (p instanceof S08PacketPlayerPosLook) {
				e.setCanceled(true);
			}
			
		}
		
		if (e instanceof EventMove && e.isPre()) {
			
			EventMove event = (EventMove)e;
			
			if (SpicyClient.config.bhop.isEnabled()) {
				SpicyClient.config.bhop.toggle();
			}
			
			mc.thePlayer.fallDistance = 0;
			
			event.x = 0;
			event.y = 0;
			event.z = 0;
			mc.thePlayer.motionX = 0;
			mc.thePlayer.motionY = 0;
			mc.thePlayer.motionZ = 0;
			
			event.y = 0;
			MovementUtils.strafe(2);
			event.x = mc.thePlayer.motionX;
			event.z = mc.thePlayer.motionZ;
			if (!BrokenLens) {
				mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.5, mc.thePlayer.posZ, true));
				BrokenLens = true;
			}
			else {
				mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY - 0.5, mc.thePlayer.posZ, true));
				BrokenLens = false;
			}
			
			if (mc.gameSettings.keyBindSneak.isKeyDown()) {
				
				mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.5, mc.thePlayer.posZ, true));
				event.y = -0.5;
				
			}
			
			if (mc.gameSettings.keyBindJump.isKeyDown()) {
				
				mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY - 0.5, mc.thePlayer.posZ, true));
				event.y = 0.5;
				
			}
			
		}
		
    }
    
    public static transient int testStage = 0;
    public static transient double testDub1 = 0, testDub2 = 0;
    public static transient boolean testBool1 = false, testBool2 = false;
    
    public void onTestEnable() {
    	
    	mc.thePlayer.jump();
    	PlayerCapabilities caps = mc.thePlayer.capabilities;
    	caps.isFlying = true;
    	mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C13PacketPlayerAbilities(caps));
    }
    
    public void onTestDisable() {
		mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY, 
				mc.thePlayer.posZ);
		mc.timer.timerSpeed = 1;
    }
    
	public void onTestEvent(Event e) {

		if (e instanceof EventSendPacket && e.isPre() && ((EventSendPacket) e).packet instanceof C03PacketPlayer) {
			// e.setCanceled(true);
		}

		if (e instanceof EventUpdate && e.isPre()) {
			
			if (mc.gameSettings.keyBindJump.isKeyDown()) {
				mc.thePlayer.motionY = 1;
			}			
			else if (mc.gameSettings.keyBindSneak.isKeyDown()) {
				mc.thePlayer.motionY = -1;
			}
			else {
				mc.thePlayer.motionY = 0;
			}
			
			mc.thePlayer.onGround = true;
			
			MovementUtils.strafe((float) speed.getValue() * 3);
			
			if (!MovementUtils.isMoving()) {
				MovementUtils.setMotion(0);
			}
			
		}
    }
    
}
