package info.spicyclient.modules.beta;

import org.lwjgl.input.Keyboard;

import info.spicyclient.events.Event;
import info.spicyclient.events.listeners.EventRender3D;
import info.spicyclient.events.listeners.EventUpdate;
import info.spicyclient.modules.Module;
import info.spicyclient.util.Timer;
import info.spicyclient.util.pathfinding.AStarPathFinder;
import info.spicyclient.util.pathfinding.PathFinder;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.client.C0BPacketEntityAction.Action;
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
	public AStarPathFinder pathFinder = new AStarPathFinder();
	
	@Override
	public void onEnable() {
		BlockPos test = mc.thePlayer.getPosition().add(20, 0, 0);
		pathFinder.createPath(mc.thePlayer.getPosition(), test);
	}

	@Override
	public void onDisable() {
		
	}

	@Override
	public void onEvent(Event e) {
		
		if (e instanceof EventRender3D) {
			pathFinder.renderPath();
		}
		
	}

	@Override
	public void onEventWhenDisabled(Event e) {
		
	}
	
}
