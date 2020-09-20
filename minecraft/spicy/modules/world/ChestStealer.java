package spicy.modules.world;

import org.lwjgl.input.Keyboard;

import net.minecraft.inventory.Slot;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import spicy.events.Event;
import spicy.events.listeners.EventMotion;
import spicy.events.listeners.EventUpdate;
import spicy.events.listeners.EventOnLadder;
import spicy.events.listeners.EventOpenChest;
import spicy.modules.Module;
import spicy.settings.ModeSetting;
import spicy.settings.NumberSetting;

public class ChestStealer extends Module {
	
	public static Thread chestStealerThread = null;
	
	NumberSetting delay = new NumberSetting("Delay", 250, 0, 2000, 50);
	
	public ChestStealer() {
		super("Chest Stealer", Keyboard.KEY_NONE, Category.WORLD);
		resetSettings();
	}
	
	@Override
	public void resetSettings() {
		settings.clear();
		addSettings(delay);
	}
	
	public void onEnable() {
		
	}
	
	public void onDisable() {
		
	}
	
	public void onEvent(Event e) {
		
		if (e instanceof EventOpenChest) {
			
			EventOpenChest chestEvent = (EventOpenChest) e;
			
			
			if (chestStealerThread == null) {
				
			}else {
				chestStealerThread.stop();
			}
			
			chestStealerThread = new Thread(new Runnable() {
				
				@Override
				public void run() {
					
					try {
						
						for (int i = 0; i < (chestEvent.chest.inventoryRows * 9); i++) {
							Slot slot = (Slot) chestEvent.chest.inventorySlots.inventorySlots.get(i);
							if (slot.getStack() != null) {
								
								try {
									Thread.sleep((long) delay.getValue());
								}catch (ThreadDeath e) {
									e.printStackTrace();
								}
								chestEvent.chest.spicyHandleMouseInput(slot, slot.slotNumber, 0, 1);
								
							}
						}
						
					} catch (Exception e2) {
						// TODO: handle exception
					}
					
				}
			});
			
			if (e.isPre()) {
				chestStealerThread.start();
			}
			
		}
		
	}
	
}
