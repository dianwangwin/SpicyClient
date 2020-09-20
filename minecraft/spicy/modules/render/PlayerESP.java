package spicy.modules.render;

import java.util.List;
import java.util.stream.Collectors;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.EntityLivingBase;
import spicy.SpicyClient;
import spicy.events.Event;
import spicy.events.listeners.EventKey;
import spicy.events.listeners.EventRenderGUI;
import spicy.events.listeners.EventUpdate;
import spicy.modules.Module;
import spicy.settings.NumberSetting;
import spicy.util.RenderUtils;

public class PlayerESP extends Module {
	
	NumberSetting range = new NumberSetting("Block Range", 64, 16, 512, 16);
	
	public PlayerESP() {
		super("PlayerESP", Keyboard.KEY_NONE, Category.BETA);
		resetSettings();
	}
	
	@Override
	public void resetSettings() {
		this.settings.clear();
		this.addSettings(range);
	}
	
	public void onEnable() {
		
	}
	
	public void onDisable() {
		
	}
	
	
	public void onEvent(Event e) {
		
		if (e instanceof EventUpdate) {
			
			List<EntityLivingBase> targets = (List<EntityLivingBase>) mc.theWorld.loadedEntityList.stream().filter(EntityLivingBase.class::isInstance).collect(Collectors.toList());
			
			List<EntityLivingBase> targetsToRemove = (List<EntityLivingBase>) mc.theWorld.loadedEntityList.stream().filter(EntityLivingBase.class::isInstance).collect(Collectors.toList());
			targetsToRemove.clear();
			
			if (!targets.isEmpty()) {
				
				for (EntityLivingBase a : targets) {
					if (a.getDistanceToEntity(mc.thePlayer) > range.getValue()) {
						targetsToRemove.add(a);
					}
				}
				
				for (EntityLivingBase t : targets) {
					
					t.setAlwaysRenderNameTag(true);
					
				}
				
			}
		}
		
	}
	
}
