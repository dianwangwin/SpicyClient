package info.spicyclient.modules.memes;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.lwjgl.input.Keyboard;

import info.spicyclient.events.Event;
import info.spicyclient.events.listeners.EventMotion;
import info.spicyclient.events.listeners.EventUpdate;
import info.spicyclient.modules.Module;
import info.spicyclient.notifications.Color;
import info.spicyclient.notifications.NotificationManager;
import info.spicyclient.notifications.Type;
import info.spicyclient.util.RenderUtils;
import info.spicyclient.util.ServerUtils;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.client.C02PacketUseEntity.Action;

public class NoHead extends Module {

	public NoHead() {
		super("No Head", Keyboard.KEY_NONE, Category.MEMES);
	}
	
	public void onEnable() {
		if (ServerUtils.isOnHypixel()) {
			toggle();
			NotificationManager.getNotificationManager().createNotification("NoHead", "This instant bans on hypixel", true, 5000, Type.WARNING, Color.RED);
		}
	}
	
	public void onDisable() {
		
	}
	
	
	public void onEvent(Event e) {
		
		if (e instanceof EventMotion) {
			
			if (e.isPre() && !ServerUtils.isOnHypixel()) {
				
				EventMotion event = (EventMotion) e;
				
				event.setPitch(180);
				RenderUtils.setCustomPitch(180);
				
			}
			
		}
		
	}
	
}
