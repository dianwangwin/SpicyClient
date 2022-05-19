package info.spicyclient.portedMods.antiantixray.Etc;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C07PacketPlayerDigging.Action;
import net.minecraft.util.EnumFacing;

public class Runner implements Runnable {
    boolean isRunning = true;
    long delay;
    int rad;

    public Runner(int rad, long delay) {
        this.rad = rad;
        this.delay = delay;
    }
    
    @Override
    public void run() {
    	
    	net.minecraft.util.BlockPos pos = Minecraft.getMinecraft().thePlayer.getPosition();


        // Blocks that aren't ores but still needs to be checked
        Block[] blocks2check = Config.checkblocks;

        for (int cx = -rad; cx <= rad; cx++) {
            for (int cy = -rad; cy <= rad; cy++) {
                for (int cz = -rad; cz <= rad; cz++) {
                    if (!isRunning) break;
                    net.minecraft.util.BlockPos current = new net.minecraft.util.BlockPos(pos.getX() + cx, pos.getY() + cy, pos.getZ() + cz);
                    assert Minecraft.getMinecraft().theWorld.getBlockState(current).getBlock() == null;
                    Block block = Minecraft.getMinecraft().theWorld.getBlockState(current).getBlock();

                    boolean good = Config.scanAll; // cool for else man

                    // only check if block is a ore or in blocks2check (obsidian for example)
                    for (Block block1 : blocks2check) {
                        if (block.equals(block1)) {
                            good = true;
                            break;
                        }
                    }

                    if (!good) {
                        continue;
                    }
                    
                    C07PacketPlayerDigging packet = new C07PacketPlayerDigging(Action.START_DESTROY_BLOCK, current, EnumFacing.UP);
                    
                    Minecraft.getMinecraft().getNetHandler().getNetworkManager().sendPacketNoEvent(packet);
                    
                    try {
                        Thread.sleep(delay);
                    } catch (InterruptedException ignored) {
                        Logger.info("Shit broke somehow, this shouldn't happen. (Runner thread scanning for fake blocks got interrupted). Did you manually kill the thread?");
                    }
                }
            }
        }
    }
}
