package spicy.modules.render;

import java.awt.Color;
import java.util.List;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import spicy.SpicyClient;
import spicy.events.Event;
import spicy.events.listeners.EventKey;
import spicy.events.listeners.EventRenderGUI;
import spicy.events.listeners.EventUpdate;
import spicy.modules.Module;
import spicy.settings.BooleanSetting;
import spicy.settings.KeybindSetting;
import spicy.settings.ModeSetting;
import spicy.settings.NumberSetting;
import spicy.settings.Setting;

public class TabGUI extends Module {
	
	public transient boolean rainbowEnabled = false;
	public transient double rainbowTimer = 4;
	
	public ModeSetting mode = new ModeSetting("Mode", "compressed", "compressed", "original");
	
	public TabGUI() {
		super("TabGUI", Keyboard.KEY_NONE, Category.RENDER);
		resetSettings();
	}
	
	@Override
	public void resetSettings() {
		this.settings.clear();
		this.addSettings(mode);
	}
	
	public void onEnable() {
		
	}
	
	public void onDisable() {
		
	}
	
	public transient int current_tab, module_index;
	public transient boolean expanded;
	
	public void onEvent(Event e) {
		
		if (mode.is("compressed") || mode.getMode() == "compressed") {
			
			compressedTabGui(e);
			
		}
		else if (mode.is("original") || mode.getMode() == "original") {
			
			originalTabGui(e);
			
		}
		
		if (e instanceof EventKey) {
			
			int key = ((EventKey)e).key;
			
			List<Module> modules = spicy.SpicyClient.getModulesByCategory(Module.Category.values()[current_tab]);
			
			if (expanded && !modules.isEmpty() && modules.get(module_index).expanded) {
				
				Module module = modules.get(module_index);
				
				if (!module.settings.isEmpty() && module.settings.get(module.index).focused && module.settings.get(module.index) instanceof KeybindSetting) {
					
					if (key == Keyboard.KEY_RETURN || key == Keyboard.KEY_LEFT || key == Keyboard.KEY_RIGHT || key == Keyboard.KEY_UP || key == Keyboard.KEY_DOWN) {
						KeybindSetting keybind = (KeybindSetting) module.settings.get(module.index);
						
						keybind.code = Keyboard.KEY_NONE;
						keybind.focused = false;
						return;
						
					}else {
						KeybindSetting keybind = (KeybindSetting) module.settings.get(module.index);
						
						keybind.code = key;
						keybind.focused = false;
					}
					
				}
				
			}
			
			if (key == Keyboard.KEY_UP) {
				
				if (expanded) {
					
					if (expanded && !modules.isEmpty() && modules.get(module_index).expanded) {
						
						Module module = modules.get(module_index);
						
						if (module.settings.get(module.index).focused) {
							
							Setting setting = modules.get(module_index).settings.get(modules.get(module_index).index);
							
							if (setting instanceof BooleanSetting) {
								
								BooleanSetting bool = (BooleanSetting) setting;
								
								bool.toggle();
								
							}
							else if (setting instanceof NumberSetting) {
								
								NumberSetting number = (NumberSetting) setting;
								
								if (number.getValue() + number.increment >= number.getMaximum()) {
									number.setValue(number.getMaximum());
								}else {
									number.increment(true);
								}
								
							}
							else if (setting instanceof ModeSetting) {
								
								ModeSetting mode  = (ModeSetting) setting;
								
								mode.cycle(false);
								
							}
							
						}else {
							
							if (module.index <= 0) {
								
								module.index = module.settings.size() - 1;
							
							}else {
								
								module.index--;
								
							}
							
						}
						
						
					}else {
						
						if (module_index <= 0) {
							module_index = modules.size() - 1;
						}else {
							module_index--;
						}
						
					}
					
				}else {
					if (current_tab <= 0) {
						current_tab = Module.Category.values().length - 1;
					}else {
						current_tab--;
					}
				}
				
			}
			
			if (key == Keyboard.KEY_DOWN) {
				
				if (expanded) {
					
					if (expanded && !modules.isEmpty() && modules.get(module_index).expanded) {
						
						Module module = modules.get(module_index);
						
						if (module.settings.get(module.index).focused) {
							
							Setting setting = modules.get(module_index).settings.get(modules.get(module_index).index);
							
							if (setting instanceof BooleanSetting) {
								
								BooleanSetting bool = (BooleanSetting) setting;
								
								bool.toggle();
								
							}
							else if (setting instanceof NumberSetting) {
								
								NumberSetting number = (NumberSetting) setting;
								
								if (number.getValue() + (number.increment * -1) >= number.getMaximum()) {
									number.setValue(number.getMaximum());
								}else {
									number.increment(false);
								}
								
							}
							else if (setting instanceof ModeSetting) {
								
								ModeSetting mode  = (ModeSetting) setting;
								
								mode.cycle(true);
								
							}
							
						}else {
							if (module.index >= module.settings.size() - 1) {
							
								module.index = 0;
						
							}else {
								
								module.index++;
								
							}
						}
						
						
						
					}else {
						
						if (module_index >= modules.size() - 1) {
						module_index = 0;
					
						}else {
						module_index++;
					
						}
						
					}
					
					
				}else {
					if (current_tab >= Module.Category.values().length - 1) {
						current_tab = 0;
					}else {
						current_tab++;
					}
				}
				
			}
			
			if (key == Keyboard.KEY_LEFT) {
				
				Module module = modules.get(module_index);
				
				if (module.expanded && module.settings.get(module.index).focused) {
					module.settings.get(module.index).focused = false;
				}
				else if (expanded && !modules.isEmpty() && modules.get(module_index).expanded) {
					
					modules.get(module_index).expanded = false;
					
				}
				else {
					expanded = false;
					module_index = 0;
				}
				
			}
			
			if (key == Keyboard.KEY_RIGHT) {
				
				Module module = modules.get(module_index);
				
				if (!module.expanded && expanded) {
					
					module.expanded = true;
					
				}
				else if (module.expanded) {
					module.settings.get(module.index).focused = !module.settings.get(module.index).focused;
				}
				else {
					expanded = true;
				}
				
			}
			
			if (key == Keyboard.KEY_RETURN) {
				
				if (expanded && !modules.isEmpty() && modules.get(module_index).expanded) {
					
					Module module = modules.get(module_index);
					
				}else {
					
					if (expanded && modules.size() != 0) {
						modules.get(module_index).toggle();
					}else {
						expanded = true;
					}
					
				}
				
			}
			
		}
		
	}
	
