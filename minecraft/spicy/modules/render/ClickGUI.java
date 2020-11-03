package spicy.modules.render;

import java.awt.Color;
import java.util.List;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGameOver;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraft.client.gui.ScaledResolution;
import spicy.SpicyClient;
import spicy.events.Event;
import spicy.events.listeners.EventKey;
import spicy.events.listeners.EventRenderGUI;
import spicy.events.listeners.EventUpdate;
import spicy.modules.Module;
import spicy.settings.BooleanSetting;
import spicy.settings.ModeSetting;
import spicy.settings.NumberSetting;
import spicy.settings.SettingChangeEvent;
import spicy.ui.HUD;
import spicy.util.Timer;

public class ClickGUI extends Module{
	
	public static Minecraft mc = Minecraft.getMinecraft();
	public static spicy.ClickGUI.ClickGUI clickGui = new spicy.ClickGUI.ClickGUI(null);
	
	public BooleanSetting sound = new BooleanSetting("Sound", true);
	public NumberSetting volume = new NumberSetting("Volume", 0.5, 0.1, 1.0, 0.1);
	public ModeSetting mode = new ModeSetting("Separator Mode", " | ", " | ", " OwO ", " UwU ", " |OwO| ", " |UwU| ", "Switch between OwO and UwU", "Switch between :OwO: and :UwU:", " - ");
	public NumberSetting colorSettingRed = new NumberSetting("Red", 255, 0, 255, 1);
	public NumberSetting colorSettingGreen = new NumberSetting("Green", 255, 0, 255, 1);
	public NumberSetting colorSettingBlue = new NumberSetting("Blue", 255, 0, 255, 1);
	
	public ClickGUI() {
		super("ClickGUI", Keyboard.KEY_RSHIFT, Category.RENDER);
		resetSettings();
	}
	
	@Override
	public void resetSettings() {
		this.settings.clear();
		this.addSettings(sound, volume, mode, colorSettingRed, colorSettingGreen, colorSettingBlue);
	}
	
	@Override
	public void toggle() {
		toggled = !toggled;
		if (toggled) {
			onEnable();
		}else {
			onDisable();
		}
	}
	
	public void onEnable() {
		spicy.ClickGUI.ClickGUI clickGui = new spicy.ClickGUI.ClickGUI(null);
		mc.displayGuiScreen(clickGui);
	}
	
	public void onDisable() {
		mc.displayGuiScreen(clickGui.last);
	}
	
	@Override
	public void onSettingChange(SettingChangeEvent e) {
		
		if (e != null && mode != null) {
			
			if (e.setting.equals(mode)) {
				if (mode.is("Switch between OwO and UwU")) {
					
					SpicyClient.hud.separator = " UwU ";
					OwO = false;
					
				}
				else if (mode.is("Switch between :OwO: and :UwU:")) {
					
					SpicyClient.hud.separator = " :UwU: ";
					OwO = false;
					
				}
			}
			
		}
		
	}
	
	private Timer timer = new Timer();
	private boolean OwO = true;
	
	public void onEvent(Event e) {
		
		if (e instanceof EventUpdate) {
			
			if (e.isPre()) {
				
				String hex = String.format("#%02X%02X%02X", ((int)colorSettingRed.getValue()), ((int)colorSettingGreen.getValue()), ((int)colorSettingBlue.getValue()));
				
				float[] hsb = Color.RGBtoHSB(((int)colorSettingRed.getValue()), ((int)colorSettingGreen.getValue()), ((int)colorSettingBlue.getValue()), null);
				 
				float hue = hsb[0];
				 
				float saturation = hsb[1];
				 
				float brightness = hsb[2];
				
				spicy.ClickGUI.ClickGUI.accentColor = Color.HSBtoRGB(hue, saturation, 1);
				SpicyClient.hud.primaryColor = Color.HSBtoRGB(hue, saturation, 1);
				SpicyClient.hud.secondaryColor = Color.HSBtoRGB(hue, saturation, 0.6f);
				SpicyClient.config.tabgui.primaryColor = Color.HSBtoRGB(hue, saturation, 1);
				SpicyClient.config.tabgui.secondaryColor = Color.HSBtoRGB(hue, saturation, 0.6f);
				
				// For the separator
				if (mode.is("Switch between OwO and UwU") || mode.is("Switch between :OwO: and :UwU:")) {
					
					if (timer.hasTimeElapsed(750L, true)) {
						
						if (mode.is("Switch between OwO and UwU")) {
							
							if (OwO) {
								SpicyClient.hud.separator = " UwU ";
								OwO = false;
							}else {
								SpicyClient.hud.separator = " OwO ";
								OwO = true;
							}
							
						}
						else if (mode.is("Switch between :OwO: and :UwU:")) {
							
							if (OwO) {
								SpicyClient.hud.separator = " :UwU: ";
								OwO = false;
							}else {
								SpicyClient.hud.separator = " :OwO: ";
								OwO = true;
							}
							
						}
						
					}
					
				}else {
					
					SpicyClient.hud.separator = mode.getMode();
					
				}
				
			}
			
		}
		
	}
	
}
