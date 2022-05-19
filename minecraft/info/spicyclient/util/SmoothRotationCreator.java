package info.spicyclient.util;

import com.sun.javafx.geom.Vec3d;

import info.spicyclient.chatCommands.Command;
import net.minecraft.client.Minecraft;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;

public class SmoothRotationCreator {
	
	public static Minecraft mc = Minecraft.getMinecraft();
	
	private rotationType type;
	public int ticks, originalTicks;
	
	public Vec3d lockPos;
	public float targetYaw, targetPitch, yaw, pitch;
	
	public SmoothRotationCreator(Vec3d pos, int ticks) {
		type = rotationType.lock;
		this.ticks = ticks;
		originalTicks = ticks;
		lockPos = pos;
		yaw = mc.thePlayer.rotationYaw;
		pitch = mc.thePlayer.rotationPitch;
	}
	
	public SmoothRotationCreator(float yaw, float pitch, int ticks) {
		type = rotationType.target;
		this.ticks = ticks;
		originalTicks = ticks;
		targetYaw = yaw;
		targetPitch = pitch;
		yaw = mc.thePlayer.rotationYaw;
		pitch = mc.thePlayer.rotationPitch;
	}
	
	public SmoothRotationCreator(Vec3d pos, int ticks, float currentYaw, float currentPitch) {
		type = rotationType.lock;
		this.ticks = ticks;
		originalTicks = ticks;
		lockPos = pos;
		yaw = currentYaw;
		pitch = currentPitch;
	}
	
	public SmoothRotationCreator(float yaw, float pitch, int ticks, float currentYaw, float currentPitch) {
		type = rotationType.target;
		this.ticks = ticks;
		originalTicks = ticks;
		targetYaw = yaw;
		targetPitch = pitch;
		yaw = currentYaw;
		pitch = currentPitch;
	}
	
	public void setRotation(Vec3d pos, int ticks) {
		type = rotationType.lock;
		this.ticks = ticks;
		originalTicks = ticks;
		lockPos = pos;
		yaw = mc.thePlayer.rotationYaw;
		pitch = mc.thePlayer.rotationPitch;
	}
	
	public void setRotation(float yaw, float pitch, int ticks) {
		type = rotationType.target;
		this.ticks = ticks;
		originalTicks = ticks;
		targetYaw = yaw;
		targetPitch = pitch;
		yaw = mc.thePlayer.rotationYaw;
		pitch = mc.thePlayer.rotationPitch;
	}
	
	public void setRotation(Vec3d pos, int ticks, float currentYaw, float currentPitch) {
		type = rotationType.lock;
		this.ticks = ticks;
		originalTicks = ticks;
		lockPos = pos;
		yaw = currentYaw;
		pitch = currentPitch;
	}
	
	public void setRotation(float yaw, float pitch, int ticks, float currentYaw, float currentPitch) {
		type = rotationType.target;
		this.ticks = ticks;
		originalTicks = ticks;
		targetYaw = yaw;
		targetPitch = pitch;
		yaw = currentYaw;
		pitch = currentPitch;
	}
	
	public float[] onTick() {
		
		if (ticks <= 0) {
			return new float[] {yaw, pitch};
		}
		
		ticks--;
		
		if (type == rotationType.lock) {
			
			float[] lockRots = RotationUtils.getRotationFromPosition(lockPos.x, lockPos.y + 1.2, lockPos.z);
			targetYaw = lockRots[0];
			targetPitch = lockRots[1];
			Command.sendPrivateChatMessage(targetYaw + " " + targetPitch);
			
		}
		
		yaw = updateRotation(yaw, targetYaw, targetYaw / (float)originalTicks);
		pitch = updateRotation(pitch, targetPitch, targetPitch / (float)originalTicks);
		
		return new float[] {yaw, pitch};
		
	}
	
	public boolean isdone() {
		return ticks <= 0;
	}
	
	private static float updateRotation(float current, float intended, float factor) {
		float var4 = MathHelper.wrapAngleTo180_float(intended - current);

		if (var4 > factor) {
			var4 = factor;
		}

		if (var4 < -factor) {
			var4 = -factor;
		}

		return current + var4;
	}
	
	private static enum rotationType{
		lock,
		target;
	}
	
}
