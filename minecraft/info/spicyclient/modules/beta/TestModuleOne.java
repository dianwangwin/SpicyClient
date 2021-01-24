package info.spicyclient.modules.beta;

import java.util.Random;

import org.lwjgl.input.Keyboard;

import info.spicyclient.SpicyClient;
import info.spicyclient.chatCommands.Command;
import info.spicyclient.events.Event;
import info.spicyclient.events.listeners.EventMotion;
import info.spicyclient.events.listeners.EventMove;
import info.spicyclient.events.listeners.EventPacket;
import info.spicyclient.events.listeners.EventSendPacket;
import info.spicyclient.events.listeners.EventUpdate;
import info.spicyclient.modules.Module;
import info.spicyclient.modules.combat.Killaura;
import info.spicyclient.modules.movement.Fly;
import info.spicyclient.notifications.Color;
import info.spicyclient.notifications.NotificationManager;
import info.spicyclient.notifications.Type;
import info.spicyclient.util.MovementUtils;
import info.spicyclient.util.PlayerUtils;
import info.spicyclient.util.RotationUtils;
import info.spicyclient.util.Timer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockGlass;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemSword;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C13PacketPlayerAbilities;
import net.minecraft.network.play.client.C02PacketUseEntity.Action;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.potion.Potion;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;

public class TestModuleOne extends Module {

	public TestModuleOne() {
		super("TestModuleOne", Keyboard.KEY_NONE, Category.BETA);
	}
	
	public static transient Timer timer = new Timer();
	
	public int status = 0, test = 0;
	public double dub = 0;
	public float flo = 0;
	public boolean bool1 = false, bool2 = true;
	
	@Override
	public void onEnable() {
		
	}
	
	@Override
	public void onDisable() {
		mc.thePlayer.motionX = 0;
		mc.thePlayer.motionY = 0;
		mc.thePlayer.motionZ = 0;
	}
	
	@Override
	public void onEvent(Event e) {
		
		if (e instanceof EventPacket) {
			
			Packet p = ((EventPacket)e).packet;
			
			if (p instanceof S08PacketPlayerPosLook) {
				e.setCanceled(true);
			}
			
		}
		
		if (e instanceof EventMove && e.isPre()) {
			
			EventMove event = (EventMove)e;
			
			if (SpicyClient.config.bhop.isEnabled()) {
				SpicyClient.config.bhop.toggle();
			}
			
			mc.thePlayer.fallDistance = 0;
			
			event.x = 0;
			event.y = 0;
			event.z = 0;
			mc.thePlayer.motionX = 0;
			mc.thePlayer.motionY = 0;
			mc.thePlayer.motionZ = 0;
			
			event.y = 0;
			MovementUtils.strafe(2);
			event.x = mc.thePlayer.motionX;
			event.z = mc.thePlayer.motionZ;
			if (!bool1) {
				mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.5, mc.thePlayer.posZ, true));
				bool1 = true;
			}
			else {
				mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY - 0.5, mc.thePlayer.posZ, true));
				bool1 = false;
			}
			
			if (mc.gameSettings.keyBindSneak.isKeyDown()) {
				
				mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.5, mc.thePlayer.posZ, true));
				event.y = -0.5;
				
			}
			
			if (mc.gameSettings.keyBindJump.isKeyDown()) {
				
				mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY - 0.5, mc.thePlayer.posZ, true));
				event.y = 0.5;
				
			}
			
		}
		
	}
	
	@Override
	public void onEventWhenDisabled(Event e) {
		
	}
	
}
