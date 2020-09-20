package spicy.modules.combat;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.lwjgl.input.Keyboard;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C02PacketUseEntity.Action;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import spicy.SpicyClient;
import spicy.events.Event;
import spicy.events.listeners.EventGetBlockReach;
import spicy.events.listeners.EventMotion;
import spicy.events.listeners.EventPlayerUseItem;
import spicy.events.listeners.EventUpdate;
import spicy.files.FileManager;
import spicy.modules.Module;
import spicy.modules.movement.BlockFly;
import spicy.modules.movement.Sprint;
import spicy.settings.BooleanSetting;
import spicy.settings.ModeSetting;
import spicy.settings.NumberSetting;
import spicy.settings.Setting;
import spicy.util.Timer;

public class Reach extends Module {
	
	private NumberSetting reach = new NumberSetting("Reach", 3, 3, 6, 0.1);
	
	public Reach() {
		super("Reach", Keyboard.KEY_NONE, Category.BETA);
		resetSettings();
	}
	
	@Override
	public void resetSettings() {
		this.settings.clear();
		this.addSettings(reach);
	}
	
	public void onEnable() {
		
	}
	
	public void onDisable() {
		mc.gameSettings.keyBindUseItem.pressed = false;
	}
	
	public Timer timer = new Timer();
	
	public void onEvent(Event e) {
		
		if (e instanceof EventUpdate) {
			
			if (e.isPre()) {
				
				this.additionalInformation = "" + reach.getValue();
				
			}
			
		}
		
		if (e instanceof EventGetBlockReach) {
			
			EventGetBlockReach event = (EventGetBlockReach) e;
			
			event.setCanceled(true);
			event.reach = (float) reach.getValue();
			
		}
		
	}
	
}
