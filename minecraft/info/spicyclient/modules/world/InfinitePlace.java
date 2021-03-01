package info.spicyclient.modules.world;

import java.util.Random;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import info.spicyclient.chatCommands.Command;
import info.spicyclient.events.Event;
import info.spicyclient.events.listeners.EventGetBlockReach;
import info.spicyclient.events.listeners.EventPacket;
import info.spicyclient.events.listeners.EventRender3D;
import info.spicyclient.events.listeners.EventSendPacket;
import info.spicyclient.events.listeners.EventUpdate;
import info.spicyclient.modules.Module;
import info.spicyclient.notifications.Color;
import info.spicyclient.notifications.NotificationManager;
import info.spicyclient.notifications.Type;
import info.spicyclient.util.MovementUtils;
import info.spicyclient.util.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;

public class InfinitePlace extends Module {

	public InfinitePlace() {
		super("InfinitePlace", Keyboard.KEY_NONE, Category.BETA);
	}

	public static transient BlockPos lockPos = null;
	public static transient boolean clicked = false, watchdog = false, plot = true, watchdogDisabled = false;
	public EnumFacing face = null;

	@Override
	public void onEnable() {
		clicked = false;
		watchdog = false;
		plot = true;
		watchdogDisabled = false;
	}

	@Override
	public void onEvent(Event e) {

		if (e instanceof EventPacket && e.isPre()) {

			if (e.isPre()) {
				
				if (((EventPacket) e).packet instanceof S08PacketPlayerPosLook) {

					if (watchdog) {
						NotificationManager.getNotificationManager().createNotification("Disabler",
								"Watchdog has been disabled for 5 s.", true, 5000, Type.INFO, Color.PINK);
						watchdogDisabled = true;
					}

				}

			}

		}

		if (e instanceof EventRender3D && e.isPre()) {

			if (mc.objectMouseOver.typeOfHit == MovingObjectType.BLOCK && lockPos != null) {
				
				double xPos = ((lockPos.getX() + 0.5)
						+ ((lockPos.getX() + 0.5) - (lockPos.getX() + 0.5)) * mc.timer.renderPartialTicks)
						- mc.getRenderManager().renderPosX;
				double yPos = ((lockPos.getY() + 0.5)
						+ ((lockPos.getY() + 0.5) - (lockPos.getY() + 0.5)) * mc.timer.renderPartialTicks)
						- mc.getRenderManager().renderPosY + 0;
				double zPos = ((lockPos.getZ() + 0.5)
						+ ((lockPos.getZ() + 0.5) - (lockPos.getZ() + 0.5)) * mc.timer.renderPartialTicks)
						- mc.getRenderManager().renderPosZ;

				GL11.glPushMatrix();
				GL11.glLoadIdentity();
				mc.entityRenderer.orientCamera(mc.timer.renderPartialTicks);
				GL11.glEnable(GL11.GL_BLEND);
				GL11.glEnable(GL11.GL_LINE_SMOOTH);
				GL11.glDisable(GL11.GL_DEPTH_TEST);
				GL11.glDisable(GL11.GL_TEXTURE_2D);
				GL11.glBlendFunc(770, 771);
				GL11.glEnable(GL11.GL_BLEND);
				GL11.glLineWidth(2.0f);
				GL11.glBegin(2);
				GL11.glVertex3d(0.0D, 0.0D + Minecraft.getMinecraft().thePlayer.getEyeHeight(), 0.0D);
				GL11.glVertex3d(xPos, yPos, zPos);
				GL11.glEnd();
				GL11.glDisable(GL11.GL_BLEND);
				GL11.glEnable(GL11.GL_TEXTURE_2D);
				GL11.glEnable(GL11.GL_DEPTH_TEST);
				GL11.glDisable(GL11.GL_LINE_SMOOTH);
				GL11.glDisable(GL11.GL_BLEND);
				GL11.glPopMatrix();

				BlockPos below = lockPos;

				for (int i = 0; i < 5; i++) {

					RenderUtils.drawLine(below.getX(), below.getY(), below.getZ(), below.getX() + 1, below.getY(),
							below.getZ());
					RenderUtils.drawLine(below.getX(), below.getY() + 1, below.getZ(), below.getX() + 1,
							below.getY() + 1, below.getZ());
					RenderUtils.drawLine(below.getX(), below.getY(), below.getZ(), below.getX(), below.getY(),
							below.getZ() + 1);
					RenderUtils.drawLine(below.getX(), below.getY() + 1, below.getZ(), below.getX(), below.getY() + 1,
							below.getZ() + 1);
					RenderUtils.drawLine(below.getX(), below.getY(), below.getZ(), below.getX(), below.getY() + 1,
							below.getZ());
					RenderUtils.drawLine(below.getX(), below.getY() + 1, below.getZ(), below.getX(), below.getY() + 1,
							below.getZ());
					RenderUtils.drawLine(below.getX() + 1, below.getY(), below.getZ(), below.getX() + 1,
							below.getY() + 1, below.getZ());
					RenderUtils.drawLine(below.getX() + 1, below.getY() + 1, below.getZ(), below.getX() + 1,
							below.getY() + 1, below.getZ());
					RenderUtils.drawLine(below.getX(), below.getY(), below.getZ() + 1, below.getX(), below.getY() + 1,
							below.getZ() + 1);
					RenderUtils.drawLine(below.getX(), below.getY() + 1, below.getZ() + 1, below.getX(),
							below.getY() + 1, below.getZ() + 1);
					RenderUtils.drawLine(below.getX() + 1, below.getY(), below.getZ() + 1, below.getX(), below.getY(),
							below.getZ() + 1);
					RenderUtils.drawLine(below.getX() + 1, below.getY() + 1, below.getZ() + 1, below.getX(),
							below.getY() + 1, below.getZ() + 1);
					RenderUtils.drawLine(below.getX() + 1, below.getY(), below.getZ() + 1, below.getX() + 1,
							below.getY() + 1, below.getZ() + 1);
					RenderUtils.drawLine(below.getX() + 1, below.getY() + 1, below.getZ(), below.getX() + 1,
							below.getY() + 1, below.getZ() + 1);
					RenderUtils.drawLine(below.getX() + 1, below.getY(), below.getZ(), below.getX() + 1, below.getY(),
							below.getZ() + 1);

				}

				// RenderUtils.drawLine(mc.thePlayer.posX, mc.thePlayer.posY +
				// mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ,
				// mc.objectMouseOver.getBlockPos().getX() + 0.5,
				// mc.objectMouseOver.getBlockPos().getY(),
				// mc.objectMouseOver.getBlockPos().getZ() + 0.5);

			}

		}
		
		if (e instanceof EventUpdate && e.isPre() || e.isPost()) {

			this.additionalInformation = "Hypixel";
			if (mc.objectMouseOver.typeOfHit == MovingObjectType.BLOCK) {

				if (!clicked) {
					lockPos = mc.objectMouseOver.getBlockPos();
				} else {

					if (!watchdog && plot && !watchdogDisabled) {
						if (MovementUtils.isOnGround(0.001) && mc.thePlayer.isCollidedVertically) {
							double x = mc.thePlayer.posX;
							double y = mc.thePlayer.posY;
							double z = mc.thePlayer.posZ;
							mc.thePlayer.motionY = 0.21;
							watchdog = true;
							NotificationManager.getNotificationManager().createNotification("Disabler: Wait 5s.", "",
									true, 5000, Type.INFO, Color.PINK);
							// mc.thePlayer.jump();
							//mc.thePlayer.setPosition(lockPos.getX(), lockPos.getY() + 1, lockPos.getZ());
						}
					}
					else if (watchdog && plot && watchdogDisabled){
						
						sendPacketsForPathAndPlaceBlock();
						
					}else if (!watchdogDisabled && mc.thePlayer.motionY <= 0) {
						mc.thePlayer.motionX = 0;
						mc.thePlayer.motionY = 0;
						mc.thePlayer.motionZ = 0;
						mc.thePlayer.jumpMovementFactor = 0;
						mc.thePlayer.noClip = true;
						mc.thePlayer.onGround = false;
					}

				}

			}

			if (mc.thePlayer.capabilities.isCreativeMode)
				return;

			mc.playerController.curBlockDamageMP = 0;

		}

		if (e instanceof EventSendPacket && e.isPre()) {

			EventSendPacket event = (EventSendPacket) e;

			if (event.packet instanceof C08PacketPlayerBlockPlacement) {
				clicked = true;
				e.setCanceled(true);
			}
			if (event.packet instanceof C03PacketPlayer) {
				if (watchdog && mc.thePlayer.motionY <= 0) {
                    e.setCanceled(true);
                }
			}
		}

		if (e instanceof EventGetBlockReach) {

			if (mc.thePlayer.capabilities.isCreativeMode)
				return;

			EventGetBlockReach event = (EventGetBlockReach) e;

			event.setCanceled(true);
			event.reach = 100f;

		}

	}
	
