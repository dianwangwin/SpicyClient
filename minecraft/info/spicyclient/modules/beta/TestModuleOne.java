package info.spicyclient.modules.beta;

import org.lwjgl.input.Keyboard;

import info.spicyclient.SpicyClient;
import info.spicyclient.chatCommands.Command;
import info.spicyclient.events.Event;
import info.spicyclient.events.listeners.EventUpdate;
import info.spicyclient.modules.Module;
import info.spicyclient.modules.movement.Fly;
import info.spicyclient.util.MovementUtils;
import info.spicyclient.util.Timer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockGlass;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.network.play.client.C13PacketPlayerAbilities;
import net.minecraft.util.BlockPos;

public class TestModuleOne extends Module {

	public TestModuleOne() {
		super("TestModuleOne", Keyboard.KEY_NONE, Category.BETA);
		// TODO Auto-generated constructor stub
	}
	
	public static transient Timer timer = new Timer();
	
	public int status = 0, test = 0;
	public double dub = 0;
	public float flo = 0;
	
	@Override
	public void onEnable() {
		status = 0;
		dub = 0;
		flo = 0;
		test = 0;
		
        PlayerCapabilities playerCapabilities = new PlayerCapabilities();
        playerCapabilities.isFlying = true;
        playerCapabilities.allowFlying = true;
        //playerCapabilities.setFlySpeed((float) ((Math.random() * (9.0 - 0.1)) + 0.1));
        playerCapabilities.setFlySpeed((float) ((Math.random() * (9.0 - 0.1)) + 0.1));
        playerCapabilities.isCreativeMode = true;
        mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C13PacketPlayerAbilities(playerCapabilities));
		
		SpicyClient.config.fly.damage();
		mc.thePlayer.onGround = false;
		MovementUtils.setMotion(0);
		mc.thePlayer.jumpMovementFactor = 0;
		
	}
	
	@Override
	public void onDisable() {
		
		mc.thePlayer.motionX = 0;
		mc.thePlayer.motionZ = 0;
		
	}
	
	@Override
	public void onEvent(Event e) {
		
		if (e instanceof EventUpdate) {
            EventUpdate em = (EventUpdate) e;
            double speed = Math.max(0.03, 0.2873D);
            Command.sendPrivateChatMessage(speed);
            if (true) {
            	if(!em.isPre())
            		return;
            	test++;
                if (true) {
                    if (mc.thePlayer.onGround && mc.thePlayer.isCollidedVertically && MovementUtils.isOnGround(0.01)) {
                        
                    	if(mc.thePlayer.hurtResistantTime == 19){
                    		MovementUtils.setMotion(0.3 + 0 * 0.05f);
                    		mc.thePlayer.motionY = 0.41999998688698f + 0*0.1;
                    		test = 25;
                    		dub = 13;
                    	}else if(test < 25){
                    		mc.thePlayer.motionX = 0;
                            mc.thePlayer.motionZ = 0;
                            mc.thePlayer.jumpMovementFactor = 0;
                            mc.thePlayer.onGround = false;
                    	}
                    	
                    }
                }
                Block block = mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 0.2, mc.thePlayer.posZ)).getBlock();
                if (!MovementUtils.isOnGround(0.0000001) && !block.isFullBlock() && !(block instanceof BlockGlass)) {
                    mc.thePlayer.motionY = 0;
                    mc.thePlayer.motionX = 0;
                    mc.thePlayer.motionZ = 0;
                    float speedf = 0.29f + 0 * 0.06f;
                    if (dub > 0) {
                        if ((mc.thePlayer.moveForward == 0 && mc.thePlayer.moveStrafing == 0) || mc.thePlayer.isCollidedHorizontally)
                            dub = 0;                        
                        speedf += dub / 18;
                        
                        //dub-= 0.175 + 0*0.006; //0.152
                        
                        dub-= 0.155 + 0*0.006;
                        
                        /*
                        if(((Options)settings.get("dubMODE").getValue()).getSelected().equalsIgnoreCase("OldFast")){
                        	dub-= 1.3;
                        }else if(((Options)settings.get("dubMODE").getValue()).getSelected().equalsIgnoreCase("Fast3")){
                        	dub-= 0.175 + 0*0.006; //0.152
                        }else{
                        	dub-= 0.155 + 0*0.006; //0.152
                        }
                        */
                        
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
                        	if(dub <= 0)
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
                    //status++;
                    //mc.thePlayer.lastReportedPosY = 0;
                    
                    //double offset2 = 4.496001251836E-5;
                    double offset2 = 9.274936900641403E-14D;
                    
                    switch (status) {
                    
    				case 0:
    					//mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
    					mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY,
    							mc.thePlayer.posZ);
    					status++;
    					break;
    				case 1:
    					// mc.thePlayer.posY = mc.thePlayer.posY + 9.947598300641403E-14;
    					// mc.thePlayer.posY = mc.thePlayer.lastTickPosY + 0.0002000000000066393;
    					//mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.0002000000000066393,
    							//mc.thePlayer.posZ);
    					if (!MovementUtils.isOnGround(0.0001)) {
    						mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + -offset2, mc.thePlayer.posZ);
    					}
    					
    					//event.setY(mc.thePlayer.posY);
    					status++;
    					break;
    				case 2:;
    					//mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
    					mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + (offset2),
    							mc.thePlayer.posZ);
    					status = 0;
    					break;
                    
					}

                }

            }
		}
	}
	
	@Override
	public void onEventWhenDisabled(Event e) {
		
	}
	
}