	private void originalTabGui(Event e) {
		
		if (e instanceof EventRenderGUI) {
			
			if (e.isPre()) {
				
				FontRenderer fr = mc.fontRendererObj;
				
				float hue = System.currentTimeMillis() % (int)(rainbowTimer * 1000) / (float)(rainbowTimer * 1000);
				int primColor = Color.HSBtoRGB(hue, 0.45f, 1);
				int secColor = Color.HSBtoRGB(hue, 0.45f, 0.45f);
				
				int primaryColor = -1;
				int secondaryColor = Color.HSBtoRGB(1, 0, 0.5f);
				
				if (rainbowEnabled) {
					primaryColor = primColor;
					secondaryColor = secColor;
				}
				
				Gui.drawRect(5, 30, 100, 50 + Module.Category.values().length*16, 0x60000000);
				//Gui.drawRect(left, down, right, top, color);
				
				int counter = 0;
				
				for (Category c : Module.Category.values()) {
					
					Gui.drawRect(7, 33 + current_tab*18, 9, 33 + current_tab*18 + 19, primaryColor);
					Gui.drawRect(7, 33 + current_tab*18, 97, 33 + current_tab*18 + 2, primaryColor);
					Gui.drawRect(7, 33 + fr.FONT_HEIGHT + 12 + (current_tab*18), 97, 33 + fr.FONT_HEIGHT + 12 + (current_tab*18) - 2, primaryColor);
					Gui.drawRect(94.8, 33 + fr.FONT_HEIGHT + 12 + (current_tab*18), 97, 33 + fr.FONT_HEIGHT + 12 + (current_tab*18) - 19, primaryColor);
					
					fr.drawStringWithShadow(c.name, 15, 40 + counter*18, primaryColor);
					counter++;
					
				}
				counter = 0;
				
				
				if (expanded) {
					
					List<Module> modules = spicy.SpicyClient.getModulesByCategory(Module.Category.values()[current_tab]);
					
					Gui.drawRect(5 + 100, 30, 150 + 100, 50 + modules.size()*16, 0x60000000);
					//Gui.drawRect(left, down, right, top, color);
					
					for (Module m : modules) {
						
						Gui.drawRect(7 + 100, 33 + module_index*18, 9 + 100, 33 + module_index*18 + 19, primaryColor);
						Gui.drawRect(7 + 100, 33 + module_index*18, 147 + 100, 33 + module_index*18 + 2, primaryColor);
						Gui.drawRect(7 + 100, 33 + fr.FONT_HEIGHT + 12 + (module_index*18), 147 + 100, 33 + fr.FONT_HEIGHT + 12 + (module_index*18) - 2, primaryColor);
						Gui.drawRect(144.8 + 100, 33 + fr.FONT_HEIGHT + 12 + (module_index*18), 147 + 100, 33 + fr.FONT_HEIGHT + 12 + (module_index*18) - 19, primaryColor);
						
						fr.drawStringWithShadow(m.name, 15 + 100, 40 + counter*18, (m.isToggled() ? primaryColor : secondaryColor));
						
						if (counter == module_index && m.expanded) {
							
							int index = 0, maxLength = 150;
							
							for (Setting setting: m.settings) {
								
								if (setting instanceof BooleanSetting) {
									
									BooleanSetting bool = (BooleanSetting) setting;
									
									if (maxLength < fr.getStringWidth(setting.name + ": " + (bool.enabled ? "Enabled" : "Disabled"))) {
										maxLength = fr.getStringWidth(setting.name + ": " + (bool.enabled ? "Enabled" : "Disabled")) + 30;
									}
									
								}
								else if (setting instanceof NumberSetting) {
									
									NumberSetting number = (NumberSetting) setting;
									
									if (maxLength < fr.getStringWidth(setting.name + ": " + Double.toString(number.getValue()))) {
										maxLength = fr.getStringWidth(setting.name + ": " + Double.toString(number.getValue()));
									}
									
								}
								else if (setting instanceof ModeSetting) {
									
									ModeSetting mode  = (ModeSetting) setting;
									
									if (maxLength < fr.getStringWidth(setting.name + ": " + mode.getMode())) {
										maxLength = fr.getStringWidth(setting.name + ": " + mode.getMode());
									}
									
								}
								else if (setting instanceof KeybindSetting) {
									
									KeybindSetting keybind  = (KeybindSetting) setting;
									
									if (maxLength < fr.getStringWidth(setting.name + ": " + Keyboard.getKeyName(keybind.code))) {
										maxLength = fr.getStringWidth(setting.name + ": " + Keyboard.getKeyName(keybind.code));
									}
									
								}
								
								//fr.drawStringWithShadow(setting.name, 15 + 100 + 150, 40 + index*18, primaryColor);
								index++;
								
							}
							
							Gui.drawRect(5 + 100 + 150, 30, 150 + 100 + maxLength, 50 + m.settings.size()*16, 0x60000000);
							
							Gui.drawRect(7 + 100 + 150, 33 + m.index*18, 9 + 100 + 150, 33 + m.index*18 + 19, m.settings.get(m.index).focused ? secondaryColor : primaryColor);
							Gui.drawRect(7 + 100 + 150, 33 + m.index*18, 147 + 100 + maxLength, 33 + m.index*18 + 2, m.settings.get(m.index).focused ? secondaryColor : primaryColor);
							Gui.drawRect(7 + 100 + 150, 33 + fr.FONT_HEIGHT + 12 + (m.index*18), 147 + 100 + maxLength, 33 + fr.FONT_HEIGHT + 12 + (m.index*18) - 2, m.settings.get(m.index).focused ? secondaryColor : primaryColor);
							Gui.drawRect(144.8 + 100 + maxLength, 33 + fr.FONT_HEIGHT + 12 + (m.index*18), 147 + 100 + maxLength, 33 + fr.FONT_HEIGHT + 12 + (m.index*18) - 19, m.settings.get(m.index).focused ? secondaryColor : primaryColor);
							
							index = 0;
							
							for (Setting setting: m.settings) {
								
								if (setting instanceof BooleanSetting) {
									
									BooleanSetting bool = (BooleanSetting) setting;
									fr.drawStringWithShadow(setting.name + ": " + (bool.enabled ? "Enabled" : "Disabled") , 15 + 100 + 150, 40 + index*18, primaryColor);
									
								}
								else if (setting instanceof NumberSetting) {
									
									NumberSetting number = (NumberSetting) setting;
									
									fr.drawStringWithShadow(setting.name + ": " + number.getValue() , 15 + 100 + 150, 40 + index*18, primaryColor);
									
								}
								else if (setting instanceof ModeSetting) {
									
									ModeSetting mode = (ModeSetting) setting;
									
									fr.drawStringWithShadow(setting.name + ": " + mode.getMode() , 15 + 100 + 150, 40 + index*18, primaryColor);
									
								}
								else if (setting instanceof KeybindSetting) {
									
									KeybindSetting keybind  = (KeybindSetting) setting;
									
									fr.drawStringWithShadow(setting.name + ": " + Keyboard.getKeyName(keybind.code) , 15 + 100 + 150, 40 + index*18, primaryColor);
									
								}
								
								//fr.drawStringWithShadow(setting.name, 15 + 100 + 150, 40 + index*18, primaryColor);
								index++;
								
							}
							
						}
						
						counter++;
						
					}
				}
				
			}
			
		}
		
	}
	
