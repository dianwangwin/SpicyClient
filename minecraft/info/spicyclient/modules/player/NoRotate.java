package info.spicyclient.modules.player;

import org.apache.commons.lang3.StringUtils;
import org.lwjgl.input.Keyboard;

import info.spicyclient.events.Event;
import info.spicyclient.events.listeners.EventChatmessage;
import info.spicyclient.events.listeners.EventMotion;
import info.spicyclient.events.listeners.EventReceivePacket;
import info.spicyclient.events.listeners.EventServerSetYawAndPitch;
import info.spicyclient.events.listeners.EventUpdate;
import info.spicyclient.modules.Module;
import info.spicyclient.settings.BooleanSetting;
import info.spicyclient.settings.ModeSetting;
import info.spicyclient.settings.NumberSetting;
import info.spicyclient.ui.NewAltManager;
import info.spicyclient.util.MovementUtils;
import net.minecraft.block.BlockAir;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C03PacketPlayer.C06PacketPlayerPosLook;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;

public class NoRotate extends Module {
	
	private BooleanSetting notify = new BooleanSetting("Notify", false);
	
	public NoRotate() {
		super("NoRotate", Keyboard.KEY_NONE, Category.PLAYER);
		resetSettings();
	}
	
	@Override
	public void resetSettings() {
		this.settings.clear();
		this.addSettings();
	}
	
	public void onEnable() {
		
	}
	
	public void onDisable() {
		
	}
	
	public void onEvent(Event e) {
		
		if (e instanceof EventReceivePacket && e.isPre()) {
			
			if (((EventReceivePacket)e).packet instanceof S08PacketPlayerPosLook) {
				S08PacketPlayerPosLook packet = ((S08PacketPlayerPosLook)(((EventReceivePacket)e).packet));
				packet.yaw = mc.thePlayer.rotationYaw;
				packet.pitch = mc.thePlayer.rotationPitch;
				mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C03PacketPlayer.C06PacketPlayerPosLook(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, packet.getYaw(), packet.getPitch(), MovementUtils.isOnGround(0.00001)));
				//e.setCanceled(true);
			}
			
		}
		
	}
	
}
