package info.spicyclient.ClickGUI;

import java.awt.Color;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import org.lwjgl.input.Keyboard;

import info.spicyclient.SpicyClient;
import info.spicyclient.chatCommands.Command;
import info.spicyclient.modules.Module;
import info.spicyclient.settings.BooleanSetting;
import info.spicyclient.settings.KeybindSetting;
import info.spicyclient.settings.ModeSetting;
import info.spicyclient.settings.NumberSetting;
import info.spicyclient.settings.Setting;
import info.spicyclient.ui.fonts.FontUtil;
import info.spicyclient.ui.fonts.JelloFontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import optifine.FontUtils;

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
		selectedSetting = null;
		
	}
	
	private static ResourceLocation gearIcon = new ResourceLocation("spicy/clickgui/gear.png");
	private static ResourceLocation dropdownIcon = new ResourceLocation("spicy/clickgui/dropdown.png");
	private static ResourceLocation circleIcon = new ResourceLocation("spicy/clickgui/circle.png");
	
	//public static ArrayList<Tab> tabs = new ArrayList<>();
	public static int accentColor = 0xffff0000;
	
	private boolean clicked = false, closingSettings = false;
	private double startSettingAnimation = 100;
	
	private Tab selectedTab = null;
	private Module selectedModule = null;
	private Setting selectedSetting = null;
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		
		ArrayList<Tab> tabs = SpicyClient.savedTabs.tabs;
		JelloFontRenderer fr = FontUtil.spicyClickGuiFont;
		
		int notToggled = 0xfff5f5f5, toggled = 0xffc4c4c4, borderLines = 0xff000000, textColor = 0xff000000,
				sliderColor = 0xffd4d4d4, dropdownBackground = 0xff000000;
		
		float hue = System.currentTimeMillis() % (int) (SpicyClient.hud.rainbowTimer * 1000)
				/ (float) (SpicyClient.hud.rainbowTimer * 1000);
		//int primColor = Color.HSBtoRGB(hue, 0.45f, 1);
		Color color = new Color(Color.HSBtoRGB(hue, 0.45f, 1), false);
		
		if (SpicyClient.config.rainbowgui.isEnabled()) {
			accentColor = Color.HSBtoRGB(hue, 0.45f, 1);
		}
		
		toggled = 0xb9171717;
		notToggled = 0xbf2e2e2e;
		borderLines = accentColor;
		textColor = accentColor;
		sliderColor = accentColor;
		
		double FONT_HEIGHT = fr.FONT_HEIGHT;
		
		for (Tab t : tabs) {
			
			double width = fr.getStringWidth(t.category.name) + 4 + 20;
			
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
						setColorForIcon(color);
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
					double offset = (settingAmount * (FONT_HEIGHT + SpicyClient.config.clickgui.padding.getValue()));
					
					if (s instanceof NumberSetting) {
						GlStateManager.pushMatrix();
						NumberSetting num = (NumberSetting) s;
						GlStateManager.color(1, 1, 1, 1);
						fr.drawString(num.name + ": " + num.getValue(), (this.width / 4) + 10,
								(float) ((this.height / 4) + 25 + offset), textColor);
						
						double maxNumberLength = fr.getStringWidth(new DecimalFormat("#.####").format(num.getValue()));
						
						for (double i = num.getMinimum() ; i < num.getMaximum(); i += num.getIncrement()) {
							if (maxNumberLength <= fr.getStringWidth(new DecimalFormat("#.####").format(i))) {
								maxNumberLength = fr.getStringWidth(new DecimalFormat("#.####").format(i));
							}
						}
						GlStateManager.color(1, 1, 1, 1);
						Gui.drawHorizontalLine(
								(this.width / 4) + 10 + fr.getStringWidth(num.name + ": ") + maxNumberLength + 16,
								right - 20, (this.height / 4) + 25 + offset + (FONT_HEIGHT / 2), sliderColor);
						
						int imageWidth = 128, imageHeight = 128;
						imageWidth /= 10;
						imageHeight /= 10;
						mc.getTextureManager().bindTexture(circleIcon);
						
						if (selectedSetting != null && selectedSetting == num) {
							
							double percentage = (((mouseX - (imageWidth / 2)) - ((this.width / 4) + 10 + fr.getStringWidth(num.name + ": ")
									+ maxNumberLength + 16))
									/ ((right - 20) - ((this.width / 4) + 10 + fr.getStringWidth(num.name + ": ")
											+ maxNumberLength + 16)))
									* 100;
							
							num.setValue((num.getMaximum() / 100) * percentage);
							
							double dragX = mouseX - (imageWidth / 2);
							
							if (dragX < (this.width / 4) + 10 + fr.getStringWidth(num.name + ": ") + maxNumberLength + 16) {
								dragX = (this.width / 4) + 10 + fr.getStringWidth(num.name + ": ") + maxNumberLength + 16;
							}
							else if (dragX > (right - 20)) {
								dragX = (right - 20);
							}
							drawModalRectWithCustomSizedTexture(
									dragX,
									(this.height / 4) + 24 + offset, 0, 0, imageWidth, imageHeight, imageWidth,
									imageHeight);
							
						}else {
							drawModalRectWithCustomSizedTexture(
									((this.width / 4) + 10 + fr.getStringWidth(num.name + ": ") + maxNumberLength + 16)
											+ (((((right - 20) - ((this.width / 4) + 10 + fr.getStringWidth(num.name + ": ")
													+ maxNumberLength + 16))) / 100)
													* ((num.getValue() / num.getMaximum()) * 100)),
									(this.height / 4) + 24 + offset, 0, 0, imageWidth, imageHeight, imageWidth,
									imageHeight);
						}
						
						GlStateManager.popMatrix();
						
					}
					else if (s instanceof BooleanSetting) {
						GlStateManager.pushMatrix();
						BooleanSetting bool = (BooleanSetting) s;
						GlStateManager.color(1, 1, 1, 1);
						fr.drawString(bool.name + ": ", (this.width / 4) + 10,
								(float) ((this.height / 4) + 25 + offset), textColor);
						
						GlStateManager.enableBlend();
						GlStateManager.color(((float) SpicyClient.config.hud.colorSettingRed.getValue()),
								((float) SpicyClient.config.hud.colorSettingGreen.getValue()),
								((float) SpicyClient.config.hud.colorSettingBlue.getValue()), 0.25f);
						
						if (SpicyClient.config.rainbowgui.isEnabled()) {
							GlStateManager.color(((float) color.getRed()) / 255, ((float) color.getGreen()) / 255,
									((float) color.getBlue()) / 255, 0.25f);
						}
						
						if (bool.isEnabled()) {
							setColorForIcon(color);
						}
						int imageWidth = 128, imageHeight = 128;
						imageWidth /= 10;
						imageHeight /= 10;
						mc.getTextureManager().bindTexture(circleIcon);
						drawModalRectWithCustomSizedTexture(
								(this.width / 4) + (imageWidth / 2) + 5 + fr.getStringWidth(bool.name + ": "),
								(this.height / 4) + 24 + offset, 0, 0, imageWidth, imageHeight, imageWidth,
								imageHeight);
						GlStateManager.popMatrix();
					}
					else if (s instanceof KeybindSetting) {
						GlStateManager.pushMatrix();
						KeybindSetting key = (KeybindSetting) s;
						GlStateManager.color(1, 1, 1, 1);
						if (selectedSetting != null && selectedSetting == key) {
							fr.drawString(key.name + ": Binding key...", (this.width / 4) + 10,
									(float) ((this.height / 4) + 25 + offset), textColor);
						}else {
							fr.drawString(key.name + ": " + Keyboard.getKeyName(key.getKeycode()), (this.width / 4) + 10,
									(float) ((this.height / 4) + 25 + offset), textColor);
						}
						GlStateManager.popMatrix();
					}
					else if (s instanceof ModeSetting) {
						GlStateManager.pushMatrix();
						ModeSetting mode = (ModeSetting) s;
						GlStateManager.color(1, 1, 1, 1);
						fr.drawString(mode.name + ":", (this.width / 4) + 10,
								(float) ((this.height / 4) + 25 + offset), textColor);
						GlStateManager.color(1, 1, 1);
						
						
						double modeWidth = 0;
						for (String string : mode.modes) {
							if (fr.getStringWidth(string) > modeWidth) {
								modeWidth = fr.getStringWidth(string);
							}
						}
						modeWidth += fr.getStringWidth(mode.name + ": ");
						
						setColorForIcon(color);
						int imageWidth = 128, imageHeight = 128;
						imageWidth /= 10;
						imageHeight /= 10;
						
						if (selectedSetting != null && selectedSetting == mode) {
							
						}else {
							fr.drawString(mode.getMode(), (this.width / 4) + 10 + fr.getStringWidth(mode.name + ": "),
									(float) ((this.height / 4) + 25 + offset), textColor);
							GlStateManager.color(1, 1, 1);
							mc.getTextureManager().bindTexture(dropdownIcon);
							drawModalRectWithCustomSizedTexture(
									(this.width / 4) + (imageWidth / 2) + 5 + modeWidth,
									(this.height / 4) + 24 + offset, 0, 0, imageWidth, imageHeight, imageWidth,
									imageHeight);
							
							Gui.drawHorizontalLine((this.width / 4) + 7.5 + fr.getStringWidth(mode.name + ": "),
									(this.width / 4) + (imageWidth / 2) + 5 + modeWidth + 13,
									(this.height / 4) + 23 + offset, borderLines);
							Gui.drawHorizontalLine((this.width / 4) + 7.5 + fr.getStringWidth(mode.name + ": "),
									(this.width / 4) + (imageWidth / 2) + 5 + modeWidth + 13,
									(this.height / 4) + 26 + offset + FONT_HEIGHT, borderLines);
							Gui.drawVerticalLine((this.width / 4) + 7.5 + fr.getStringWidth(mode.name + ": "),
									(this.height / 4) + 23 + offset, (this.height / 4) + 26 + offset + FONT_HEIGHT,
									borderLines);
							Gui.drawVerticalLine((this.width / 4) + (imageWidth / 2) + 5 + modeWidth + 13,
									(this.height / 4) + 23 + offset, (this.height / 4) + 26 + offset + FONT_HEIGHT,
									borderLines);
						}
						
						GlStateManager.popMatrix();
					}
					
					settingAmount++;
					
				}
				settingAmount = 0;
				if (selectedSetting instanceof ModeSetting) {
					
					for (Setting s : selectedModule.settings) {
						double offset = (settingAmount * (FONT_HEIGHT + SpicyClient.config.clickgui.padding.getValue()));
						
						if (s instanceof ModeSetting && selectedSetting == s) {
							
							GlStateManager.pushMatrix();
							ModeSetting mode = (ModeSetting) s;
							
							double modeWidth = 0;
							for (String string : mode.modes) {
								if (fr.getStringWidth(string) >= modeWidth) {
									modeWidth = fr.getStringWidth(string);
								}
							}
							modeWidth += fr.getStringWidth(mode.name + ": ");
							
							int imageWidth = 128, imageHeight = 128;
							imageWidth /= 10;
							imageHeight /= 10;
							
							if (selectedSetting != null && selectedSetting == mode) {
								
								Gui.drawRect((this.width / 4) + 7.5 + fr.getStringWidth(mode.name + ": "), (this.height / 4) + 26 + offset + FONT_HEIGHT + ((mode.modes.size() - 1) * (FONT_HEIGHT + 5.5)), (this.width / 4) + (imageWidth / 2) + 5 + modeWidth + 13, (this.height / 4) + 24 + offset, dropdownBackground);
								double modeNum = 0;
								for (String string : mode.modes) {
									
									double modeOffset = (modeNum * (FONT_HEIGHT + 5.5));
									
									GlStateManager.color(1, 1, 1, 1);
									//Gui.drawRect((this.width / 4) + 7.5 + fr.getStringWidth(mode.name + ": "),
											//(this.height / 4) + 19.4 + 3.25 + offset + modeOffset,
											//(this.width / 4) + (imageWidth / 2) + 5 + modeWidth + 13,
											//(this.height / 4) + 24.5 + 3.25 + offset + FONT_HEIGHT + modeOffset,
											//dropdownBackground);
									
									fr.drawString(string, (this.width / 4) + 10 + fr.getStringWidth(mode.name + ": "),
											(float) ((this.height / 4) + 25 + offset + modeOffset), textColor);
									GlStateManager.color(1, 1, 1, 1);
									
									if (mode.modes.indexOf(string) == mode.modes.size() - 1) {
										Gui.drawHorizontalLine((this.width / 4) + 7.5 + fr.getStringWidth(mode.name + ": "),
												(this.width / 4) + (imageWidth / 2) + 5 + modeWidth + 13,
												(this.height / 4) + 26 + offset + FONT_HEIGHT + modeOffset, borderLines);
										Gui.drawVerticalLine((this.width / 4) + 7.5 + fr.getStringWidth(mode.name + ": "),
												(this.height / 4) + 19 + offset + modeOffset, (this.height / 4) + 26 + offset + FONT_HEIGHT + modeOffset,
												borderLines);
										Gui.drawVerticalLine((this.width / 4) + (imageWidth / 2) + 5 + modeWidth + 13,
												(this.height / 4) + 19 + offset + modeOffset, (this.height / 4) + 26 + offset + FONT_HEIGHT + modeOffset,
												borderLines);
									}
									else if (mode.modes.indexOf(string) == 0) {
										Gui.drawHorizontalLine((this.width / 4) + 7.5 + fr.getStringWidth(mode.name + ": "),
												(this.width / 4) + (imageWidth / 2) + 5 + modeWidth + 13,
												(this.height / 4) + 23 + offset + modeOffset, borderLines);
										Gui.drawVerticalLine((this.width / 4) + 7.5 + fr.getStringWidth(mode.name + ": "),
												(this.height / 4) + 23 + offset + modeOffset, (this.height / 4) + 26 + offset + FONT_HEIGHT + modeOffset,
												borderLines);
										Gui.drawVerticalLine((this.width / 4) + (imageWidth / 2) + 5 + modeWidth + 13,
												(this.height / 4) + 23 + offset + modeOffset, (this.height / 4) + 26 + offset + FONT_HEIGHT + modeOffset,
												borderLines);
									}else {
										Gui.drawVerticalLine((this.width / 4) + 7.5 + fr.getStringWidth(mode.name + ": "),
												(this.height / 4) + 19 + offset + modeOffset, (this.height / 4) + 26 + offset + FONT_HEIGHT + modeOffset,
												borderLines);
										Gui.drawVerticalLine((this.width / 4) + (imageWidth / 2) + 5 + modeWidth + 13,
												(this.height / 4) + 19 + offset + modeOffset, (this.height / 4) + 26 + offset + FONT_HEIGHT + modeOffset,
												borderLines);
									}
									
									modeNum++;
									
								}
								
							}
							GlStateManager.popMatrix();
						}
						
						settingAmount++;
						
					}
					
					
				}
				
			}
			
		}else {
			startSettingAnimation = 100;
		}
		
	}
	
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		
		if (selectedModule == null) {
			
			ArrayList<Tab> tabs = SpicyClient.savedTabs.tabs;
			JelloFontRenderer fr = FontUtil.spicyClickGuiFont;
			
			for (Tab t : tabs) {
				
				double width = fr.getStringWidth(t.category.name) + 4 + 20;
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
			
			boolean renderSettings = false;
			if (startSettingAnimation < 15 && !closingSettings) {
				renderSettings = true;
			}
			
			double percent = startSettingAnimation;
			
			if (closingSettings) {
				percent = (double) (100 - startSettingAnimation);
			}
			double left = (this.width / 4) + (((((this.width / 2)) - (this.width / 4)) / 100) * percent),
					up = (this.height / 4) + ((((this.height / 2) - (this.height / 4)) / 100) * percent),
					right = ((this.width / 4) * 3) - (((((this.width / 4) * 3) - (this.width / 2)) / 100) * percent),
					down = ((this.height / 4) * 3) - ((((((this.height / 4) * 3) - (this.height / 2))) / 100) * percent);
			
			if (mouseX > left && mouseX < right && mouseY > up && mouseY < down) {
				
				if (renderSettings) {
					
					JelloFontRenderer fr = FontUtil.spicyClickGuiFont;
					
					boolean modeDropdownOpen = (selectedSetting != null && selectedSetting instanceof ModeSetting);
					
					int settingAmount = 0;
					for (Setting s : selectedModule.settings) {
						double offset = (settingAmount * (FontUtil.spicyClickGuiFont.FONT_HEIGHT + SpicyClient.config.clickgui.padding.getValue()));
						
						if (s instanceof NumberSetting && !modeDropdownOpen) {
							
							NumberSetting num = (NumberSetting) s;
							
							double imageWidth = 12.8;
							
							double maxNumberLength = fr.getStringWidth(new DecimalFormat("#.####").format(num.getValue()));
							
							for (double i = num.getMinimum() ; i < num.getMaximum(); i += num.getIncrement()) {
								if (maxNumberLength <= fr.getStringWidth(new DecimalFormat("#.####").format(i))) {
									maxNumberLength = fr.getStringWidth(new DecimalFormat("#.####").format(i));
								}
							}
							
							if (mouseX > (this.width / 4) + 10 + fr.getStringWidth(num.name + ": ") + maxNumberLength
									+ 16 && mouseX < right - 20 + imageWidth && mouseY > (this.height / 4) + 23.5 + offset
									&& mouseY < (this.height / 4) + 27 + offset + fr.FONT_HEIGHT) {
								selectedSetting = num;
							}
							
						}
						else if (s instanceof BooleanSetting && !modeDropdownOpen) {
							
							BooleanSetting bool = (BooleanSetting) s;
							
							double imageWidth = 12.8;
							
							if (mouseX > (this.width / 4) + 5
									&& mouseX < (this.width / 4) + (imageWidth / 2) + imageWidth + 6
											+ fr.getStringWidth(bool.name + ": ")
									&& mouseY > (this.height / 4) + 20 + offset
									&& mouseY < (this.height / 4) + 28 + offset + fr.FONT_HEIGHT) {
								bool.toggle();
								return;
							}
							
						}
						else if (s instanceof KeybindSetting && !modeDropdownOpen) {
							
							KeybindSetting key = (KeybindSetting) s;
							
							if (mouseX > (this.width / 4) + 5
									&& mouseX < (this.width / 4) + 13
											+ fr.getStringWidth(key.name + ": " + Keyboard.getKeyName(key.getKeycode()))
									&& mouseY > (this.height / 4) + 20 + offset
									&& mouseY < (this.height / 4) + 28 + offset + fr.FONT_HEIGHT) {
								selectedSetting = key;
								return;
							}
							
						}
						else if (s instanceof ModeSetting) {
							
							ModeSetting mode = (ModeSetting) s;
							
							double imageWidth = 12.8;
							double FONT_HEIGHT = fr.FONT_HEIGHT;
							
							double modeWidth = 0;
							for (String string : mode.modes) {
								if (fr.getStringWidth(string) >= modeWidth) {
									modeWidth = fr.getStringWidth(string);
								}
							}
							modeWidth += fr.getStringWidth(mode.name + ": ");
							
							if (mouseX >= (this.width / 4) + 6.5 + fr.getStringWidth(mode.name + ": ") && mouseX <= (this.width / 4) + (imageWidth / 2) + 5 + modeWidth + 14 && mouseY <= (this.height / 4) + 27 + offset + FONT_HEIGHT + (modeDropdownOpen ? ((mode.modes.size() - 1) * (FONT_HEIGHT + 5.5)) : 0) && mouseY >= (this.height / 4) + 22 + offset) {
								
								if (selectedSetting != null && mode == selectedSetting) {
									
									for (String string : mode.modes) {
										
										if (mouseX >= (this.width / 4) + 6.5 + fr.getStringWidth(mode.name + ": ") && mouseX <= (this.width / 4) + (imageWidth / 2) + 5 + modeWidth + 14 && mouseY <= (this.height / 4) + 27 + offset + FONT_HEIGHT + ((mode.modes.indexOf(string)) * (FONT_HEIGHT + 5.5)) && mouseY >= (this.height / 4) + 22 + offset + ((mode.modes.indexOf(string)) * (FONT_HEIGHT + 5.5))) {
											mode.setMode(string);
											selectedSetting = null;
											return;
										}
										
									}
									
								}else if (!modeDropdownOpen) {
									selectedSetting = mode;
									return;
								}
								
							}else {
								if (selectedSetting != null && mode == selectedSetting) {
									selectedSetting = null;
								}
							}
							
							//Gui.drawHorizontalLine((this.width / 4) + 7.5 + fr.getStringWidth(mode.name + ": "), (this.width / 4) + (imageWidth / 2) + 5 + modeWidth + 13, (this.height / 4) + 23 + offset, borderLines);
							//Gui.drawHorizontalLine((this.width / 4) + 7.5 + fr.getStringWidth(mode.name + ": "), (this.width / 4) + (imageWidth / 2) + 5 + modeWidth + 13, (this.height / 4) + 26 + offset + FONT_HEIGHT, borderLines);
							//Gui.drawVerticalLine((this.width / 4) + 7.5 + fr.getStringWidth(mode.name + ": "), (this.height / 4) + 23 + offset, (this.height / 4) + 26 + offset + FONT_HEIGHT, borderLines);
							//Gui.drawVerticalLine((this.width / 4) + (imageWidth / 2) + 5 + modeWidth + 13, (this.height / 4) + 23 + offset, (this.height / 4) + 26 + offset + FONT_HEIGHT, borderLines);
							
						}
						
						settingAmount++;
						
					}
					
				}
				
			}else {
				startSettingAnimation = 100;
				closingSettings = true;
				selectedSetting = null;
			}
			
		}
		
	}
	
	@Override
	protected void mouseReleased(int mouseX, int mouseY, int state) {
		if (state == 0) {
			selectedTab = null;
			clicked = false;
		}
		
		if (selectedSetting != null && selectedSetting instanceof NumberSetting) {
			selectedSetting = null;
		}
		
	}
	
	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		if (selectedSetting != null & selectedSetting instanceof KeybindSetting) {
			KeybindSetting key = (KeybindSetting) selectedSetting;
			if (keyCode != Keyboard.KEY_ESCAPE) {
				key.setKeycode(keyCode);
			}else {
				key.setKeycode(Keyboard.KEY_NONE);
			}
			selectedSetting = null;
		}else {
			if (keyCode == Keyboard.KEY_ESCAPE || keyCode == SpicyClient.config.clickgui.getKey()) {
				
				if (selectedSetting instanceof ModeSetting) {
					selectedSetting = null;
				}
				else if (selectedModule != null && !closingSettings) {
					startSettingAnimation = 100;
					closingSettings = true;
				}else {
					mc.displayGuiScreen(null);
				}
				
			}
		}
	}
	
	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}
	
	private double getDistanceBetweenPoints(double x1, double y1, double x2, double y2) {
		// a�+b�=c�
		// Math.sqrt gets the square root of the number
		return Math.sqrt(((x2-x1)*(x2-x1))+((y2-y1)*(y2-y1)));
	}
	
	private void setColorForIcon(Color color) {
		GlStateManager.enableBlend();
		GlStateManager.color(((float) SpicyClient.config.hud.colorSettingRed.getValue()),
				((float) SpicyClient.config.hud.colorSettingGreen.getValue()),
				((float) SpicyClient.config.hud.colorSettingBlue.getValue()));
		if (SpicyClient.config.rainbowgui.isEnabled()) {
			GlStateManager.color(((float) color.getRed()) / 255, ((float) color.getGreen()) / 255,
					((float) color.getBlue()) / 255);
		}
	}
	
}
