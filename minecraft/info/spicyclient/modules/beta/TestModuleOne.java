package info.spicyclient.modules.beta;

import org.lwjgl.input.Keyboard;

import info.spicyclient.events.Event;
import info.spicyclient.events.listeners.EventRender3D;
import info.spicyclient.modules.Module;
import info.spicyclient.util.Timer;
import info.spicyclient.util.pathfinding.AStarPathFinder;
import net.minecraft.util.BlockPos;

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
	public AStarPathFinder pathFinder = new AStarPathFinder(10000, false);
	
	@Override
	public void onEnable() {
		pathFinder = new AStarPathFinder(10000, false);
//		BlockPos test = mc.thePlayer.getPosition().add(RandomUtils.nextDouble(0, 100) - 50, 100, RandomUtils.nextDouble(0, 100) - 50);
		BlockPos test = new BlockPos(-265, 57, 102);
		pathFinder.createPath(mc.thePlayer.getRealPosition(), test);
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
