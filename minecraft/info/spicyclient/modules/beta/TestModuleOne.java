package info.spicyclient.modules.beta;

import java.text.DecimalFormat;
import java.util.Random;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import com.sun.javafx.geom.Vec3d;

import info.spicyclient.SpicyClient;
import info.spicyclient.chatCommands.Command;
import info.spicyclient.dragon.RenderWings;
import info.spicyclient.events.Event;
import info.spicyclient.events.listeners.EventMotion;
import info.spicyclient.events.listeners.EventMove;
import info.spicyclient.events.listeners.EventPacket;
import info.spicyclient.events.listeners.EventPlayerRender;
import info.spicyclient.events.listeners.EventRender3D;
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
import info.spicyclient.util.RenderUtils;
import info.spicyclient.util.RotationUtils;
import info.spicyclient.util.Timer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockGlass;
import net.minecraft.client.model.ModelRenderer;
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
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.client.C13PacketPlayerAbilities;
import net.minecraft.network.play.client.C18PacketSpectate;
import net.minecraft.network.play.client.C02PacketUseEntity.Action;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
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
	
	@Override
	public void onEnable() {
		
	}

	@Override
	public void onDisable() {

	}

	@Override
	public void onEvent(Event e) {
		if (e instanceof EventSendPacket && e.isPre()) {
			if (((EventSendPacket)e).packet instanceof C0APacketAnimation) {
				e.setCanceled(true);
			}
		}
	}

	@Override
	public void onEventWhenDisabled(Event e) {
		
	}
	
}
