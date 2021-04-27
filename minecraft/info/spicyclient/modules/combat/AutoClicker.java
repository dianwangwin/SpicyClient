package info.spicyclient.modules.combat;

import org.lwjgl.input.Keyboard;

import info.spicyclient.chatCommands.Command;
import info.spicyclient.events.Event;
import info.spicyclient.events.listeners.EventUpdate;
import info.spicyclient.modules.Module;
import info.spicyclient.settings.ModeSetting;
import info.spicyclient.settings.NumberSetting;
import info.spicyclient.util.RotationUtils;
import info.spicyclient.util.Timer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C02PacketUseEntity.Action;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings;

public class AutoClicker extends Module {
	
	private NumberSetting aps = new NumberSetting("APS", 10, 0, 20, 1);
	//private ModeSetting mode = new ModeSetting("Mode", "Swing", "Swing", "Swing + Autoblock");
	
	private static transient boolean blocking = false;
	
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
		blocking = mc.thePlayer.isBlocking();
	}
	
	public void onDisable() {
		blocking = mc.thePlayer.isBlocking();
	}
	
	public Timer timer = new Timer();
	
	public void onEvent(Event e) {
		
		if (e instanceof EventUpdate) {
			
			if (timer.hasTimeElapsed((long) (1000/aps.getValue()), true) && mc.gameSettings.keyBindAttack.pressed && !mc.thePlayer.isUsingItem()) {
				
				//if (mode.is("Swing + Autoblock")) {
					//mc.gameSettings.keyBindUseItem.pressed = true;
				//}
				
				mc.gameSettings.keyBindUseItem.pressed = false;
				
				mc.thePlayer.swingItem();
				
				if (mc.objectMouseOver.typeOfHit.equals(MovingObjectType.ENTITY)) {
					
					if (mc.gameSettings.keyBindUseItem.pressed && mc.thePlayer.getCurrentEquippedItem() != null && mc.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemSword) {
						mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
						blocking = false;
					}
					
					mc.thePlayer.sendQueue.addToSendQueue(new C02PacketUseEntity(mc.objectMouseOver.entityHit, Action.ATTACK));
					
				}else {
					
				}
				
			}
			
			//else if (!mc.gameSettings.keyBindAttack.pressed){
				//mc.gameSettings.keyBindUseItem.pressed = false;
			//}
			
		}
		
	}
	
}
