package info.spicyclient.modules.movement;

import java.util.Random;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import info.spicyclient.events.Event;
import info.spicyclient.events.listeners.EventGetBlockReach;
import info.spicyclient.events.listeners.EventRender3D;
import info.spicyclient.events.listeners.EventSendPacket;
import info.spicyclient.events.listeners.EventUpdate;
import info.spicyclient.modules.Module;
import info.spicyclient.util.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;

public class HypixelClickTeleport extends Module {

	public HypixelClickTeleport() {
		super("HypixelClickTeleport", Keyboard.KEY_NONE, Category.MOVEMENT);
	}
	
	public static transient BlockPos lockPos = null;
	
	@Override
	public void onEnable() {
		lockPos = null;
	}
	
	@Override
	public void onEvent(Event e) {
		
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

				for (short i = 0; i < 5; i++) {

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
		
		if (e instanceof EventGetBlockReach) {

			if (mc.thePlayer.capabilities.isCreativeMode)
				return;

			EventGetBlockReach event = (EventGetBlockReach) e;

			event.setCanceled(true);
			event.reach = 200f;

		}
		
		if (e instanceof EventUpdate && e.isPre() || e.isPost()) {

			this.additionalInformation = "Hypixel";
			if (mc.objectMouseOver.typeOfHit == MovingObjectType.BLOCK) {
				lockPos = mc.objectMouseOver.getBlockPos();
			}

			if (mc.thePlayer.capabilities.isCreativeMode)
				return;

			mc.playerController.curBlockDamageMP = 0;

		}
		
		if (e instanceof EventSendPacket && e.isPre()) {

			EventSendPacket event = (EventSendPacket) e;

			if (event.packet instanceof C08PacketPlayerBlockPlacement) {
				e.setCanceled(true);
				try {
					C08PacketPlayerBlockPlacement packet = (C08PacketPlayerBlockPlacement)event.packet;
					
					if (packet.getPosition().getY() + 1 <= mc.thePlayer.posY - 3) {
						
						if (mc.objectMouseOver.typeOfHit == MovingObjectType.BLOCK && mc.objectMouseOver.getBlockPos() != null) {
							packet.position = mc.objectMouseOver.getBlockPos();
						}
						
						double x, y, z;
						
						x = packet.getPosition().getX() + (new Random().nextDouble() - 0.5);
						y = packet.getPosition().getY() + 1;
						z = packet.getPosition().getZ() + (new Random().nextDouble() - 0.5);
						
						mc.thePlayer.setPosition(x, y, z);
						mc.theWorld.setBlockToAir(new BlockPos(x, y, z));
						
					}
					
				} catch (Exception e2) {
					e2.printStackTrace();
				}
				
			}
		}
		
	}
	
}
