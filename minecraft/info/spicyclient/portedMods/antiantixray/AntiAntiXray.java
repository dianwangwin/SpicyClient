package info.spicyclient.portedMods.antiantixray;

import net.minecraft.client.Minecraft;

import java.util.ArrayList;
import java.util.List;

import info.spicyclient.portedMods.antiantixray.Etc.*;

public class AntiAntiXray {
    public static KeyBind rvn = new KeyBind(Config.kcScan);
    public static KeyBind removeBlockBeta = new KeyBind(Config.kcRemove);
    public static List<RefreshingJob> jobs = new ArrayList<>();

    public static void scanForFake(int rad, long delayInMS) {
        RefreshingJob rfj = new RefreshingJob(new Runner(rad, delayInMS));
        jobs.add(rfj);
    }
    
}
