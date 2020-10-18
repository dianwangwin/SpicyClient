package spicy.modules.render;

import java.util.List;
import java.util.stream.Collectors;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.Color;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;
import spicy.SpicyClient;
import spicy.events.Event;
import spicy.events.listeners.EventKey;
import spicy.events.listeners.EventPlayerRender;
import spicy.events.listeners.EventRenderGUI;
import spicy.events.listeners.EventUpdate;
import spicy.modules.Module;
import spicy.settings.NumberSetting;
import spicy.util.RenderUtils;

public class PlayerESP extends Module {
	
	public PlayerESP() {
		super("PlayerESP", Keyboard.KEY_NONE, Category.RENDER);
		
	}
	
	public void onEnable() {
		
	}
	
	public void onDisable() {
		
	}
	
	public void onEvent(Event e) {
		
		
		if (e instanceof EventPlayerRender) {
			
			if (e.isPre()) {
				
				GlStateManager.disableDepth();
				
				EventPlayerRender event = (EventPlayerRender) e;
				
				AbstractClientPlayer player = event.entity;
				//RenderUtils.renderAxisAlignedBB(player.boundingBox);
				RenderUtils.blockESPBox(player.posX, player.posY, player.posZ, player);
				
				GlStateManager.enableDepth();
				
			}
			
			if (e.isPost()) {
				
				//GlStateManager.enableDepth();
				
			}
			
		}
		
		
	}
	
}
