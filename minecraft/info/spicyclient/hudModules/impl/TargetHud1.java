package info.spicyclient.hudModules.impl;

import java.awt.Color;
import java.text.DecimalFormat;

import info.spicyclient.SpicyClient;
import info.spicyclient.chatCommands.Command;
import info.spicyclient.hudModules.HudModule;
import info.spicyclient.modules.combat.Killaura;
import info.spicyclient.ui.fonts.FontUtil;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;

public class TargetHud1 extends HudModule {
	
	public static transient double healthBarTarget = 0, healthBar = 0, hurtTime = 0, hurtTimeTarget = 0;
	
	@Override
	protected void onRender(boolean fakeRender) {
		
		if (SpicyClient.config.targetHud.mode.is("SpicyV1")) {
			setSize(new ScaledResolution(mc).getScaledWidth() / 2 - 110, new ScaledResolution(mc).getScaledHeight() / 2 + 170, new ScaledResolution(mc).getScaledWidth() / 2 + 110, new ScaledResolution(mc).getScaledHeight() / 2 + 100);
		}
		else if (SpicyClient.config.targetHud.mode.is("SpicyV2")) {
			setSize(0, 65, 220, 0);
		}
		
		if (fakeRender) {
			drawOutlineBox();
			return;
		}
		
		if (Killaura.target == null) {
			healthBar = 0;
			return;
		}
		
		ScaledResolution sr = new ScaledResolution(mc);
		FontRenderer fr = mc.fontRendererObj;
		DecimalFormat dec = new DecimalFormat("#");
		
		// Lower is faster, higher is slower
		double barSpeed = 5;
		if (SpicyClient.config.targetHud.mode.is("SpicyV2")) {
			barSpeed = 5;
		}
		if (healthBar > healthBarTarget) {
			healthBar = ((healthBar) - ((healthBar - healthBarTarget) / barSpeed));
		}
		else if (healthBar < healthBarTarget) {
			healthBar = ((healthBar) + ((healthBarTarget - healthBar) / barSpeed));
		}
		
		if (hurtTime > hurtTimeTarget) {
			hurtTime = ((hurtTime) - ((hurtTime - hurtTimeTarget) / barSpeed));
		}
		else if (hurtTime < healthBarTarget) {
			hurtTime = ((hurtTime) + ((hurtTimeTarget - hurtTime) / barSpeed));
		}
		
		if (SpicyClient.config.targetHud.mode.is("SpicyV1")) {
			healthBarTarget = sr.getScaledWidth() / 2 - 41 + (((140) / (Killaura.target.getMaxHealth())) * (Killaura.target.getHealth()));
			
			int color = (Killaura.target.getHealth() / Killaura.target.getMaxHealth() > 0.66f) ? 0xff00ff00 : (Killaura.target.getHealth() / Killaura.target.getMaxHealth() > 0.33f) ? 0xffff9900 : 0xffff0000;
			
			color = 0xff00ff00;
			
			float[] hsb = Color.RGBtoHSB(((int)SpicyClient.config.hud.colorSettingRed.getValue()), ((int)SpicyClient.config.hud.colorSettingGreen.getValue()), ((int)SpicyClient.config.hud.colorSettingBlue.getValue()), null);
			float hue = hsb[0];
			float saturation = hsb[1];
			color = Color.HSBtoRGB(hue, saturation, 1);;
			
			if (SpicyClient.config.rainbowgui.isEnabled()) {
				float hue1 = System.currentTimeMillis() % (int)((100.5f - SpicyClient.config.rainbowgui.speed.getValue()) * 1000) / (float)((100.5f - SpicyClient.config.rainbowgui.speed.getValue()) * 1000);
				color = Color.HSBtoRGB(hue1, 0.65f, 1);
			}
			
			Gui.drawRect(sr.getScaledWidth() / 2 - 110, sr.getScaledHeight() / 2 + 100, sr.getScaledWidth() / 2 + 110, sr.getScaledHeight() / 2 + 170, 0xff36393f);
			Gui.drawRect(sr.getScaledWidth() / 2 - 41, sr.getScaledHeight() / 2 + 100 + 54, sr.getScaledWidth() / 2 + 100, sr.getScaledHeight() / 2 + 96 + 45, 0xff202225);
			Gui.drawRect(sr.getScaledWidth() / 2 - 41, sr.getScaledHeight() / 2 + 100 + 54, healthBar, sr.getScaledHeight() / 2 + 96 + 45, color);
			
			GlStateManager.color(1, 1, 1);
			GuiInventory.drawEntityOnScreen(sr.getScaledWidth() / 2 - 75, sr.getScaledHeight() / 2 + 165, 25, 1f, 1f, Killaura.target);
			fr.drawString(Killaura.target.getName(), sr.getScaledWidth() / 2 - 40, sr.getScaledHeight() / 2 + 110, -1);
			fr.drawString("HP: ", sr.getScaledWidth() / 2 - 40, sr.getScaledHeight() / 2 + 125, -1);
			fr.drawString("§c❤: §f" + dec.format(Killaura.target.getHealth()), sr.getScaledWidth() / 2 - 40 + fr.getStringWidth("HP: "), sr.getScaledHeight() / 2 + 125, color);
		}
		else if (SpicyClient.config.targetHud.mode.is("SpicyV2")) {
			
			float[] hsb = Color.RGBtoHSB(((int)SpicyClient.config.hud.colorSettingRed.getValue()), ((int)SpicyClient.config.hud.colorSettingGreen.getValue()), ((int)SpicyClient.config.hud.colorSettingBlue.getValue()), null);
			float hue = hsb[0];
			float saturation = hsb[1];
			int color = Color.HSBtoRGB(hue, saturation, 1);;
			
			if (SpicyClient.config.rainbowgui.isEnabled()) {
				float hue1 = System.currentTimeMillis() % (int)((100.5f - SpicyClient.config.rainbowgui.speed.getValue()) * 1000) / (float)((100.5f - SpicyClient.config.rainbowgui.speed.getValue()) * 1000);
				color = Color.HSBtoRGB(hue1, 0.65f, 1);
			}
			
			// Main box
			Gui.drawRect(0, 0, 220, 65, 0x9036393f);
			Gui.drawRect(0, 64, 220, 65, color);
			Gui.drawRect(0, 0, 1, 65, color);
			Gui.drawRect(219, 0, 220, 65, color);
			Gui.drawRect(0, 0, 220, 1, color);
			
			// Health bar
			fr.drawString("❤", 57, 26f, color, false);
			healthBarTarget = (140 * (Killaura.target.getHealth() / Killaura.target.getMaxHealth()));
			if (healthBar > 140) {
				healthBar = 140;
			}
			Gui.drawRect(70, 27, 210, 32.5f, 0xb836393f);
			Gui.drawRect(70, 27, 70 + healthBar, 32.5f, color);
			
			// Hurt time bar
			fr.drawString("⚔", 57, 39, color, false);
			hurtTimeTarget = 140 - (140 * ((float)Killaura.target.hurtResistantTime / (float)Killaura.target.maxHurtResistantTime));
			if (hurtTime > 140) {
				hurtTime = 140;
			}
			Gui.drawRect(70, 40, 210, 45.5f, 0xb836393f);
			Gui.drawRect(70, 40, 70 + hurtTime, 45.5f, color);
			
			// Name
			FontUtil.jelloFontGui.drawString(Killaura.target.getName(), 8, 8, color);
			
			// 3D model of the target
			GlStateManager.color(1, 1, 1);
			GuiInventory.drawEntityOnScreen(30, 60, (int)(30 / Killaura.target.height), 0, 0, Killaura.target);
		}
		
	}
	
}
