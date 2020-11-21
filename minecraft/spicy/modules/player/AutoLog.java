package spicy.modules.player;

import org.apache.commons.lang3.StringUtils;
import org.lwjgl.input.Keyboard;

import net.minecraft.block.BlockAir;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.MathHelper;
import spicy.SpicyClient;
import spicy.chatCommands.Command;
import spicy.events.Event;
import spicy.events.listeners.EventChatmessage;
import spicy.events.listeners.EventMotion;
import spicy.events.listeners.EventServerSetYawAndPitch;
import spicy.events.listeners.EventUpdate;
import spicy.modules.Module;
import spicy.notifications.Color;
import spicy.notifications.NotificationManager;
import spicy.notifications.Type;
import spicy.settings.BooleanSetting;
import spicy.settings.ModeSetting;
import spicy.settings.NumberSetting;
import spicy.ui.NewAltManager;

public class AutoLog extends Module {
	
	private ModeSetting mode = new ModeSetting("mode", "Vanilla", "Vanilla", "Packet");
	private NumberSetting health = new NumberSetting("health", 4, 1, 20, 1);
	
	
	public AutoLog() {
		super("AutoLog", Keyboard.KEY_NONE, Category.PLAYER);
		resetSettings();
	}
	
	@Override
	public void resetSettings() {
		this.settings.clear();
		this.addSettings(health, mode);
	}
	
	public void onEnable() {
		
	}
	
	public void onDisable() {
		
	}
	
	public void onEvent(Event e) {
		
		if (e instanceof EventUpdate) {
			
			if (e.isPre() && mc.thePlayer != null) {
				
				if (mode.is("Vanilla")) {
					
					if (mc.thePlayer.getHealth() <= health.getValue() && mc.thePlayer.getHealth() != 0) {
						
						mc.thePlayer.sendQueue.getNetworkManager().closeChannel(new ChatComponentText("§6[ §f" + SpicyClient.config.clientName + SpicyClient.config.clientVersion + " §6] §f" + "Disconnected from the server so you wouldn't die, autolog has been disabled"));
						this.notifyPlayer();
						
					}
					
				}
				else if (mode.is("Packet")) {
					
					if (mc.thePlayer.getHealth() <= health.getValue() && mc.thePlayer.getHealth() != 0) {
						
						for (int i = 0; i < 1000; i++) {
							
							mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, mc.thePlayer.onGround));
							
						}
						this.notifyPlayer();
						
					}
					
				}
				
			}
			
		}
		
	}
	
	private void notifyPlayer() {
		
		//Command.sendPrivateChatMessage("Disconnected from the server so you wouldn't die, autolog has been disabled");
		NotificationManager.getNotificationManager().createNotification("Autolog was disabled", "", true, 5000, Type.INFO, Color.PINK);
		NotificationManager.getNotificationManager().createNotification("Disconnected from the server due to autolog", "", true, 5000, Type.INFO, Color.PINK);
		this.toggle();
		
	}
	
}
