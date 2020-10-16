package spicy.chatCommands.commands;

import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.C03PacketPlayer;
import spicy.chatCommands.Command;

public class Damage extends Command {

	public Damage() {
		super("damage", "damage", 0);
	}
	
	@Override
	public void commandAction(String message) {
		
		Minecraft mc = Minecraft.getMinecraft();
		
		for (int index = 0; index < 70; index++) {
            mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.06D, mc.thePlayer.posZ, false));
            mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false));
        }
		mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.1D, mc.thePlayer.posZ, false));
		
	}
	
	@Override
	public void incorrectParameters() {
		sendPrivateChatMessage("Please use .damage");
	}
	
}
