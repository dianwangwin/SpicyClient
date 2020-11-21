package spicy.modules.combat;

import org.apache.commons.lang3.RandomUtils;
import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C02PacketUseEntity.Action;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import spicy.chatCommands.Command;
import spicy.events.Event;
import spicy.events.listeners.EventGetBlockReach;
import spicy.events.listeners.EventMotion;
import spicy.events.listeners.EventPacket;
import spicy.events.listeners.EventSendPacket;
import spicy.events.listeners.EventUpdate;
import spicy.modules.Module;
import spicy.settings.NumberSetting;
import spicy.util.Timer;

public class TriggerBot extends Module {
	
	private NumberSetting aps = new NumberSetting("APS", 10, 0, 20, 1);
	
	public TriggerBot() {
		super("TriggerBot", Keyboard.KEY_NONE, Category.COMBAT);
		resetSettings();
	}
	
	@Override
	public void resetSettings() {
		this.settings.clear();
		this.addSettings(aps);
	}
	
	
	public void onEnable() {
		
	}
	
	public void onDisable() {
		
	}
	
	public Timer timer = new Timer();
	
	public void onEvent(Event e) {
		
		if (e instanceof EventMotion) {
			
			if (mc.objectMouseOver.typeOfHit.equals(MovingObjectType.ENTITY) && timer.hasTimeElapsed((long) (1000/aps.getValue()), true)) {
				mc.thePlayer.swingItem();
				mc.thePlayer.sendQueue.addToSendQueue(new C02PacketUseEntity(mc.objectMouseOver.entityHit, Action.ATTACK));
			}
			
		}
		
	}
	
}
