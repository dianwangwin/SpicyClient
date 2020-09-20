package spicy.settings;

import java.util.Arrays;
import java.util.List;

import spicy.SpicyClient;
import spicy.settings.SettingChangeEvent.type;

public class ModeSetting extends Setting {
	
	public int index;
	public List<String> modes;
	
	public ModeSetting(String name, String defaultMode, String... modes) {
		
		this.name = name;
		this.modes = Arrays.asList(modes);
		index = this.modes.indexOf(defaultMode);
		
	}
	
	public String getMode() {
		
		return this.modes.get(index);
		
	}
	
	public boolean is(String mode) {
		
		return index == modes.indexOf(mode);
		
	}
	
	public void cycle(boolean backwards) {
		
		if (backwards) {
			
			if (index > 0) {
				index--;
			}else {
				index = modes.size() - 1;
			}
			
		}else {
			
			if (index < modes.size() - 1) {
				index++;
			}else {
				index = 0;
			}
			
		}
		
		SettingChangeEvent settingMode = new SettingChangeEvent(type.MODE, getSetting());
		SpicyClient.onSettingChange(settingMode);
		
	}
	
}
