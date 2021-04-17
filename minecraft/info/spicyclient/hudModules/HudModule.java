package info.spicyclient.hudModules;

import java.util.concurrent.CopyOnWriteArrayList;

import info.spicyclient.hudModules.impl.Keystrokes1;
import info.spicyclient.hudModules.impl.TargetHud1;
import info.spicyclient.util.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;

public abstract class HudModule {
	
	public static Minecraft mc = Minecraft.getMinecraft();
	public static transient CopyOnWriteArrayList<HudModule> mods = new CopyOnWriteArrayList<HudModule>();
	
	public static void fakeRenderAllhudMods() {
		for (HudModule mod : mods) {
			mod.draw(true);
		}
	}
	
	public static HudModule getHudModuleFromPos(double x, double y) {
		
		for (HudModule tempMod: mods) {
			
			if (x - tempMod.offsetX < tempMod.right && x - tempMod.offsetX > tempMod.left && y - tempMod.offsetY < tempMod.down && y - tempMod.offsetY > tempMod.up) {
				return tempMod;
			}
			
		}
		
		return null;
		
	}
	
	public static class HudModuleConfig{
		
		public TargetHud1 targetHud1 = new TargetHud1();
		public Keystrokes1 keystrokes1 = new Keystrokes1();
		
		public void resetFuckingModsListBecauseGoogleFuckingSucksAndTheirLibIsShitAndCannotLoadAFUCKINGClassCorrectlyWithoutFuckingItUpBeondBelief() {
			mods.clear();
			mods.add(targetHud1);
			mods.add(keystrokes1);
		}
		
	}
	
	public HudModule() {
		mods.add(this);
	}
	
	public double offsetX = 0, offsetY = 0;
	public transient double tempOffsetX = 0, tempOffsetY = 0, left, up, right, down;
	
	private void startGL() {
		GlStateManager.pushMatrix();
		GlStateManager.enableBlend();
		RenderUtils.resetColor();
		GlStateManager.translate(offsetX, offsetY, 0);
	}
	
	private void endGL() {
		GlStateManager.popMatrix();
	}
	
	protected void onRender(boolean fakeRender) {
		
	}
	
	public void draw(boolean fakeRender) {
		try {
			startGL();
			onRender(fakeRender);
			endGL();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	protected void drawOutlineBox() {
		Gui.drawRect(left, down, right, up, 0x9036393f);
	}
	
	protected transient boolean hasSetSize = false;
	
	protected void setSize(double left, double down, double right, double up) {
		hasSetSize = true;
		this.left = left;
		this.down = down;
		this.right = right;
		this.up = up;
	}
	
}
