package info.spicyclient.modules.combat;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.lwjgl.input.Keyboard;

import info.spicyclient.SpicyClient;
import info.spicyclient.chatCommands.commands.Friend;
import info.spicyclient.events.Event;
import info.spicyclient.events.listeners.EventUpdate;
import info.spicyclient.modules.Module;
import info.spicyclient.settings.BooleanSetting;
import info.spicyclient.settings.NumberSetting;
import info.spicyclient.util.RotationUtils;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;

public class AimAssist extends Module {

	public AimAssist() {
		super("AimAssist", Keyboard.KEY_NONE, Category.COMBAT);
		resetSettings();
	}
	
	@Override
	public void resetSettings() {
		this.settings.clear();
		this.addSettings(range, horizontalSpeed, verticalSpeed, targetPlayers, targetAnimals, targetMobs, targetOther);
	}
	
	public NumberSetting horizontalSpeed = new NumberSetting("Horizontal speed", 20, 1, 180, 1),
			verticalSpeed = new NumberSetting("Vertical speed", 0, 0, 180, 1),
			range = new NumberSetting("Range", 3, 1, 6, 0.1);
	
	public BooleanSetting targetPlayers = new BooleanSetting("Target players", true),
			targetAnimals = new BooleanSetting("Target animals", true),
			targetMobs = new BooleanSetting("Target mobs", true),
			targetOther = new BooleanSetting("Target other", true);
	
	@Override
	public void onEvent(Event e) {
		
		if (e instanceof EventUpdate && e.isPre()) {
			
			// Creates a list of potential targets
			List<EntityLivingBase> targets = (List<EntityLivingBase>) mc.theWorld.loadedEntityList.stream().filter(EntityLivingBase.class::isInstance).collect(Collectors.toList());
			targets.sort(Comparator.comparingDouble(entity -> ((EntityLivingBase)entity).getDistanceToEntity(mc.thePlayer)));
			targets = targets.stream().filter(entity -> entity.getDistanceToEntity(mc.thePlayer) < range.getValue() && entity != mc.thePlayer && !entity.isDead && entity.getHealth() > 0).collect(Collectors.toList());
			
			ArrayList<EntityLivingBase> targetsToUse = new ArrayList<>();
			
			if (targets.isEmpty())
				return;
			
			// Filters the targets
			for (EntityLivingBase a : targets) {
				if (a.getDistanceToEntity(mc.thePlayer) <= range.getValue() && !Friend.friends.contains(a.getName().toLowerCase())
						&& (!SpicyClient.config.teams.isEnabled() || !Teams.isOnSameTeam(a))) {
					
					if (a instanceof EntityPlayer) {
						if (targetPlayers.isEnabled()) {
							targetsToUse.add(a);
						}
					}
					else if (a instanceof EntityAnimal) {
						if (targetAnimals.isEnabled()) {
							targetsToUse.add(a);
						}
					}
					else if (a instanceof EntityMob) {
						if (targetMobs.isEnabled()) {
							targetsToUse.add(a);
						}
					}
					else if (targetOther.isEnabled()) {
						targetsToUse.add(a);
					}
					
				}
				
			}
			
			if (targetsToUse.isEmpty())
				return;
			
			// Sets the killaura target so they appear on the targethud
			Killaura.target = targetsToUse.get(0);
			
			// Rotations
			float[] rots = RotationUtils.getRotations(Killaura.target);
			
			// Rotate the player
			if (horizontalSpeed.getValue() > 0) {
				mc.thePlayer.rotationYaw = RotationUtils.updateRotation(mc.thePlayer.rotationYaw, rots[0], (float) horizontalSpeed.getValue());
			}
			
			if (verticalSpeed.getValue() > 0) {
				mc.thePlayer.rotationPitch = RotationUtils.updateRotation(mc.thePlayer.rotationPitch, rots[1], (float) verticalSpeed.getValue());
			}
			
		}
		
	}
	
}
