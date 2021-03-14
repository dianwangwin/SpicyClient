package info.spicyclient.modules.player;

import org.lwjgl.input.Keyboard;

import info.spicyclient.chatCommands.Command;
import info.spicyclient.events.Event;
import info.spicyclient.events.listeners.EventReceivePacket;
import info.spicyclient.events.listeners.EventSendPacket;
import info.spicyclient.events.listeners.EventUpdate;
import info.spicyclient.modules.Module;
import info.spicyclient.notifications.Color;
import info.spicyclient.notifications.NotificationManager;
import info.spicyclient.notifications.Type;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C0CPacketBoatInput;

public class EntityDesync extends Module {

	public EntityDesync() {
		super("EntityDesync", Keyboard.KEY_NONE, Category.PLAYER);
	}
	
	private static transient Entity riding = null;
	
	@Override
	public void onEnable() {
		
		if (mc.thePlayer == null) {
			toggle();
			return;
		}
		
		if (!mc.thePlayer.isRiding()) {
			toggle();
			NotificationManager.getNotificationManager().createNotification("Entity Desync", "Make sure you're riding an entity first", true, 5000, Type.WARNING, Color.RED);
			return;
		}
		
        riding = mc.thePlayer.ridingEntity;

        mc.thePlayer.dismountEntity(riding);
        mc.theWorld.removeEntity(riding);
        mc.thePlayer.setPosition(riding.posX, riding.posY, riding.posZ);
		
	}
	
	@Override
	public void onDisable() {
		
		if (riding != null && !riding.isDead) {
			
			 mc.theWorld.spawnEntityInWorld(riding);
             mc.thePlayer.ridingEntity = riding;
             mc.thePlayer.updateRiderPosition();
			
		}
		
	}
	
	@Override
	public void onEvent(Event e) {
		
		if (e instanceof EventUpdate && e.isPre()) {
			
	        if (riding == null)
	            return;

	        if (mc.thePlayer.isRiding())
	            return;
	        
	        mc.thePlayer.onGround = true;
	        
	        riding.setPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
	        
	        mc.getNetHandler().getNetworkManager().sendPacket(new C0CPacketBoatInput(mc.thePlayer.moveStrafing, mc.thePlayer.moveForward, false, false));
	        mc.getNetHandler().getNetworkManager().sendPacket(new C0CPacketBoatInput(mc.thePlayer.moveStrafing, mc.thePlayer.moveForward, false, false));
	        mc.getNetHandler().getNetworkManager().sendPacket(new C0CPacketBoatInput(mc.thePlayer.moveStrafing, mc.thePlayer.moveForward, false, false));
	        mc.getNetHandler().getNetworkManager().sendPacket(new C0CPacketBoatInput(mc.thePlayer.moveStrafing, mc.thePlayer.moveForward, false, false));
	        mc.getNetHandler().getNetworkManager().sendPacket(new C0CPacketBoatInput(mc.thePlayer.moveStrafing, mc.thePlayer.moveForward, false, false));
	        mc.getNetHandler().getNetworkManager().sendPacket(new C0CPacketBoatInput(mc.thePlayer.moveStrafing, mc.thePlayer.moveForward, false, false));
	        mc.getNetHandler().getNetworkManager().sendPacket(new C0CPacketBoatInput(mc.thePlayer.moveStrafing, mc.thePlayer.moveForward, false, false));
	        mc.getNetHandler().getNetworkManager().sendPacket(new C0CPacketBoatInput(mc.thePlayer.moveStrafing, mc.thePlayer.moveForward, false, false));
			
		}
		
		if (e instanceof EventSendPacket && e.isPre()) {
			
			Packet packet = ((EventSendPacket)e).packet;
			
			if (packet instanceof C0CPacketBoatInput && e.isPre()) {
				
				((C0CPacketBoatInput)packet).setJumping(true);
				
			}
			
		}
		
	}
	
}
