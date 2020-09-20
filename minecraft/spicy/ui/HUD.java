package spicy.ui;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.CopyOnWriteArrayList;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import spicy.SpicyClient;
import spicy.events.EventType;
import spicy.events.listeners.EventRenderGUI;
import spicy.modules.Module;

public class HUD {
	public boolean rainbowEnabled = false;
	public double rainbowTimer = 4;
	
	public String separator = " | ";
	
	public boolean doesGuiPauseGame()
    {
        return false;
    }
	
	public Minecraft mc = Minecraft.getMinecraft();
	
	public static boolean RainbowGUI = false;
	
	public static boolean ClickGUI = false;
	
	public void draw() {
		
		ScaledResolution sr = new ScaledResolution(mc); 
		FontRenderer fr = mc.fontRendererObj;
		
		float hue = System.currentTimeMillis() % (int)(rainbowTimer * 1000) / (float)(rainbowTimer * 1000);
		int primColor = Color.HSBtoRGB(hue, 0.45f, 1);
		int secColor = Color.HSBtoRGB(hue, 0.45f, 0.55f);
		
		int primaryColor = -1, secondaryColor = 0x80ffffff;
		
		if (rainbowEnabled) {
			primaryColor = primColor;
			secondaryColor = secColor;
		}
		
		// This is here so the modules don't move when you toggle them
		CopyOnWriteArrayList<Module> modules = new CopyOnWriteArrayList<Module>(SpicyClient.modules);
		
		spicy.SpicyClient.modules.sort(Comparator.comparingInt(m -> mc.fontRendererObj.getStringWidth(((Module) m).name + (((Module)m).additionalInformation != "" ? ((Module)m).additionalInformation + separator : ""))).reversed());
		
		// HUD
		
		GlStateManager.pushMatrix();
		GlStateManager.translate(4, 4, 0);
		GlStateManager.scale(2, 2, 1);
		GlStateManager.translate(-4, -4, 0);
		
		fr.drawStringWithShadow(SpicyClient.config.clientName + SpicyClient.config.clientVersion, 4, 4, primaryColor);
		
		GlStateManager.popMatrix();
		
		int count = 0;
		
		for (Module m : spicy.SpicyClient.modules) {
			if (m.toggled) {
				
				double offset = count*(fr.FONT_HEIGHT + 2);
				
				
				
				if (m.additionalInformation != "") {
					Gui.drawRect(sr.getScaledWidth() - fr.getStringWidth(m.name + m.additionalInformation + separator) - 8, 0 + offset, sr.getScaledWidth() - fr.getStringWidth(m.name + m.additionalInformation + separator) - 6, fr.FONT_HEIGHT + 2 + offset, primaryColor);
					Gui.drawRect(sr.getScaledWidth() - fr.getStringWidth(m.name + m.additionalInformation + separator) - 6, 0 + offset, sr.getScaledWidth(), fr.FONT_HEIGHT + 2 + offset, 0x90000000);
					fr.drawStringWithShadow(m.name, sr.getScaledWidth() - fr.getStringWidth(m.name + m.additionalInformation + separator) - 4, (float) (2 + offset), -1);
					fr.drawStringWithShadow(separator, sr.getScaledWidth() - fr.getStringWidth(m.additionalInformation + separator) - 4, (float) (2 + offset), -1);
					// fr.drawStringWithShadow("   " + m.additionalInformation, sr.getScaledWidth() - fr.getStringWidth(m.additionalInformation + separator) - 4, (float) (2 + offset), 0xff9c9c9c);
					fr.drawStringWithShadow(m.additionalInformation, sr.getScaledWidth() - fr.getStringWidth(m.additionalInformation) - 4, (float) (2 + offset), 0xff9c9c9c);
				}else {
					Gui.drawRect(sr.getScaledWidth() - fr.getStringWidth(m.name + m.additionalInformation) - 8, 0 + offset, sr.getScaledWidth() - fr.getStringWidth(m.name + m.additionalInformation) - 6, fr.FONT_HEIGHT + 2 + offset, primaryColor);
					Gui.drawRect(sr.getScaledWidth() - fr.getStringWidth(m.name + m.additionalInformation) - 6, 0 + offset, sr.getScaledWidth(), fr.FONT_HEIGHT + 2 + offset, 0x90000000);
					fr.drawStringWithShadow(m.name, sr.getScaledWidth() - fr.getStringWidth(m.name + m.additionalInformation) - 4, (float) (2 + offset), -1);
				}
				
				double bottemLines = 0;
				
				ArrayList<Module> enabledModules = new ArrayList<Module>();
				for (Module e : SpicyClient.modules) {
					if (e.isEnabled()) {
						enabledModules.add(e);
					}
				}
				
				if (enabledModules.indexOf(m) != enabledModules.size() - 1) {
					bottemLines = (fr.getStringWidth(enabledModules.get(enabledModules.indexOf(m)).name)) - (fr.getStringWidth(enabledModules.get(enabledModules.indexOf(m) + 1).name + (enabledModules.get(enabledModules.indexOf(m) + 1).additionalInformation != "" ? enabledModules.get(enabledModules.indexOf(m) + 1).additionalInformation + separator : "")));
				}else {
					bottemLines = (fr.getStringWidth(enabledModules.get(enabledModules.indexOf(m)).name + (enabledModules.get(enabledModules.indexOf(m)).additionalInformation == "" ? enabledModules.get(enabledModules.indexOf(m)).additionalInformation + separator : ""))) + 10;
				}
				
				Gui.drawRect(sr.getScaledWidth() - fr.getStringWidth(m.name + (m.additionalInformation != "" ? m.additionalInformation + separator : "")) - 8, (0 + offset) + (fr.FONT_HEIGHT + 2), (sr.getScaledWidth() - fr.getStringWidth(m.name) - 6) + (bottemLines), (2 + offset) + (fr.FONT_HEIGHT + 2), primaryColor);
				//Gui.drawRect(sr.getScaledWidth() - fr.getStringWidth(m.name) - 8, 0 + offset, sr.getScaledWidth() - fr.getStringWidth(m.name) - 6, fr.FONT_HEIGHT + 2 + offset, primaryColor);
				
				count++;
				
				
			}
		}
		
		// This is here so the modules don't move when you toggle them
		SpicyClient.modules = modules;
		
		EventRenderGUI event = new EventRenderGUI();
		event.setType(EventType.PRE);
		
		spicy.SpicyClient.onEvent(event);
		
	}
	
}
