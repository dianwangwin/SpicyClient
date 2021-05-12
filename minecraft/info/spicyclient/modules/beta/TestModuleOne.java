package info.spicyclient.modules.beta;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.lwjgl.input.Keyboard;

import com.google.common.collect.Lists;

import info.spicyclient.SpicyClient;
import info.spicyclient.chatCommands.Command;
import info.spicyclient.events.Event;
import info.spicyclient.events.listeners.EventSendPacket;
import info.spicyclient.events.listeners.EventTick;
import info.spicyclient.events.listeners.EventUpdate;
import info.spicyclient.modules.Module;
import info.spicyclient.notifications.Color;
import info.spicyclient.notifications.NotificationManager;
import info.spicyclient.notifications.Type;
import info.spicyclient.util.MovementUtils;
import info.spicyclient.util.RandomUtils;
import info.spicyclient.util.RenderUtils;
import info.spicyclient.util.RotationUtils;
import info.spicyclient.util.ServerUtils;
import info.spicyclient.util.Timer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.entity.projectile.EntityLargeFireball;
import net.minecraft.init.Blocks;
import net.minecraft.network.Packet;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.client.C0BPacketEntityAction.Action;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.client.C13PacketPlayerAbilities;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public class TestModuleOne extends Module {

	public TestModuleOne() {
		super("TestModuleOne", Keyboard.KEY_NONE, Category.BETA);
	}

	public static transient Timer timer = new Timer();

	public int status = 0, test = 0;
	public double dub = 0;
	public float flo = 0;
	public boolean bool1 = false, bool2 = true;
	public BlockPos pos = BlockPos.ORIGIN;
	private ArrayList<Packet> packets = new ArrayList<Packet>();
	
	@Override
	public void onEnable() {
		
	}

	@Override
	public void onDisable() {
		
	}

	@Override
	public void onEvent(Event e) {
		
		if (e instanceof EventUpdate && e.isBeforePre()) {
			EventUpdate event = (EventUpdate)e;
			List<Object> thingsInWorld = mc.theWorld.loadedEntityList;
			for (Object obj : thingsInWorld) {
				if (obj instanceof EntityFireball) {
					EntityFireball fireball = (EntityFireball)obj;
					if (mc.thePlayer.getDistanceToEntity(fireball) <= 30 && fireball.ticksExisted > 1) {
						fireball.posY += fireball.getEyeHeight() / 2;
						float[] rots = RotationUtils.getRotations(fireball);
						fireball.posY -= fireball.getEyeHeight() / 2;
						event.setYaw(rots[0]);
						event.setPitch(rots[1]);
						RenderUtils.setCustomYaw(event.yaw);
						RenderUtils.setCustomPitch(event.pitch);
//						mc.thePlayer.rotationYaw = event.yaw;
//						mc.thePlayer.rotationPitch = event.pitch;
						if (mc.thePlayer.getDistanceToEntity(fireball) <= 7 && !SpicyClient.config.killaura.isEnabled() && !SpicyClient.config.blockFly.isEnabled()) {
							mc.thePlayer.swingItem();
							mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C02PacketUseEntity(fireball, C02PacketUseEntity.Action.ATTACK));
						}
//						fireball.posY += 8;
//						NotificationManager.getNotificationManager().createNotification("AntiFireball", "Hit a fireball for you", true, 2000, Type.INFO, Color.GREEN);
						break;
					}
				}
			}
		}
		
	}

	@Override
	public void onEventWhenDisabled(Event e) {
		
	}
	
}
