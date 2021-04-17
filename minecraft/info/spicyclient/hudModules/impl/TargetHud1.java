package info.spicyclient.hudModules.impl;

import java.awt.Color;
import java.text.DecimalFormat;

import info.spicyclient.SpicyClient;
import info.spicyclient.hudModules.HudModule;
import info.spicyclient.modules.combat.Killaura;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;

public class TargetHud1 extends HudModule {
	
	public static transient double healthBarTarget = 0, healthBar = 0;
	
	@Override
	protected void onRender(boolean fakeRender) {
		
		if (!hasSetSize) {
			setSize(new ScaledResolution(mc).getScaledWidth() / 2 - 110, new ScaledResolution(mc).getScaledHeight() / 2 + 170, new ScaledResolution(mc).getScaledWidth() / 2 + 110, new ScaledResolution(mc).getScaledHeight() / 2 + 100);
		}
		
		if (fakeRender) {
			drawOutlineBox();
			return;
		}
		
		if (Killaura.target == null) {
			healthBar = new ScaledResolution(mc).getScaledWidth() / 2 - 41;
			return;
		}
		
		ScaledResolution sr = new ScaledResolution(mc);
		FontRenderer fr = mc.fontRendererObj;
		DecimalFormat dec = new DecimalFormat("#");
		
		healthBarTarget = sr.getScaledWidth() / 2 - 41 + (((140) / (Killaura.target.getMaxHealth())) * (Killaura.target.getHealth()));
		
		// Lower is faster, higher is slower
		double HealthBarSpeed = 5;
		
		if (healthBar > healthBarTarget) {
			healthBar = ((healthBar) - ((healthBar - healthBarTarget) / HealthBarSpeed));
		}
		else if (healthBar < healthBarTarget) {
			//healthBar = healthBarTarget;
			healthBar = ((healthBar) + ((healthBarTarget - healthBar) / HealthBarSpeed));
		}
		//Command.sendPrivateChatMessage(healthBarTarget + " : " + healthBar);
		
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
		
		// Color override
		color = 0xff00ff00;
		if (Killaura.target.hurtResistantTime >= 16) {
			color = 0xffff0000;
		}
		// Color override
		
		Gui.drawRect(sr.getScaledWidth() / 2 - 110, sr.getScaledHeight() / 2 + 100, sr.getScaledWidth() / 2 + 110, sr.getScaledHeight() / 2 + 170, 0xff36393f);
		Gui.drawRect(sr.getScaledWidth() / 2 - 41, sr.getScaledHeight() / 2 + 100 + 54, sr.getScaledWidth() / 2 + 100, sr.getScaledHeight() / 2 + 96 + 45, 0xff202225);
		Gui.drawRect(sr.getScaledWidth() / 2 - 41, sr.getScaledHeight() / 2 + 100 + 54, healthBar, sr.getScaledHeight() / 2 + 96 + 45, color);
		//Gui.drawRect(sr.getScaledWidth() / 2 - 41, sr.getScaledHeight() / 2 + 100 + 54, healthBarTarget, sr.getScaledHeight() / 2 + 96 + 45, color);
		
		GlStateManager.color(1, 1, 1);
		GuiInventory.drawEntityOnScreen(sr.getScaledWidth() / 2 - 75, sr.getScaledHeight() / 2 + 165, 25, 1f, 1f, Killaura.target);
		fr.drawString(Killaura.target.getName(), sr.getScaledWidth() / 2 - 40, sr.getScaledHeight() / 2 + 110, -1);
		fr.drawString("HP: ", sr.getScaledWidth() / 2 - 40, sr.getScaledHeight() / 2 + 125, -1);
		fr.drawString("§c❤: §f" + dec.format(Killaura.target.getHealth()), sr.getScaledWidth() / 2 - 40 + fr.getStringWidth("HP: "), sr.getScaledHeight() / 2 + 125, color);
		//fr.drawString(dec.format(target.getMaxHealth()) + "", sr.getScaledWidth() / 2 - 40 + fr.getStringWidth("HP: ") + fr.getStringWidth(dec.format(target.getHealth()) + " / "), sr.getScaledHeight() / 2 + 125, color);
		
		/*
		RenderHelper.enableGUIStandardItemLighting();
		mc.getRenderItem().renderItemAndEffectIntoGUI(target.getHeldItem(), sr.getScaledWidth() / 2 - 40, sr.getScaledHeight() / 2 + 143);
		mc.getRenderItem().renderItemAndEffectIntoGUI(target.getCurrentArmor(3), sr.getScaledWidth() / 2 - 10, sr.getScaledHeight() / 2 + 143);
		mc.getRenderItem().renderItemAndEffectIntoGUI(target.getCurrentArmor(2), sr.getScaledWidth() / 2 + 20, sr.getScaledHeight() / 2 + 143);
		mc.getRenderItem().renderItemAndEffectIntoGUI(target.getCurrentArmor(1), sr.getScaledWidth() / 2 + 50, sr.getScaledHeight() / 2 + 143);
		mc.getRenderItem().renderItemAndEffectIntoGUI(target.getCurrentArmor(0), sr.getScaledWidth() / 2 + 80, sr.getScaledHeight() / 2 + 143);
		*/
		
		//Gui.drawRect(sr.getScaledWidth() / 2, sr.getScaledHeight() / 2 + 100, sr.getScaledWidth() / 2 + 10, sr.getScaledHeight() / 2 + 150, 0x50000000);
		
	}
	
}
