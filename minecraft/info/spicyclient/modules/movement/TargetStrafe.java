package info.spicyclient.modules.movement;

import org.lwjgl.input.Keyboard;

import info.spicyclient.SpicyClient;
import info.spicyclient.chatCommands.Command;
import info.spicyclient.events.Event;
import info.spicyclient.events.listeners.EventMotion;
import info.spicyclient.events.listeners.EventMove;
import info.spicyclient.events.listeners.EventReceivePacket;
import info.spicyclient.events.listeners.EventRender3D;
import info.spicyclient.events.listeners.EventSendPacket;
import info.spicyclient.events.listeners.EventUpdate;
import info.spicyclient.modules.Module;
import info.spicyclient.modules.combat.Killaura;
import info.spicyclient.modules.movement.Fly;
import info.spicyclient.notifications.Color;
import info.spicyclient.notifications.NotificationManager;
import info.spicyclient.notifications.Type;
import info.spicyclient.settings.BooleanSetting;
import info.spicyclient.settings.NumberSetting;
import info.spicyclient.util.MovementUtils;
import info.spicyclient.util.RenderUtils;
import info.spicyclient.util.RotationUtils;
import info.spicyclient.util.Timer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockGlass;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C13PacketPlayerAbilities;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

public class TargetStrafe extends Module {

	public TargetStrafe() {
		super("TargetStrafe", Keyboard.KEY_NONE, Category.MOVEMENT);
		resetSettings();
	}
	
	public NumberSetting speed = new NumberSetting("Speed", 0, 0, 1, 0.01);
	public NumberSetting distance = new NumberSetting("Distance", 3, 0.1, 6, 0.1);
	
	public static transient boolean direction = false, forward = false, left = false, right = false, back = false;
	
	@Override
	public void resetSettings() {
		this.settings.clear();
		this.addSettings(speed, distance);
	}
	
	@Override
	public void onEnable() {
		
		forward = mc.gameSettings.keyBindForward.pressed;
		left = mc.gameSettings.keyBindLeft.pressed;
		right = mc.gameSettings.keyBindRight.pressed;
		back = mc.gameSettings.keyBindBack.pressed;
		
	}
	
	@Override
	public void onEvent(Event e) {
		
		if (e instanceof EventMove && e.isPre()) {
			
			EventMove event = (EventMove)e;
			
			if (mc.thePlayer.isCollidedHorizontally) {
				direction = !direction;
			}
			
			Killaura k = SpicyClient.config.killaura;
			
			if (k.target == null || !k.isEnabled()) {
				return;
			}else {
				
				double currentSpeed = MovementUtils.getSpeed();
				
				//event.setSpeed(0);
				
				double yawChange = 45;
				
				float f = (float) ((RotationUtils.getRotations(k.target)[0] + (direction ? -yawChange : yawChange)) * 0.017453292F);
				double x2 = k.target.posX, z2 = k.target.posZ;
	            x2 -= (double)(MathHelper.sin(f) * (distance.getValue()) * -1);
	            z2 += (double)(MathHelper.cos(f) * (distance.getValue()) * -1);
	            
	            float currentSpeed1 = MovementUtils.getSpeed();
	            
				double backupMotX = mc.thePlayer.motionX, backupMotZ = mc.thePlayer.motionZ;
	            event.setSpeed(((currentSpeed + speed.getValue()) / 100) * 90, RotationUtils.getRotationFromPosition(x2, z2, mc.thePlayer.posY)[0]);
	            mc.thePlayer.motionX = backupMotX;
	            mc.thePlayer.motionZ = backupMotZ;
	            
	            if (currentSpeed > MovementUtils.getSpeed()) {
	            	direction = !direction;
	            }
	            
			}
			
		}
		
		if (e instanceof EventRender3D && e.isPre()) {
			
			if (mc.thePlayer.isCollidedHorizontally) {
				direction = !direction;
			}
			
			Killaura k = SpicyClient.config.killaura;
			
			if (k.target == null || !k.isEnabled()) {
				return;
			}else {
				
				Vec3 lastLine = new Vec3(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
				
				for (short i = 0; i <= 360; i++) {
					
					float f = (RotationUtils.getRotations(k.target)[0] + (direction ? -i : i)) * 0.017453292F;
					double x2 = k.target.posX, z2 = k.target.posZ;
		            x2 -= (double)(MathHelper.sin(f) * distance.getValue()) * -1;
		            z2 += (double)(MathHelper.cos(f) * distance.getValue()) * -1;
		            
		            if (i != 0) {
		            	RenderUtils.drawLine(lastLine.xCoord, lastLine.yCoord, lastLine.zCoord, x2, lastLine.yCoord, z2);
		            }
		            
		            //RenderUtils.drawLine(lastLine.xCoord, lastLine.yCoord, lastLine.zCoord, x2, lastLine.yCoord, z2);
		            lastLine.xCoord = x2;
		            lastLine.zCoord = z2;
					
				}
				
				RenderUtils.drawLine(k.target.posX, mc.thePlayer.posY, k.target.posZ, k.target.posX, k.target.posY, k.target.posZ);
				
			}
			
		}
		
	}
	
	@Override
	public void onEventWhenDisabled(Event e) {
		
	}
	
}
