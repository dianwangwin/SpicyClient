package info.spicyclient.util;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import info.spicyclient.SpicyClient;
import info.spicyclient.chatCommands.Command;
import info.spicyclient.files.FileManager;
import info.spicyclient.settings.BooleanSetting;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCarpet;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockLadder;
import net.minecraft.block.BlockSkull;
import net.minecraft.block.BlockSnow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;

public class RandomUtils {
	
	public static void damagePlayer(double damage) {
		
		Minecraft mc = Minecraft.getMinecraft();
		
		if (damage > MathHelper.floor_double(mc.thePlayer.getMaxHealth()))
			damage = MathHelper.floor_double(mc.thePlayer.getMaxHealth());

		double offset = 0.0625;
		if (mc.thePlayer != null && mc.getNetHandler() != null && mc.thePlayer.onGround) {
			for (int i = 0; i <= ((3 + damage) / offset); i++) {
				mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX,
						mc.thePlayer.posY + offset, mc.thePlayer.posZ, false));
				mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX,
						mc.thePlayer.posY, mc.thePlayer.posZ, (i == ((3 + damage) / offset))));
			}
		}
		
	}
	
    public static boolean isPosSolid(BlockPos pos) {
        Block block = Minecraft.getMinecraft().theWorld.getBlockState(pos).getBlock();
        if ((block.getMaterial().isSolid() || !block.isTranslucent() || block instanceof BlockLadder || block instanceof BlockCarpet
                || block instanceof BlockSnow || block instanceof BlockSkull)
                && !block.getMaterial().isLiquid() && !(block instanceof BlockContainer)) {
            return true;
        }
        return false;
    }
    
    public static boolean shouldRenderCapeOnPlayer(AbstractClientPlayer player) {
    	
    	if (player.getName() == Minecraft.getMinecraft().thePlayer.getName()) {
    		
    		if (SpicyClient.config.dragonWings.isEnabled() || !SpicyClient.config.cape.isEnabled()) {
    			return false;
    		}else {
    			return true;
    		}
    		
    	}
    	else if (SpicyClient.account.loggedIn) {
    		
    		if (SpicyClient.account.usernames.containsKey(player.getName())) {
    			return true;
    		}else {
    			return false;
    		}
    		
    	}else {
    		return false;
    	}
    	
    }
    
	public static String getTeamName(int num, Scoreboard board) {
		ScoreObjective objective = board.getObjectiveInDisplaySlot(1);
        Collection collection = board.getSortedScores(objective);
        ArrayList arraylist = Lists.newArrayList(Iterables.filter(collection, new Predicate()
        {
            public static final String __OBFID = "CL_00001958";
            public boolean apply(Score p_apply_1_)
            {
                return p_apply_1_.getPlayerName() != null && !p_apply_1_.getPlayerName().startsWith("#");
            }
            public boolean apply(Object p_apply_1_)
            {
                return this.apply((Score)p_apply_1_);
            }
        }));
        
        try {
			Score score = (Score) arraylist.get(num);
			ScorePlayerTeam scoreplayerteam = board.getPlayersTeam((score).getPlayerName());
			//String s = ScorePlayerTeam.formatPlayerName(scoreplayerteam, ((Score) score).getPlayerName()) + ": " + EnumChatFormatting.RED + ((Score) score).getScorePoints();
			String s = ScorePlayerTeam.formatPlayerName(scoreplayerteam, ((Score) score).getPlayerName());
			return s;
		} catch (Exception e) {
			//e.printStackTrace();
		}
        
        return "ERROR";
        
	}
	
	public static String getFormattedTime() {
		
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm");
		LocalDateTime now = LocalDateTime.now();
		
		String message = dtf.format(now);
		
		if (SpicyClient.config.hud.hour24Time == null) {
			SpicyClient.config.hud.hour24Time = new BooleanSetting("24 Hour Time", false);
			SpicyClient.config.hud.resetSettings();
			try {
				FileManager.save_config(SpicyClient.config.name);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if (!SpicyClient.config.hud.hour24Time.isEnabled()) {
			String[] times = message.split(":");
			
			if (Integer.valueOf(times[0]) >= 12 && Integer.valueOf(times[0]) < 24) {
				message = message.replaceAll("13:", "01:").replaceAll("14:", "02:").replaceAll("15:", "03:").replaceAll("16:", "04:").replaceAll("17:", "05:").replaceAll("18:", "06:").replaceAll("19:", "07:").replaceAll("20:", "08:").replaceAll("21:", "09:").replaceAll("22:", "10:").replaceAll("23:", "11:").replaceAll("24:", "12:");
				message += " PM";
			}
			else if (Integer.valueOf(times[0]) <= 0) {
				message = message.replaceAll("00:", "12:");
				message += " AM";
			}
			else if (Integer.valueOf(times[0]) <= 12) {
				message += " AM";
			}
		}
		
		return message;
		
	}
	
	public static String getFormattedDate() {
		
		DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE;

		String formattedDate = formatter.format(LocalDate.now());
		return formattedDate;
		
	}
	
	public static String getFormattedDateAndTime(long millis) {
		
		String dateAndTime = "";
		Timestamp time = new Timestamp(millis);
		dateAndTime += time.getMonth() + 1;
		dateAndTime += "/" + time.getDate();
		dateAndTime += "/" + (time.getYear() + 1900);
		dateAndTime += " " + (time.getHours());
		dateAndTime += ":" + (time.getMinutes() + 1 <= 9 ? "0" + (time.getMinutes() + 1) : (time.getMinutes() + 1));
		return dateAndTime;
		
	}
	
	// Made by lavaflowglow 4/5/2021 6:58 PM
	public static void connectToServer(String ip, int port, Minecraft mc) {
		ServerData connect = new ServerData("temp", ip + ":" + port, false);
		mc.displayGuiScreen(new GuiConnecting(mc.currentScreen, mc, connect));
	}
	// Made by lavaflowglow 4/5/2021 6:58 PM
	
	// Made by lavaflowglow 4/5/2021 1:30 PM
	public static void setPosAndMotionWithData6d(Data6d posAndMotion) {
		
		Minecraft.getMinecraft().thePlayer.setPosition(posAndMotion.x, posAndMotion.y, posAndMotion.z);
		Minecraft.getMinecraft().thePlayer.motionX = posAndMotion.motionX;
		Minecraft.getMinecraft().thePlayer.motionY = posAndMotion.motionY;
		Minecraft.getMinecraft().thePlayer.motionZ = posAndMotion.motionZ;
		
	}
	// Made by lavaflowglow 4/5/2021 1:30 PM
	
}
