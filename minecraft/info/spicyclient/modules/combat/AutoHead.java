package info.spicyclient.modules.combat;

import org.lwjgl.input.Keyboard;

import info.spicyclient.chatCommands.Command;
import info.spicyclient.events.Event;
import info.spicyclient.events.listeners.EventUpdate;
import info.spicyclient.modules.Module;
import info.spicyclient.notifications.Color;
import info.spicyclient.notifications.NotificationManager;
import info.spicyclient.notifications.Type;
import info.spicyclient.settings.NumberSetting;
import info.spicyclient.util.InventoryUtils;
import info.spicyclient.util.Timer;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemSkull;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;

public class AutoHead extends Module {

	public AutoHead() {
		super("AutoHead", Keyboard.KEY_NONE, Category.COMBAT);
	}
	
	public NumberSetting delay = new NumberSetting("Delay", 750, 0, 3000, 50),
			eatAtPercent = new NumberSetting("Percent to eat", 50, 1, 99, 1);
	
	@Override
	public void resetSettings() {
		this.settings.clear();
		this.addSettings(delay, eatAtPercent);
	}
	
	public static transient Timer timer = new Timer();
	
	@Override
	public void onEvent(Event e) {
		
		if (e instanceof EventUpdate && e.isPre()) {
			
			if ((mc.thePlayer.getHealth() / mc.thePlayer.getMaxHealth()) * 100 <= eatAtPercent.getValue() && timer.hasTimeElapsed((long) delay.getValue(), false)) {
				
				for (short i = 0; i < 45; i++) {
					
					if (Minecraft.getMinecraft().thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
						ItemStack is = Minecraft.getMinecraft().thePlayer.inventoryContainer.getSlot(i).getStack();
						
						if (is.getItem() instanceof ItemSkull && is.getDisplayName().equalsIgnoreCase("§6Golden Head")) {
							NotificationManager.getNotificationManager().createNotification("Auto Head",
									"Eating head", true, 3000, Type.INFO, Color.YELLOW);
							int heldItemBeforeThrow = mc.thePlayer.inventory.currentItem;
							if (i - 36 < 0) {
								
								InventoryUtils.swap(i, 8);
								
								Minecraft.getMinecraft().getNetHandler().getNetworkManager()
										.sendPacketNoEvent(new C09PacketHeldItemChange(8));
								
							}else {
								
								Minecraft.getMinecraft().getNetHandler().getNetworkManager()
										.sendPacketNoEvent(new C09PacketHeldItemChange(i - 36));
								
							}
							
							Minecraft.getMinecraft().getNetHandler().getNetworkManager()
									.sendPacketNoEvent(new C08PacketPlayerBlockPlacement(is));
							mc.thePlayer.inventory.currentItem = heldItemBeforeThrow;
							Minecraft.getMinecraft().getNetHandler().getNetworkManager()
								.sendPacketNoEvent( new C09PacketHeldItemChange(heldItemBeforeThrow));
							timer.reset();
						}
						
					}
					
				}
				
			}
			
		}
		
	}
	
}
