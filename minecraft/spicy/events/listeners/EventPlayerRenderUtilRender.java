package spicy.events.listeners;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import spicy.events.Event;

public class EventPlayerRenderUtilRender extends Event<EventPlayerRenderUtilRender> {
	
	public ModelBiped playerModel;
	public Entity player;
	public float limbSwing, limbSwingAmount, partialTicks, ageInTicks, headYaw, headPitch, scale;
	
	public EventPlayerRenderUtilRender(ModelBiped playerModel, Entity player, float limbSwing, float limbSwingAmount, float ageInTicks, float headYaw, float headPitch, float scale) {
		this.playerModel = playerModel;
		this.player = player;
		this.limbSwing = limbSwing;
		this.limbSwingAmount = limbSwingAmount;
		this.ageInTicks = ageInTicks;
		this.headYaw = headYaw;
		this.headPitch = headPitch;
		this.scale = scale;
	}

	public ModelBiped getPlayerModel() {
		return playerModel;
	}

	public void setPlayerModel(ModelBiped playerModel) {
		this.playerModel = playerModel;
	}

	public Entity getPlayer() {
		return player;
	}

	public void setPlayer(Entity player) {
		this.player = player;
	}

	public float getLimbSwing() {
		return limbSwing;
	}

	public void setLimbSwing(float limbSwing) {
		this.limbSwing = limbSwing;
	}

	public float getLimbSwingAmount() {
		return limbSwingAmount;
	}

	public void setLimbSwingAmount(float limbSwingAmount) {
		this.limbSwingAmount = limbSwingAmount;
	}

	public float getPartialTicks() {
		return partialTicks;
	}

	public void setPartialTicks(float partialTicks) {
		this.partialTicks = partialTicks;
	}

	public float getAgeInTicks() {
		return ageInTicks;
	}

	public void setAgeInTicks(float ageInTicks) {
		this.ageInTicks = ageInTicks;
	}

	public float getHeadYaw() {
		return headYaw;
	}

	public void setHeadYaw(float headYaw) {
		this.headYaw = headYaw;
	}

	public float getHeadPitch() {
		return headPitch;
	}

	public void setHeadPitch(float headPitch) {
		this.headPitch = headPitch;
	}

	public float getScale() {
		return scale;
	}

	public void setScale(float scale) {
		this.scale = scale;
	}
	
}
