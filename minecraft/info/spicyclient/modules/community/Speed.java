package info.spicyclient.modules.community;

import java.awt.Color;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import org.lwjgl.input.Keyboard;

import com.google.common.collect.Maps;

import info.spicyclient.events.Event;
import info.spicyclient.events.listeners.EventMotion;
import info.spicyclient.events.listeners.EventMove;
import info.spicyclient.events.listeners.EventTick;
import info.spicyclient.events.listeners.EventUpdate;
import info.spicyclient.modules.Module.Category;
import info.spicyclient.settings.BooleanSetting;
import info.spicyclient.settings.ModeSetting;
import info.spicyclient.settings.NumberSetting;
import info.spicyclient.util.MovementUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockBarrier;
import net.minecraft.block.BlockGlass;
import net.minecraft.block.BlockIce;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockPackedIce;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.BlockStainedGlass;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0CPacketBoatInput;
import net.minecraft.network.play.client.C18PacketSpectate;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovementInput;
import net.minecraft.util.Timer;
import net.minecraft.world.World;

public class Speed extends info.spicyclient.modules.Module {
	
	public ModeSetting watermark = new ModeSetting("Created by", "Alliance#2186", "Alliance#2186");
	public ModeSetting mode = new ModeSetting("Mode", "Hypixel", "Hypixel", "Phant0m", "LowHop");
	public BooleanSetting damageBoost = new BooleanSetting("Damage Boost", true);
	public NumberSetting speed = new NumberSetting("Speed", 2.0, 1.0, 2.4, 0.01),
			timerSetting = new NumberSetting("Timer", 1.0, 1.0, 1.4, 0.01),
			boostSpeed = new NumberSetting("Boost", 2.0, 1.0, 2.4, 0.01),
			phantomSpeed = new NumberSetting("PhantomSpeed", 0.15, 0.0, 0.5, 0.01);
			
	
	private int stage;
	private double movementSpeed;
	private double distance;
	private info.spicyclient.util.Timer timer = new info.spicyclient.util.Timer();
	private int state3;
	private int state4;

	public Speed() {
		super("Speed", Keyboard.KEY_NONE, Category.COMMUNITY);
		// What the fuck type of base did he use to make this?
		//this.setColor(new Color(99, 248, 91).getRGB());
		resetSettings();
	}
	
	@Override
	public void resetSettings() {
		this.settings.clear();
		this.addSettings(watermark, mode, damageBoost, speed, timerSetting, boostSpeed, phantomSpeed);
	}
	
	public void onEnable() {

		movementSpeed = 0.0;
		state3 = 0;
		state4 = 0;
		this.distance = 0.0;
		stage = 0;

	}

	@Override
	public void onDisable() {
		// mc.timer.ticksPerSecond = 20;
		this.mc.timer.timerSpeed = 1.0f;
	}
	
	@Override
	public void onEvent(Event e) {
		
		if (e instanceof EventUpdate) {
			if (e.isPost()) {
				final double xDist = mc.thePlayer.posX - mc.thePlayer.prevPosX;
				final double zDist = mc.thePlayer.posZ - mc.thePlayer.prevPosZ;
				this.distance = Math.sqrt(xDist * xDist + zDist * zDist);
				this.additionalInformation = this.mode.getMode();
			}
			else if (e.isPre() && mode.is("Hypixel")) {
				if (mc.thePlayer.isInWater()) {
					return;
				}
				if (MovementUtils.isOnGround(0.001) && mc.thePlayer.onGround && MovementUtils.isMoving()) {
					EventUpdate update = ((EventUpdate)e);
					update.setY(mc.thePlayer.posY + (double) ThreadLocalRandom.current().nextFloat() / 1000.0);
					((EventUpdate) e).setOnGround(true);
				}
			}
			
		}
		
		if (e instanceof EventMotion) {
			if (e.isPost()) {
				EventMotion motion = ((EventMotion)e);
				yaw = motion.getYaw();
				if (mode.is("Phant0m")) {
					motion.setX(motion.getX() + motion.getX() * phantomSpeed.getValue());
					motion.setZ(motion.getZ() + motion.getZ() * phantomSpeed.getValue());
				}
			}
		}
		
		if (e instanceof EventMove && e.isPre()) {
			EventMove motion = ((EventMove)e);
			motion.setX(motion.getX() + motion.getX() * phantomSpeed.getValue());
			motion.setZ(motion.getZ() + motion.getZ() * phantomSpeed.getValue());
		}
		
		if (e instanceof EventTick && e.isPre()) {
			if (mode.is("Hypixel")) {
				if (this.stage == 2) {
					if (MovementUtils.isOnGround(0.01) && MovementUtils.isMoving()) {
						this.mc.thePlayer.motionY = 0.419999999812688697815;
						//this.mc.thePlayer.motionY = MovementUtils.getJumpBoostModifier(0.419999999812688697815);
					}
				}
			}
		}
		
		if (e instanceof EventMove && e.isPre()) {
			if (mode.is("Hypixel")) {
				if (this.canZoom() && this.stage == 1) {
					this.movementSpeed = 1.46 * MovementUtils.getBaseMoveSpeed() - 0.01;
				} else if (this.canZoom() && this.stage == 2) {
					this.mc.timer.timerSpeed = (float) timerSetting.getValue();
					this.movementSpeed = speed.getValue() * MovementUtils.getBaseMoveSpeed() - 0.01;
				} else if (this.stage == 3) {
					double difference = 0.66 * (this.distance - MovementUtils.getBaseMoveSpeed()) - 1.0E-5;
					this.movementSpeed = this.distance - difference;
				} else {
					if (MovementUtils.isOnGround(-mc.thePlayer.motionY)
							|| mc.thePlayer.isCollidedVertically && mc.thePlayer.onGround)
						stage = 1;
					this.movementSpeed = this.distance - this.distance / 99.0;
					this.mc.timer.timerSpeed = 1.00f;
				}
				if (!mc.thePlayer.canRenderOnFire()) {
					state3 = 0;
				}
				if (mc.thePlayer.hurtTime >= 9 && this.movementSpeed < 0.6 && damageBoost.isEnabled()
						&& state3 == 0) {
					this.movementSpeed = MovementUtils.getBaseMoveSpeed() * boostSpeed.getValue();
					state4 = 1;
				}
				this.movementSpeed = Math.max(this.movementSpeed, MovementUtils.getBaseMoveSpeed());
				((EventMove)e).setSpeed(this.movementSpeed);
				if (MovementUtils.isMoving()) {
					++this.stage;
				}
			}
		}
		
	}
	
	float yaw;
	
	private boolean canZoom() {
		if (MovementUtils.isMoving() && this.mc.thePlayer.onGround) {
			return true;
		}
		return false;
	}
	
}