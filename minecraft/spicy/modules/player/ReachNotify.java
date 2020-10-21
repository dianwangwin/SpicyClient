package spicy.modules.player;

import org.apache.commons.lang3.RandomUtils;
import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C02PacketUseEntity.Action;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import spicy.chatCommands.Command;
import spicy.events.Event;
import spicy.events.listeners.EventGetBlockReach;
import spicy.events.listeners.EventMotion;
import spicy.events.listeners.EventPacket;
import spicy.events.listeners.EventRenderGUI;
import spicy.events.listeners.EventSendPacket;
import spicy.events.listeners.EventUpdate;
import spicy.modules.Module;
import spicy.settings.NumberSetting;
import spicy.util.Timer;

public class ReachNotify extends Module {
	
	public ReachNotify() {
		super("ReachNotify", Keyboard.KEY_NONE, Category.COMBAT);
		resetSettings();
	}
	
	public void onEnable() {
		
	}
	
	public void onDisable() {
		
	}
	
	public Timer timer = new Timer();
	
	public void onEvent(Event e) {
		
		if (e instanceof EventRenderGUI) {
			
			if (mc.objectMouseOver.typeOfHit.equals(MovingObjectType.ENTITY)) {
				
				ScaledResolution sr = new ScaledResolution(mc);
				
				Gui.drawRect((sr.getScaledWidth()/2) - 4, (sr.getScaledHeight()/2) - 0, (sr.getScaledWidth()/2) + 5, (sr.getScaledHeight()/2) + 1, 0xffff0000);
				Gui.drawRect((sr.getScaledWidth()/2) - 0, (sr.getScaledHeight()/2) - 4, (sr.getScaledWidth()/2) + 1, (sr.getScaledHeight()/2) + 5, 0xffff0000);
				
			}
			
		}
		
	}
	
}