	private void compressedTabGui(Event e) {
		
		if (e instanceof EventRenderGUI) {
			
			if (e.isPre()) {
				
				FontRenderer fr = mc.fontRendererObj;
				
				float hue = System.currentTimeMillis() % (int)(rainbowTimer * 1000) / (float)(rainbowTimer * 1000);
				int primColor = Color.HSBtoRGB(hue, 0.45f, 1);
				int secColor = Color.HSBtoRGB(hue, 0.45f, 0.45f);
				
				int primaryColor = -1;
				int secondaryColor = Color.HSBtoRGB(1, 0, 0.5f);
				
				if (rainbowEnabled) {
					primaryColor = primColor;
					secondaryColor = secColor;
				}
				
				int catNum = 0;
				
				float maxCatSize = 0, maxModSize = 0;
				
				for (Category c : CategoryList) {
					
					if (fr.getStringWidth(c.name) > maxCatSize) {
						maxCatSize = fr.getStringWidth(c.name);
					}
					
				}
				
				for (Module m : SpicyClient.modules) {
					
					if (fr.getStringWidth(m.name) > maxModSize) {
						
						maxModSize = fr.getStringWidth(m.name);
						
					}
					
				}
				
				maxCatSize += 5;
				maxModSize += 5;
				
				for (Category c : CategoryList) {
					
					float offset = ((catNum*11) + 30);
					
					//Gui.drawRect(0, 0 + offset, maxCatSize, 20 + offset, 0x90000000);
					Gui.drawRect(0, 0 + offset, maxCatSize, fr.FONT_HEIGHT + 2 + offset, 0x60000000);
					
					catNum++;
					
				}
				
				catNum = 0;
				
				Gui.drawRect(0, 0 + ((current_tab*11) + 30), maxCatSize, fr.FONT_HEIGHT + 2 + ((current_tab*11) + 30), 0x60ffffff);
				
				for (Category c : CategoryList) {
					
					float offset = ((catNum*11) + 30);
					
					fr.drawStringWithShadow(c.name, 2, (int) (2 + offset), primaryColor);
					
					catNum++;
					
				}
				
				catNum = 0;
				
				int moduleNum = 0;
				
				boolean renderedModuleIndex = false;
				
				for (Category c : CategoryList) {
					
					if (expanded && current_tab == CategoryList.indexOf(c)) {
						
						Category cat = c;
						
						for (Module m : SpicyClient.modules) {
							
							float offset = ((moduleNum*11) + 30);
							
							if (m.category == cat) {
								
								Gui.drawRect(maxCatSize, 0 + offset, maxModSize + maxCatSize, fr.FONT_HEIGHT + 2 + offset, 0x60000000);
								
							}
							
							if (m.category == cat && !renderedModuleIndex && moduleNum == module_index) {
								
								Gui.drawRect(maxCatSize, 0 + ((module_index*11) + 30), maxCatSize + maxModSize, fr.FONT_HEIGHT + 2 + ((module_index*11) + 30), 0x60ffffff);
								renderedModuleIndex = true;
								
							}
							
							if (m.category == cat) {
								
								fr.drawStringWithShadow(m.name, (int) (2 + maxCatSize), (int) (2 + offset), m.isToggled() ? primaryColor : secondaryColor);
								
							}
							
							if (m.category == cat) {
								
								if (m.expanded) {
									
									int maxSettingNum = 0;
									float maxSettingSize = 0;
									
									for (Setting s : m.settings) {
										
										String name = " ";
										
										if (s instanceof BooleanSetting) {
											
											BooleanSetting setting = (BooleanSetting) s;
											
											if (setting.isEnabled()) {
												name = s.name + ": Enabled";
											}else {
												name = s.name + ": Disabled";
											}
											
										}
										else if (s instanceof ModeSetting) {
											
											ModeSetting setting = (ModeSetting) s;
											
											name = s.name + ": " + setting.getMode();
											
										}
										else if (s instanceof NumberSetting) {
											
											NumberSetting setting = (NumberSetting) s;
											name = s.name + ": " + setting.getValue();
											
										}
										else if (s instanceof KeybindSetting) {
											
											KeybindSetting setting = (KeybindSetting) s;
											name = s.name + ": " + Keyboard.getKeyName(setting.code);
											
										}
										
										if (fr.getStringWidth(name) > maxSettingSize) {
											maxSettingSize = fr.getStringWidth(name);
										}
										
									}
									
									boolean renderedSelectedSetting = false;
									
									for (Setting s : m.settings) {
										
										float settingOffset = ((maxSettingNum*11) + 30);
										
										Gui.drawRect(maxCatSize + maxModSize, 0 + settingOffset, maxModSize + maxCatSize + maxSettingSize, fr.FONT_HEIGHT + 2 + settingOffset, 0x60000000);
										
										String name = "";
										
										if (s instanceof BooleanSetting) {
											
											BooleanSetting setting = (BooleanSetting) s;
											
											if (setting.isEnabled()) {
												name = s.name + ": Enabled";
											}else {
												name = s.name + ": Disabled";
											}
											
										}
										else if (s instanceof ModeSetting) {
											
											ModeSetting setting = (ModeSetting) s;
											name = s.name + ": " + setting.getMode();
											
										}
										else if (s instanceof NumberSetting) {
											
											NumberSetting setting = (NumberSetting) s;
											name = s.name + ": " + setting.getValue();
											
										}
										else if (s instanceof KeybindSetting) {
											
											KeybindSetting setting = (KeybindSetting) s;
											name = s.name + ": " + Keyboard.getKeyName(setting.code);
											
										}
										
										if (m.settings.indexOf(s) == m.index && !renderedSelectedSetting) {
											
											Gui.drawRect(maxCatSize + maxModSize, 0 + settingOffset, maxModSize + maxCatSize + maxSettingSize, fr.FONT_HEIGHT + 2 + settingOffset, 0x60ffffff);
											renderedSelectedSetting = true;
											
										}
										
										fr.drawStringWithShadow(name, maxCatSize + maxModSize, 2 + settingOffset, s.focused ? secondaryColor : primaryColor);
										maxSettingNum++;
										
									}
									
								}
								
							}
							
							if (m.category == cat) {
								
								moduleNum++;
								
							}
							
						}
						
					}
					
					catNum++;
					
				}
				
			}
			
		}
		
	}
	
}
