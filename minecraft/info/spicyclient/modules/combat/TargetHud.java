package info.spicyclient.modules.combat;

import org.lwjgl.input.Keyboard;

import info.spicyclient.SpicyClient;
import info.spicyclient.events.Event;
import info.spicyclient.events.listeners.EventRenderGUI;
import info.spicyclient.events.listeners.EventUpdate;
import info.spicyclient.modules.Module;
import info.spicyclient.settings.ModeSetting;

public class TargetHud extends Module {

	public TargetHud() {
		super("TargetHud", Keyboard.KEY_NONE, Category.COMBAT);
	}
	
	@Override
	public void resetSettings() {
		this.settings.clear();
		this.addSettings(mode);
	}
	
	public ModeSetting mode = new ModeSetting("TargetHud", "SpicyV2", "SpicyV1", "SpicyV2", "Test");
	
	@Override
	public void onEvent(Event e) {
		
		if (e instanceof EventUpdate && e.isPre()) {
			this.additionalInformation = mode.getMode();
		}
		
		if (e instanceof EventRenderGUI && e.isPre() && (SpicyClient.config.killaura.isEnabled() || SpicyClient.config.aimAssist.isEnabled()) && Killaura.target != null) {
			
			if (SpicyClient.config.aimAssist == null) {
				SpicyClient.config.aimAssist = new AimAssist();
				for (Module m : SpicyClient.modules) {
					if (m instanceof AimAssist) {
						SpicyClient.modules.remove(m);
						SpicyClient.modules.add(SpicyClient.config.aimAssist);
						break;
					}
				}
			}
			
			SpicyClient.config.hudModConfig.targetHud1.draw(false);
			
		}
		
	}
	
}
