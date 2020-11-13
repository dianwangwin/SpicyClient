package spicy.modules.player;

import org.lwjgl.input.Keyboard;

import net.minecraft.item.ItemBucket;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;
import spicy.chatCommands.Command;
import spicy.events.Event;
import spicy.events.listeners.EventMotion;
import spicy.events.listeners.EventUpdate;
import spicy.modules.Module;

public class AntiLava extends Module {

	public AntiLava() {
		super("AntiLava", Keyboard.KEY_NONE, Category.PLAYER);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void onEvent(Event e) {
		
		if (e instanceof EventUpdate && e.isPre()) {
			this.additionalInformation = "Hypixel";
		}
		
		if (e instanceof EventMotion) {
			
			EventMotion event = (EventMotion) e;
			
			if (mc.thePlayer.isInLava()) {
				for (int i = 36; i < 45; i++) {
					
					if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
						if (mc.thePlayer.inventoryContainer.getSlot(i).getStack().getItem() instanceof ItemBucket) {
							ItemBucket bucket = (ItemBucket) mc.thePlayer.inventoryContainer.getSlot(i).getStack().getItem();
							mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C05PacketPlayerLook(mc.thePlayer.rotationYaw, 90, mc.thePlayer.onGround));
							mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(i - 36));
							mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(mc.thePlayer.inventoryContainer.getSlot(i).getStack()));
							mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(mc.thePlayer.inventoryContainer.getSlot(i).getStack()));
							return;
							
						}
						
					}
					
				}
				
			}
			
		}
		
	}
	
}
