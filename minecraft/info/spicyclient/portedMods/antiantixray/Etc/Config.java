package info.spicyclient.portedMods.antiantixray.Etc;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;

import org.lwjgl.input.Keyboard;

import java.io.IOException;

public class Config {
    public static int rad = 5;
    public static long delay = 1000;
    public static boolean scanAll = false;
    public static boolean auto = false;
    public static int mtreshold = 5;
    public static Block[] checkblocks = {Blocks.obsidian, Blocks.clay, Blocks.mossy_cobblestone,
            Blocks.diamond_ore, Blocks.redstone_ore, Blocks.iron_ore, Blocks.coal_ore, Blocks.lapis_ore,
            Blocks.gold_ore, Blocks.emerald_ore, Blocks.quartz_ore};
    public static int kcScan = Keyboard.KEY_Y;
    public static int kcRemove = Keyboard.KEY_V;


}


