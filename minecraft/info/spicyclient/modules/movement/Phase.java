package info.spicyclient.modules.movement;

import org.lwjgl.input.Keyboard;

import info.spicyclient.events.Event;
import info.spicyclient.events.listeners.EventGetBlockHitbox;
import info.spicyclient.events.listeners.EventMotion;
import info.spicyclient.events.listeners.EventReceivePacket;
import info.spicyclient.events.listeners.EventSendPacket;
import info.spicyclient.events.listeners.EventUpdate;
import info.spicyclient.modules.Module;
import info.spicyclient.modules.Module.Category;
import info.spicyclient.notifications.Color;
import info.spicyclient.notifications.NotificationManager;
import info.spicyclient.notifications.Type;
import info.spicyclient.util.MovementUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockHopper;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S18PacketEntityTeleport;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;

public class Phase extends Module {
	
	public Phase() {
		super("Phase", Keyboard.KEY_NONE, Category.MOVEMENT);
	}

	
	public void onEnable() {
		mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY - 5, mc.thePlayer.posZ);
		NotificationManager.getNotificationManager().createNotification("Phase", "Teleported you out of the box", true, 3000, Type.INFO, Color.GREEN);
		toggle();
	}
	
	public void onDisable() {
		
	}
	
}
