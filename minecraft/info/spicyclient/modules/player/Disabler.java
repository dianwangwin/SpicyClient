package info.spicyclient.modules.player;

import java.util.Random;

import org.lwjgl.input.Keyboard;

import info.spicyclient.SpicyClient;
import info.spicyclient.chatCommands.Command;
import info.spicyclient.events.Event;
import info.spicyclient.events.listeners.EventPacket;
import info.spicyclient.events.listeners.EventSendPacket;
import info.spicyclient.events.listeners.EventUpdate;
import info.spicyclient.modules.Module;
import info.spicyclient.notifications.Color;
import info.spicyclient.notifications.NotificationManager;
import info.spicyclient.notifications.Type;
import info.spicyclient.util.MovementUtils;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.client.C13PacketPlayerAbilities;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;

public class Disabler extends Module {
	public Disabler() {
		super("Disabler", Keyboard.KEY_NONE, Category.PLAYER);
	}
	
	public static transient boolean watchdog = false;
	
	@Override
	public void onEnable() {
		NotificationManager.getNotificationManager().createNotification("Relog for the disabler to take effect", "", true, 5000, Type.INFO, Color.PINK);
		watchdog = false;
	}
	
	@Override
	public void onDisable() {
		watchdog = false;
	}
	
	@Override
	public void onEvent(Event e) {
		
		if (e instanceof EventUpdate && e.isPre()) {
			
			this.additionalInformation = "Hypixel";
			
			if (SpicyClient.config.fly.isEnabled() && !SpicyClient.config.fly.hypixelBlink.isEnabled()) {
				
	            PlayerCapabilities playerCapabilities = new PlayerCapabilities();
	            playerCapabilities.isFlying = true;
	            playerCapabilities.allowFlying = true;
	            //playerCapabilities.setFlySpeed((float) ((Math.random() * (9.0 - 0.1)) + 0.1));
	            playerCapabilities.setFlySpeed((float) ((Math.random() * (9.0 - 0.1)) + 0.1));
	            playerCapabilities.isCreativeMode = new Random().nextBoolean();
	            //mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C13PacketPlayerAbilities(playerCapabilities));
				
			}
			
		}
		
		if (e instanceof EventSendPacket && e.isPre()) {
			
			EventSendPacket event = (EventSendPacket) e;
			
            if (event.packet instanceof C0FPacketConfirmTransaction) {
                C0FPacketConfirmTransaction packetConfirmTransaction = (C0FPacketConfirmTransaction)event.packet;
                mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C0FPacketConfirmTransaction(2147483647, packetConfirmTransaction.getUid(), false));
                //mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C0FPacketConfirmTransaction(1147483647, packetConfirmTransaction.getUid(), false));
                e.setCanceled(true);
            }

            if (event.packet instanceof C00PacketKeepAlive) {
            	mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C00PacketKeepAlive(-2147483648 + (new Random()).nextInt(100)));
            	//mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C00PacketKeepAlive(-1147483648 + (new Random()).nextInt(100)));
                e.setCanceled(true);
            }
			
		}
		
	}
	
}
