package info.spicyclient.util.pathfinding;

import java.util.ArrayList;

import net.minecraft.util.BlockPos;

public class AStarPathFinderThread extends AStarPathFinder {

	public AStarPathFinderThread(boolean goThroughBlocks) {
		super(26298000000L, goThroughBlocks);
	}
	
	public Thread pathFinderThread = null;
	
	public void createPathInThread(BlockPos start, BlockPos end) {
		pathFinderThread = new Thread("Pathfinder Thread") {
			@Override
			public void run() {
				try {
					createPath(start, end);
				} catch (Exception e) {
					
				}
			}
		};
		pathFinderThread.start();
	}
	
	public void cancelPathFinding() {
		if (pathFinderThread != null)
			pathFinderThread.stop();
	}
	
}
