package info.spicyclient.modules.beta;

import java.text.DecimalFormat;
import java.util.Random;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import com.sun.javafx.geom.Vec3d;

import info.spicyclient.SpicyClient;
import info.spicyclient.chatCommands.Command;
import info.spicyclient.events.Event;
import info.spicyclient.events.listeners.EventGetBlockReach;
import info.spicyclient.events.listeners.EventKey;
import info.spicyclient.events.listeners.EventMotion;
import info.spicyclient.events.listeners.EventMove;
import info.spicyclient.events.listeners.EventReceivePacket;
import info.spicyclient.events.listeners.EventPlayerRender;
import info.spicyclient.events.listeners.EventRender3D;
import info.spicyclient.events.listeners.EventSendPacket;
import info.spicyclient.events.listeners.EventTick;
import info.spicyclient.events.listeners.EventUpdate;
import info.spicyclient.modules.Module;
import info.spicyclient.modules.combat.Killaura;
import info.spicyclient.modules.movement.Fly;
import info.spicyclient.notifications.Color;
import info.spicyclient.notifications.NotificationManager;
import info.spicyclient.notifications.Type;
import info.spicyclient.portedMods.antiantixray.AntiAntiXray;
import info.spicyclient.portedMods.antiantixray.Mixins.TickMixin;
import info.spicyclient.portedMods.dragonWings.RenderWings;
import info.spicyclient.util.MovementUtils;
import info.spicyclient.util.PlayerUtils;
import info.spicyclient.util.RandomUtils;
import info.spicyclient.util.RenderUtils;
import info.spicyclient.util.RotationUtils;
import info.spicyclient.util.Timer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockGlass;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemSword;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.login.client.C00PacketLoginStart;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.client.C0CPacketBoatInput;
import net.minecraft.network.play.client.C10PacketCreativeInventoryAction;
import net.minecraft.network.play.client.C13PacketPlayerAbilities;
import net.minecraft.network.play.client.C14PacketTabComplete;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.network.play.client.C18PacketSpectate;
import net.minecraft.network.play.client.C19PacketResourcePackStatus;
import net.minecraft.network.play.client.C02PacketUseEntity.Action;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S14PacketEntity;
import net.minecraft.network.play.server.S18PacketEntityTeleport;
import net.minecraft.network.play.server.S19PacketEntityHeadLook;
import net.minecraft.network.play.server.S29PacketSoundEffect;
import net.minecraft.potion.Potion;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

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
	
	@Override
	public void onEnable() {
		bool1 = false;
		bool1 = false;
	}

	@Override
	public void onDisable() {
		
	}

	@Override
	public void onEvent(Event e) {
		
		if (e instanceof EventUpdate && e.isPre() && bool1) {
			if (isOverVoid() && mc.thePlayer.fallDistance >= 3.5) {
				bool2 = true;
				mc.thePlayer.motionY = 0;
				MovementUtils.setMotion(0);
			}
		}
		
		if (e instanceof EventGetBlockReach && e.isPre() && !bool1) {
			((EventGetBlockReach)e).reach = 100;
		}
		
		if (e instanceof EventRender3D && e.isPre() && pos != BlockPos.ORIGIN && bool1) {
			for (int i = 0; i < 5; i++) {

				RenderUtils.drawLine(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY(), pos.getZ());
				RenderUtils.drawLine(pos.getX(), pos.getY() + 1, pos.getZ(), pos.getX() + 1, pos.getY() + 1,
						pos.getZ());
				RenderUtils.drawLine(pos.getX(), pos.getY(), pos.getZ(), pos.getX(), pos.getY(), pos.getZ() + 1);
				RenderUtils.drawLine(pos.getX(), pos.getY() + 1, pos.getZ(), pos.getX(), pos.getY() + 1,
						pos.getZ() + 1);
				RenderUtils.drawLine(pos.getX(), pos.getY(), pos.getZ(), pos.getX(), pos.getY() + 1, pos.getZ());
				RenderUtils.drawLine(pos.getX(), pos.getY() + 1, pos.getZ(), pos.getX(), pos.getY() + 1, pos.getZ());
				RenderUtils.drawLine(pos.getX() + 1, pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1,
						pos.getZ());
				RenderUtils.drawLine(pos.getX() + 1, pos.getY() + 1, pos.getZ(), pos.getX() + 1, pos.getY() + 1,
						pos.getZ());
				RenderUtils.drawLine(pos.getX(), pos.getY(), pos.getZ() + 1, pos.getX(), pos.getY() + 1,
						pos.getZ() + 1);
				RenderUtils.drawLine(pos.getX(), pos.getY() + 1, pos.getZ() + 1, pos.getX(), pos.getY() + 1,
						pos.getZ() + 1);
				RenderUtils.drawLine(pos.getX() + 1, pos.getY(), pos.getZ() + 1, pos.getX(), pos.getY(),
						pos.getZ() + 1);
				RenderUtils.drawLine(pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1, pos.getX(), pos.getY() + 1,
						pos.getZ() + 1);
				RenderUtils.drawLine(pos.getX() + 1, pos.getY(), pos.getZ() + 1, pos.getX() + 1, pos.getY() + 1,
						pos.getZ() + 1);
				RenderUtils.drawLine(pos.getX() + 1, pos.getY() + 1, pos.getZ(), pos.getX() + 1, pos.getY() + 1,
						pos.getZ() + 1);
				RenderUtils.drawLine(pos.getX() + 1, pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY(),
						pos.getZ() + 1);
			}
		}
		
		if (e instanceof EventSendPacket && e.isPre()) {

			EventSendPacket event = (EventSendPacket) e;

			if (event.packet instanceof C08PacketPlayerBlockPlacement && mc.objectMouseOver != null && mc.objectMouseOver.blockPos != null && !bool1) {
				bool1 = true;
				pos = mc.objectMouseOver.getBlockPos();
				mc.thePlayer.setPosition(pos.getZ(), pos.getY(), pos.getZ());
				toggle();
				e.setCanceled(true);
			}
			
		}
		
		if (e instanceof EventReceivePacket && e.isPre()) {
			
			EventReceivePacket event = (EventReceivePacket)e;
			
			if (event.packet instanceof S08PacketPlayerPosLook && bool1 && bool2) {
				e.setCanceled(true);
				S08PacketPlayerPosLook s08 = (S08PacketPlayerPosLook) event.packet;
				mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(s08.getX(), s08.getY(), s08.getZ(), false));
				mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + 30, mc.thePlayer.posX);
				toggle();
			}
			
		}
		
	}

	@Override
	public void onEventWhenDisabled(Event e) {
		
	}
	
	private boolean isOverVoid() {
		boolean isOverVoid = true;
		BlockPos block = mc.thePlayer.getPosition();
		
		for (int i = (int) mc.thePlayer.posY; i > 0; i--) {
			
			if (isOverVoid) {
				
				if (!(mc.theWorld.getBlockState(block).getBlock() instanceof BlockAir)) {
					
					isOverVoid = false;
					
				}
				
			}
			
			block = block.add(0, -1, 0);
			
		}
		return isOverVoid;
	}
	
}
