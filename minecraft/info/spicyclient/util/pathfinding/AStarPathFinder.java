package info.spicyclient.util.pathfinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import info.spicyclient.chatCommands.Command;
import info.spicyclient.util.RenderUtils;
import info.spicyclient.util.WorldUtils;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;

public class AStarPathFinder {
	
	public AStarPathFinder(long timeout, boolean goThoughBlocks) {
		this.timeout = timeout;
		this.goThoughBlocks = goThoughBlocks;
	}
	
	public long timeout;
	public boolean goThoughBlocks;
	
	// A* pathfinding basically works like this
	// Step 1: Get list of nodes with lowest f value
	// Step 2: if you have more than one node then select the one with the lowest distanceToEnd
	// Step 3: if you still have more pick one of the remaining ones
	// Step 4: check nodes around the selected node
	// Step 5: repeat until you reach the end
	
	// Node
	public static class Node{
		
		public Node(BlockPos pos, double distanceToStart, double distanceToEnd, Node previousNode) {
			this.pos = pos;
			this.distanceToStart = distanceToStart;
			this.distanceToEnd = distanceToEnd;
			this.previousNode = previousNode;
		}
		
		public double distanceToStart, distanceToEnd;
		public Node previousNode;
		public BlockPos pos;
		public boolean hasChecked = false;
		
		public double getFvalue() {
			return distanceToStart + distanceToEnd;
		}
		
	}
	
	public ArrayList<BlockPos> path = new ArrayList<>();
	
	public ArrayList<BlockPos> createPath(BlockPos start, BlockPos end) {
		
		boolean flipAfter = false;
		
		if (getBlockCountAroundPos(start) < getBlockCountAroundPos(end)) {
			BlockPos temp = start;
			start = end;
			end = temp;
			flipAfter = true;
		}
		
		path.clear();
		
		ArrayList<Node> nodes = new ArrayList<>();
		nodes.add(new Node(start, 0, WorldUtils.getDistance(start, end), null));
		
		// Prevents the program from freezing
		long antiFreeze = System.currentTimeMillis() + timeout;
		while (System.currentTimeMillis() < antiFreeze) {
			
			// Prevents it from getting stuck if there is no path the to end
			boolean breakHere = true;
			for (Node n : nodes) {
				if (!n.hasChecked) {
					breakHere = false;
				}
			}
			if (breakHere)
				break;
			
			// Finds nodes that are best to check
			Node nodeToCheck = null;
			ArrayList<Node> temp = new ArrayList<>();
			for (Node n : nodes) {
				if (temp.isEmpty() || (n.getFvalue() < temp.get(0).getFvalue() && !n.hasChecked)) {
					if (!n.hasChecked) {
						temp.clear();
						temp.add(n);
					}
				}
				else if (!temp.isEmpty() && temp.get(0).getFvalue() == n.getFvalue() && !n.hasChecked) {
					if (!n.hasChecked) {
						temp.add(n);
					}
				}
			}
			for (Node n : temp) {
				if (nodeToCheck == null || n.distanceToEnd < nodeToCheck.distanceToEnd) {
					nodeToCheck = n;
				}
			}
			
			// If it reached the end then return
			if (nodeToCheck.pos.equals(end)) {
				path.clear();
				Node backtrack = nodeToCheck;
				try {
					while ((backtrack = backtrack.previousNode) != null) {
						path.add(backtrack.pos);
						backtrack = backtrack.previousNode;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				if (flipAfter)
					Collections.reverse(path);
				
				return path;
			}
			
			// Recreates arraylist with added values
			nodeToCheck.hasChecked = true;
			path.add(nodeToCheck.pos);
			nodes = reCreateNodeArrayList(nodeToCheck, end, nodes);
			
		}
		
		return new ArrayList<>();
		
	}
	
	private int getBlockCountAroundPos(BlockPos pos) {
		int blockCount = 0;
		
		for (int x = -10; x < 10; x++)
			for (int y = -10; y < 10; y++)
				for (int z = -10; z < 10; z++)
					if (!Minecraft.getMinecraft().theWorld.getBlockState(pos.add(x, y, z)).getBlock().equals(Blocks.air))
						blockCount++;
		
		return blockCount;
	}
	
	private ArrayList<Node> reCreateNodeArrayList(Node nodeToCheck, BlockPos end, ArrayList<Node> existingNodes){
		
		nodeToCheck.hasChecked = true;
		
		// Creates new arraylist
		ArrayList<Node> nodes = new ArrayList<>();
		// Adds existing nodes
		nodes.addAll(existingNodes);
		
		// Checks area around the node
		for (EnumFacing face : EnumFacing.VALUES) {
			// Creates new node
			Node newNode = new Node(nodeToCheck.pos.offset(face), nodeToCheck.distanceToStart + 1, WorldUtils.getDistance(nodeToCheck.pos.offset(face), end), nodeToCheck);
			
			// Checks to see if it should actually add the node
			boolean add = true;
			for (Node n : nodes) {
				if (n.pos.equals(newNode.pos)) {
//					n.previousNode = nodeToCheck;
					add = false;
				}
			}
			
			if (add && !goThoughBlocks) {
				if (!Minecraft.getMinecraft().theWorld.getBlockState(newNode.pos).getBlock().equals(Blocks.air))
					add = false;
			}
			
			// Adds node if it fits the criteria
			if (add) {
				nodes.add(newNode);
			}
		}
		
		// Return the new arraylist
		return nodes;
		
	}
	
	public void renderPath() {
		
		ArrayList<Vec3> trailList = new ArrayList<Vec3>();
		for (BlockPos pos : path) {
			trailList.add(new Vec3(pos.x + 0.5, pos.y + 0.5, pos.z + 0.5));
		}
		
		Vec3 lastLoc = null;
		
		for (Vec3 loc: trailList) {
			
			if (lastLoc == null) {
				lastLoc = loc;
			}else {
				
				if (Minecraft.getMinecraft().thePlayer.getDistance(loc.xCoord, loc.yCoord, loc.zCoord) > 100) {
					
				}else {
					
					RenderUtils.drawLine(lastLoc.xCoord, lastLoc.yCoord, lastLoc.zCoord, loc.xCoord, loc.yCoord, loc.zCoord);
					
				}
				
				lastLoc = loc;
			}
			
		}
		
	}
	
}
