package info.spicyclient.modules.beta;

import org.lwjgl.input.Keyboard;

import info.spicyclient.SpicyClient;
import info.spicyclient.chatCommands.Command;
import info.spicyclient.events.Event;
import info.spicyclient.events.listeners.EventMotion;
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
import info.spicyclient.util.RotationUtils;
import info.spicyclient.util.Timer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockGlass;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C13PacketPlayerAbilities;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public class TestModuleOne extends Module {

	public TestModuleOne() {
		super("TestModuleOne", Keyboard.KEY_NONE, Category.BETA);
		// TODO Auto-generated constructor stub
	}
	
	public static transient Timer timer = new Timer();
	
	public int status = 0, test = 0;
	public double dub = 0;
	public float flo = 0;
	public boolean bool1 = false, bool2 = true;
	
	@Override
	public void onEnable() {
		status = -1;
	}
	
	@Override
	public void onDisable() {
		
	}
	
	@Override
	public void onEvent(Event e) {
		
		if (e instanceof EventUpdate && e.isPre()) {
			
			int ticks = 12;
			
			if (mc.thePlayer.getItemInUseDuration() == ticks && canUseItem(mc.thePlayer.getItemInUse().getItem())) {
	        	for (int i = 0; i < 30; i++) {
	                mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer(true));
	            }
	            mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
	            mc.thePlayer.stopUsingItem();
	        }
			
		}
		
	}
	
	@Override
	public void onEventWhenDisabled(Event e) {
		
	}
	
    private boolean canUseItem(Item item) {
    	boolean result = !((item instanceof ItemSword) || (item instanceof ItemBow));
        return result;
    }
	
}
