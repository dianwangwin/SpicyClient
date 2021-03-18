package info.spicyclient.modules.player;

import org.lwjgl.input.Keyboard;

import info.spicyclient.chatCommands.Command;
import info.spicyclient.events.Event;
import info.spicyclient.events.listeners.EventChatmessage;
import info.spicyclient.events.listeners.EventReceivePacket;
import info.spicyclient.events.listeners.EventSendPacket;
import info.spicyclient.events.listeners.EventUpdate;
import info.spicyclient.modules.Module;
import info.spicyclient.util.InventoryUtils;
import info.spicyclient.util.RotationUtils;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.client.C0EPacketClickWindow;
import net.minecraft.network.play.server.S2DPacketOpenWindow;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public class AutoPaperChallenge extends Module {

	public AutoPaperChallenge() {
		super("AutoPaperChallenge", Keyboard.KEY_NONE, Category.BETA);
	}
	
	@Override
	public void onEvent(Event e) {
		
		if (e instanceof EventReceivePacket && e.isPre()) {
			if (((EventReceivePacket)e).packet instanceof S2DPacketOpenWindow) {
				S2DPacketOpenWindow packet = ((S2DPacketOpenWindow)((EventReceivePacket)e).packet);
				if (packet.getWindowTitle().getUnformattedTextForChat().equals("SkyWars Challenges")) {
					mc.thePlayer.ticksExisted = 3;
				}
			}
		}
		
		if (e instanceof EventUpdate && e.isPre()) {
			
			if (mc.thePlayer.ticksExisted == 2) {
				if (mc.thePlayer.inventoryContainer.getSlot(7 + 36).getHasStack()
						&& mc.thePlayer.inventoryContainer.getSlot(7 + 36).getStack().getItem() == Items.blaze_powder) {
					InventoryUtils.switchToSlot(8);
					try {
						mc.playerController.clickBlock(mc.objectMouseOver.getBlockPos(), mc.objectMouseOver.sideHit);
					} catch (Exception e2) {
						e2.printStackTrace();
						Command.sendPrivateChatMessage("Auto paper challenge has failed due to an error");
					}
					mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C0APacketAnimation());
					//mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
					//mc.thePlayer.swingItem();
					
				}
			}
			else if (mc.thePlayer.ticksExisted == 3 && mc.currentScreen != null && mc.currentScreen instanceof GuiChest
					&& ((GuiChest) mc.currentScreen).inventorySlots.getSlot(7).getHasStack()
					&& ((GuiChest) mc.currentScreen).inventorySlots.getSlot(7).getStack().getItem().equals(Items.paper)
					&& ((GuiChest) mc.currentScreen).inventorySlots.getSlot(7).getStack().getDisplayName().toLowerCase()
							.contains("paper challenge")) {
				InventoryUtils.click(7);
			}
		}
		
	}
	
}
