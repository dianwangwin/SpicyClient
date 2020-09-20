package spicy.settings;

import spicy.SpicyClient;
import spicy.settings.SettingChangeEvent.type;

public class KeybindSetting extends Setting {
	
	public int code;
	public boolean ClickGuiSelected = false;
	
	public KeybindSetting(int code) {
		this.code = code;
		this.name = "Keybind";
	}

	public int getKeycode() {
		return code;
	}

	public void setKeycode(int code) {
		
		this.code = code;
		
		SettingChangeEvent settingKeybind = new SettingChangeEvent(type.KEYBIND, getSetting());
		SpicyClient.onSettingChange(settingKeybind);
		
	}
	
}
