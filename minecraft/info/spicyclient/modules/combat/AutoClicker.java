package info.spicyclient.modules.combat;

import org.lwjgl.input.Keyboard;

import info.spicyclient.chatCommands.Command;
import info.spicyclient.events.Event;
import info.spicyclient.events.listeners.EventUpdate;
import info.spicyclient.modules.Module;
import info.spicyclient.settings.ModeSetting;
import info.spicyclient.settings.NumberSetting;
import info.spicyclient.util.Timer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C02PacketUseEntity.Action;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;

public class AutoClicker extends Module {
	
	private NumberSetting aps = new NumberSetting("APS", 10, 0, 20, 1);
	//private ModeSetting mode = new ModeSetting("Mode", "Swing", "Swing", "Swing + Autoblock");
	
	public AutoClicker() {
		super("Autoclicker", Keyboard.KEY_NONE, Category.COMBAT);
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
		
		if (e instanceof EventUpdate) {
			
			if (timer.hasTimeElapsed((long) (1000/aps.getValue()), true) && mc.gameSettings.keyBindAttack.pressed) {
				
				//if (mode.is("Swing + Autoblock")) {
					//mc.gameSettings.keyBindUseItem.pressed = true;
				//}
				mc.thePlayer.swingItem();
				if (mc.objectMouseOver.typeOfHit.equals(MovingObjectType.ENTITY)) {
					mc.thePlayer.sendQueue.addToSendQueue(new C02PacketUseEntity(mc.objectMouseOver.entityHit, Action.ATTACK));
				}
			}
			
			//else if (!mc.gameSettings.keyBindAttack.pressed){
				//mc.gameSettings.keyBindUseItem.pressed = false;
			//}
			
		}
		
	}
	
}
