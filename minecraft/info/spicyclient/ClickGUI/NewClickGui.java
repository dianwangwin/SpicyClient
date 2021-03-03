package info.spicyclient.ClickGUI;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;

import org.lwjgl.input.Keyboard;

import info.spicyclient.SpicyClient;
import info.spicyclient.modules.Module;
import info.spicyclient.settings.NumberSetting;
import info.spicyclient.settings.Setting;
import info.spicyclient.ui.fonts.FontUtil;
import info.spicyclient.ui.fonts.JelloFontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class NewClickGui extends GuiScreen {
	
	public static NewClickGui clickgui = null;
	
	public static NewClickGui getClickGui() {
		if (clickgui == null) {
			clickgui = new NewClickGui();
		}
		return clickgui;
	}
	
	@Override
	public void initGui() {
		
		clicked = false;
		
	}
	
	public static ArrayList<Tab> tabs = new ArrayList<>();
	private static ResourceLocation gearIcon = new ResourceLocation("spicy/clickgui/gear.png");
	public static int accentColor = 0xffff0000;
	
	private boolean clicked = false, closingSettings = false;
	private Tab selectedTab = null;
	private Module selectedModule = null;
	private double startSettingAnimation = 100;
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		
		JelloFontRenderer fr = FontUtil.spicyClickGuiFont;
		
		int notToggled = 0xfff5f5f5, toggled = 0xffc4c4c4, borderLines = 0xff000000, textColor = 0xff000000,
				sliderColor = 0xffd4d4d4;
		
		toggled = 0xb9171717;
		notToggled = 0xbf2e2e2e;
		borderLines = accentColor;
		textColor = accentColor;
		
		double FONT_HEIGHT = fr.FONT_HEIGHT;
		
		for (Tab t : tabs) {
			
			double width = -1;
			
			ArrayList<Module> modulesInCat = new ArrayList<>();
			
			for (Module m : SpicyClient.modules) {
				if (m.category == t.category) {
					modulesInCat.add(m);
					if (fr.getStringWidth(m.name) + 4 + 20 >= width) {
						width = fr.getStringWidth(m.name) + 4 + 20;
					}
				}
			}
			
			if (clicked && selectedTab != null && t.equals(selectedTab)) {
				t.x = mouseX - t.offsetX;
				t.y = mouseY - t.offsetY;
			}
			
			GlStateManager.pushMatrix();
			Gui.drawRect(t.x, t.y - 2, t.x + width, t.y + 2 + FONT_HEIGHT, notToggled);
			Gui.drawHorizontalLine(t.x, t.x + width, t.y - 2, borderLines);
			if (!t.extended) {
				Gui.drawHorizontalLine(t.x, t.x + width, t.y + 2 + FONT_HEIGHT, borderLines);
			}
			Gui.drawVerticalLine(t.x, t.y - 2, t.y + 2 + FONT_HEIGHT, borderLines);
			Gui.drawVerticalLine(t.x + width, t.y - 2, t.y + 2 + FONT_HEIGHT, borderLines);
			//fr.drawString(t.category.name, t.x, t.y, textColor);
			fr.drawSmoothString(t.category.name,
					(t.x + (((t.x + width) - (t.x + 2)) / 2)) - (fr.getStringWidth(t.category.name) / 2), (float) (t.y),
					textColor);
			GlStateManager.popMatrix();
			
			if (t.extended) {
				int moduleCount = 1;
				for (Module m : modulesInCat) {
					float offset = (float) ((FONT_HEIGHT + 4) * moduleCount);
					GlStateManager.pushMatrix();
					Gui.drawRect(t.x, t.y - 2 + offset, t.x + width, t.y + 2 + FONT_HEIGHT + offset, (m.isEnabled() ? toggled : notToggled));
					if (moduleCount == modulesInCat.size()) {
						Gui.drawHorizontalLine(t.x, t.x + width, t.y + 2 + FONT_HEIGHT + offset, borderLines);
					}
					Gui.drawVerticalLine(t.x, t.y - 4 + offset, t.y + 2 + FONT_HEIGHT + offset, borderLines);
					Gui.drawVerticalLine(t.x + width, t.y - 4 + offset, t.y + 2 + FONT_HEIGHT + offset, borderLines);
					//fr.drawString(t.category.name, t.x, t.y, textColor);
					fr.drawSmoothString(m.name, t.x + 2, (float) (t.y + offset),
							textColor);
					if (m.settings.size() > 1) {
						GlStateManager.enableBlend();
						int imageWidth = 128, imageHeight = 128;
						imageWidth /= 10;
						imageHeight /= 10;
						GlStateManager.pushMatrix();
						GlStateManager.color(((float) SpicyClient.config.hud.colorSettingRed.getValue()),
								((float) SpicyClient.config.hud.colorSettingGreen.getValue()),
								((float) SpicyClient.config.hud.colorSettingBlue.getValue()));
						if (SpicyClient.config.rainbowgui.isEnabled()) {
							float hue = System.currentTimeMillis() % (int) (SpicyClient.hud.rainbowTimer * 1000)
									/ (float) (SpicyClient.hud.rainbowTimer * 1000);
							//int primColor = Color.HSBtoRGB(hue, 0.45f, 1);
							Color color = new Color(Color.HSBtoRGB(hue, 0.45f, 1), false);
							GlStateManager.color(((float) color.getRed()) / 255, ((float) color.getGreen()) / 255,
									((float) color.getBlue()) / 255);
						}
						mc.getTextureManager().bindTexture(gearIcon);
						drawModalRectWithCustomSizedTexture(t.x + width - 14, t.y - 2 + offset, 0, 0, imageWidth,
								imageHeight, imageWidth, imageHeight);
						GlStateManager.popMatrix();
					}
					GlStateManager.popMatrix();
					moduleCount++;
					
				}
				
			}
			
		}
		
		if (selectedModule != null) {
			
			boolean renderSettings = false;
			if (startSettingAnimation < 15 && !closingSettings) {
				renderSettings = true;
			}
			else if (startSettingAnimation > 85 && closingSettings) {
				renderSettings = false;
			}
			else if (startSettingAnimation < 15 && closingSettings) {
				selectedModule = null;
				closingSettings = false;
				return;
			}
			
			startSettingAnimation -= startSettingAnimation/5;
			
			double percent = startSettingAnimation;
			
			if (closingSettings) {
				percent = (double) (100 - startSettingAnimation);
			}
			double left = (this.width / 4) + (((((this.width / 2)) - (this.width / 4)) / 100) * percent),
					up = (this.height / 4) + ((((this.height / 2) - (this.height / 4)) / 100) * percent),
					right = ((this.width / 4) * 3) - (((((this.width / 4) * 3) - (this.width / 2)) / 100) * percent),
					down = ((this.height / 4) * 3) - ((((((this.height / 4) * 3) - (this.height / 2))) / 100) * percent);
			
			Gui.drawRect(left, up, right, down, notToggled);
			Gui.drawHorizontalLine(left, right, up, borderLines);
			Gui.drawHorizontalLine(left, right, down, borderLines);
			Gui.drawVerticalLine(left, up, down, borderLines);
			Gui.drawVerticalLine(right, up, down, borderLines);
			//Gui.drawRect((this.width / 4), (this.height / 4), (this.width / 4) * 3, (this.height / 4) * 3, notToggled);
			
			if (renderSettings) {
				
				GlStateManager.pushMatrix();
				FontUtil.jelloFontScale.drawString(selectedModule.name, (this.width / 4) + 10,
						(float) ((this.height / 4) + 4), textColor);
				GlStateManager.color(1, 1, 1, 1);
				Gui.drawHorizontalLine(left, right, up + FONT_HEIGHT + 9, textColor);
				GlStateManager.popMatrix();
				
				int settingAmount = 0;
				for (Setting s : selectedModule.settings) {
					
					if (s instanceof NumberSetting) {
						double offset = (settingAmount * (FONT_HEIGHT + 4));
						GlStateManager.pushMatrix();
						NumberSetting num = (NumberSetting) s;
						GlStateManager.color(1, 1, 1, 1);
						fr.drawString(num.name + ": " + num.getValue(), (this.width / 4) + 10,
								(float) ((this.height / 4) + 25 + offset), textColor);
						GlStateManager.popMatrix();
					}
					
					settingAmount++;
					
				}
			}
			
		}else {
			startSettingAnimation = 100;
		}
		
	}
	
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		
		if (selectedModule == null) {
			JelloFontRenderer fr = FontUtil.spicyClickGuiFont;
			
			for (Tab t : tabs) {
				
				double width = -1;
				double FONT_HEIGHT = fr.FONT_HEIGHT;
				
				ArrayList<Module> modulesInCat = new ArrayList<>();
				
				for (Module m : SpicyClient.modules) {
					if (m.category == t.category) {
						modulesInCat.add(m);
						if (fr.getStringWidth(m.name) + 4 + 20 >= width) {
							width = fr.getStringWidth(m.name) + 4 + 20;
						}
					}
				}
				
				if (mouseButton == 0) {
					
					if (mouseX >= t.x && mouseX <= t.x + width && mouseY >= t.y - 2 && mouseY <= t.y + 2 + FONT_HEIGHT) {
						selectedTab = t;
						t.offsetX = mouseX - t.getX();
						t.offsetY = mouseY - t.getY();
						clicked = true;
						return;
					}
					
				}
				else if (mouseButton == 1) {
					
					if (mouseX >= t.x && mouseX <= t.x + width && mouseY >= t.y - 2 && mouseY <= t.y + 2 + FONT_HEIGHT) {
						t.extended = !t.extended;
						return;
					}
					
				}
				
				if (t.extended) {
					int moduleCount = 1;
					for (Module m : modulesInCat) {
						float offset = (float) ((FONT_HEIGHT + 4) * moduleCount);
						
						if (mouseX >= t.x && mouseX <= t.x + width && mouseY >= t.y - 2 + offset && mouseY <= t.y + 2 + FONT_HEIGHT + offset) {
							if (mouseButton == 0) {
								m.toggle();
								return;
							}
							else if (mouseButton == 1) {
								selectedModule = m;
								return;
							}
						}
						
						moduleCount++;
					}
				}
				
			}
		}else {
			
		}
		
	}
	
	@Override
	protected void mouseReleased(int mouseX, int mouseY, int state) {
		if (state == 0) {
			selectedTab = null;
			clicked = false;
		}
	}
	
	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		if (keyCode == Keyboard.KEY_ESCAPE || keyCode == SpicyClient.config.clickgui.getKey()) {
			
			if (selectedModule != null && !closingSettings) {
				startSettingAnimation = 100;
				closingSettings = true;
			}else {
				mc.displayGuiScreen(null);
			}
			
		}
	}
	
	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}
	
	private double getDistanceBetweenPoints(double x1, double y1, double x2, double y2) {
		// a²+b²=c²
		// Math.sqrt gets the square root of the number
		return Math.sqrt(((x2-x1)*(x2-x1))+((y2-y1)*(y2-y1)));
	}
	
}
