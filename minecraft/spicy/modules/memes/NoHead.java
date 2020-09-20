package spicy.modules.memes;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.lwjgl.input.Keyboard;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.client.C02PacketUseEntity.Action;
import spicy.events.Event;
import spicy.events.listeners.EventMotion;
import spicy.events.listeners.EventUpdate;
import spicy.modules.Module;

public class NoHead extends Module {

	public NoHead() {
		super("No Head", Keyboard.KEY_NONE, Category.MEMES);
	}
	
	public void onEnable() {
		
	}
	
	public void onDisable() {
		
	}
	
	
	public void onEvent(Event e) {
		
		if (e instanceof EventMotion) {
			
			if (e.isPre()) {
				
				EventMotion event = (EventMotion) e;
				
				event.setPitch(180);
				
			}
			
		}
		
	}
	
}
