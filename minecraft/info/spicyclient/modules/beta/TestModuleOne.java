package info.spicyclient.modules.beta;

import org.lwjgl.input.Keyboard;

import info.spicyclient.events.Event;
import info.spicyclient.events.listeners.EventRender3D;
import info.spicyclient.modules.Module;
import info.spicyclient.util.Timer;
import info.spicyclient.util.pathfinding.AStarPathFinder;
import info.spicyclient.util.pathfinding.AStarPathFinderThread;
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
	public AStarPathFinderThread pathFinder = new AStarPathFinderThread(false);
	
	@Override
	public void onEnable() {
//		pathFinder.cancelPathFinding();
//		pathFinder = new AStarPathFinderThread(false);
//		BlockPos test = new BlockPos(-1390, 4, 439);
//		pathFinder.createPathInThread(mc.thePlayer.getRealPosition(), test);
	}

	@Override
	public void onDisable() {
		pathFinder.cancelPathFinding();
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
