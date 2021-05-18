package info.spicyclient.util.pathfinding;

import java.util.ArrayList;

import net.minecraft.util.BlockPos;

public class AStarPathFinderThread extends AStarPathFinder {

	public AStarPathFinderThread(boolean goThoughBlocks) {
		super(26298000000L, goThoughBlocks);
	}
	
	public void createPathInThread(BlockPos start, BlockPos end) {
		new Thread("Pathfinder Thread") {
			@Override
			public void run() {
				createPath(start, end);
			}
		}.start();
	}
	
}