	public void sendPacketsForPathAndPlaceBlock() {
		
		plot = false;
		Vec3 lastPos1 = new Vec3(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ),
				targetPos = new Vec3(lockPos.getX(), lockPos.getY() + 1, lockPos.getZ());
		
		Double xRange = lastPos1.xCoord - targetPos.xCoord,
				yRange = lastPos1.yCoord - targetPos.yCoord,
				zRange = lastPos1.zCoord - targetPos.zCoord;
		
		Vec3 temp = null;
		
		//mc.getNetHandler().getNetworkManager().sendPacket(new C03PacketPlayer.C06PacketPlayerPosLook(
				//mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch, new Random().nextBoolean()));
		
		for (double i = 0; i < 209; i++) {
			Double currentPosX = lastPos1.xCoord - ((xRange / 209) * i),
					currentPosY = lastPos1.yCoord - ((yRange / 209) * i),
					currentPosZ = lastPos1.zCoord - ((zRange / 209) * i);
			Command.sendPrivateChatMessage(i);
			
			mc.getNetHandler().getNetworkManager().sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(
					currentPosX, currentPosY, currentPosZ, new Random().nextBoolean()));
			temp = new Vec3(currentPosX + 0.5, currentPosY, currentPosZ + 0.5);
		}
		
		if (temp != null) {
			
			mc.thePlayer.setPosition(temp.xCoord, temp.yCoord, temp.zCoord);
			mc.getNetHandler().getNetworkManager().sendPacket(new C08PacketPlayerBlockPlacement(mc.thePlayer.inventory.getCurrentItem()));
			toggle();
			
		}
		
	}
	
    public double getDistance(double x1, double y1, double z1, double x2, double y2, double z2){
        double d0 = x1 - x2;
        double d1 = y1 - y2;
        double d2 = z1 - z2;
        return (double)MathHelper.sqrt_double(d0 * d0 + d1 * d1 + d2 * d2);
    }
	
}
