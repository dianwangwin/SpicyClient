package info.spicyclient.settings;

import info.spicyclient.SpicyClient;
import info.spicyclient.settings.SettingChangeEvent.type;

public class KeybindSetting extends Setting {
	
	public int code;
	public transient boolean ClickGuiSelected = false;
	
	public KeybindSetting(int code) {
		this.code = code;
		this.name = "Keybind";
	}
	
	public KeybindSetting(String name, int code) {
		this.name = name;
		this.code = code;
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
