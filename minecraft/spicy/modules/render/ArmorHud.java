package spicy.modules.render;

import java.awt.Color;
import java.util.Comparator;
import java.util.Random;

import org.lwjgl.input.Keyboard;

import net.minecraft.util.Vec3;
import net.minecraft.world.World;
//import io.netty.util.internal.ThreadLocalRandom;
import spicy.SpicyClient;
import spicy.events.Event;
import spicy.events.listeners.EventRenderGUI;
import spicy.events.listeners.EventUpdate;
import spicy.modules.HudModule;
import spicy.modules.Module;
import spicy.settings.BooleanSetting;
import spicy.settings.NumberSetting;
import spicy.settings.SettingChangeEvent;

public class ArmorHud extends HudModule {
	
	public ArmorHud() {
		super("ArmorHud", Keyboard.KEY_NONE, Category.RENDER);
		resetSettings();
	}
	
	@Override
	public void resetSettings() {
		this.settings.clear();
		this.addSettings();
	}
	
	@Override
	public void onSettingChange(SettingChangeEvent e) {
		
	}
	
	public void onEnable() {
		
	}
	
	public void onDisable() {
		World.spicySkyColorSet = false;
	}
	
	private Random rand = new Random();
	private double counter = 0;
	private int count = 0;
	
	public void onEvent(Event e) {
		
		if (e instanceof EventRenderGUI) {
			
			// Render Stuff Here
			
			
			
		}
		
	}
	
}