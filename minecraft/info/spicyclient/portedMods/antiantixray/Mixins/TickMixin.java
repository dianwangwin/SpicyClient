package info.spicyclient.portedMods.antiantixray.Mixins;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import info.spicyclient.SpicyClient;
import info.spicyclient.chatCommands.Command;
import info.spicyclient.portedMods.antiantixray.AntiAntiXray;
import info.spicyclient.portedMods.antiantixray.Etc.Config;
import info.spicyclient.portedMods.antiantixray.Etc.Logger;
import info.spicyclient.portedMods.antiantixray.Etc.RefreshingJob;

import java.util.ArrayList;
import java.util.List;

public class TickMixin {
	
	public TickMixin() {
		
	}
	
    public static net.minecraft.util.BlockPos old;
    public static int movedblocks;
    
    public static void tick() {
        List<RefreshingJob> nl = new ArrayList<>();
        AntiAntiXray.jobs.forEach(refreshingJob -> {
            if (!refreshingJob.done) {
                nl.add(refreshingJob);
            }
        });
        AntiAntiXray.jobs = nl;
        if (AntiAntiXray.rvn.checkPressed() && !SpicyClient.config.antiAntiXray.lastTickPressed) {
            assert Minecraft.getMinecraft().thePlayer != null;
            Command.sendPrivateChatMessage("§b[ §fAAX §b] §f", true, "Refreshing blocks...");
            AntiAntiXray.scanForFake(Config.rad, Config.delay);
        }
        
        if (Config.auto) {
            try {
                assert Minecraft.getMinecraft().thePlayer != null;
                net.minecraft.util.BlockPos pos = Minecraft.getMinecraft().thePlayer.getPosition();

                if (pos != old) {
                    movedblocks++;

                    if (movedblocks > Config.mtreshold && AntiAntiXray.jobs.size() == 0) {
                        AntiAntiXray.scanForFake(Config.rad, Config.delay);
                        Logger.info("Scanning new pos: " + pos.getX() + " " + pos.getY() + " " + pos.getZ());
                        movedblocks = 0;
                    }
                }
                old = pos;

            } catch (NullPointerException e) {
                Logger.info("Null Error");
            }
        }
    }
}
