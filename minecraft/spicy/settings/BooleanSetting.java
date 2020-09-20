package spicy.settings;

import spicy.SpicyClient;
import spicy.settings.SettingChangeEvent.type;

public class BooleanSetting extends Setting {
	
	public boolean enabled;

	public BooleanSetting(String name, boolean enabled) {
		
		this.name = name;
		this.enabled = enabled;
		
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		
		this.enabled = enabled;
		
		SettingChangeEvent settingBoolean = new SettingChangeEvent(type.BOOLEAN, getSetting());
		SpicyClient.onSettingChange(settingBoolean);
		
	}
	
	public void toggle() {
		
		enabled = !enabled;
		
		SettingChangeEvent settingBoolean = new SettingChangeEvent(type.BOOLEAN, getSetting());
		SpicyClient.onSettingChange(settingBoolean);
		
	}
	
}
