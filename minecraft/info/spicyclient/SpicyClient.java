package info.spicyclient;

import java.awt.Font;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.swing.SwingUtilities;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;

import com.sun.javafx.PlatformUtil;
import com.thealtening.AltService;
import com.thealtening.AltService.EnumAltService;

import info.spicyclient.ClickGUI.NewClickGui;
import info.spicyclient.ClickGUI.Tab;
import info.spicyclient.autoUpdater.Updater;
import info.spicyclient.bypass.Hypixel;
import info.spicyclient.chatCommands.Command;
import info.spicyclient.chatCommands.CommandManager;
import info.spicyclient.events.Event;
import info.spicyclient.events.EventType;
import info.spicyclient.events.listeners.EventChatmessage;
import info.spicyclient.events.listeners.EventKey;
import info.spicyclient.events.listeners.EventReceivePacket;
import info.spicyclient.events.listeners.EventRenderGUI;
import info.spicyclient.events.listeners.EventTick;
import info.spicyclient.events.listeners.EventUpdate;
import info.spicyclient.files.Account;
import info.spicyclient.files.AltInfo;
import info.spicyclient.files.Config;
import info.spicyclient.files.FileManager;
import info.spicyclient.files.Tabs;
import info.spicyclient.hudModules.HudModule;
import info.spicyclient.hudModules.HudModule.HudModuleConfig;
import info.spicyclient.modules.Module;
import info.spicyclient.modules.Module.Category;
import info.spicyclient.modules.player.Timer;
import info.spicyclient.modules.render.*;
import info.spicyclient.music.MusicManager;
import info.spicyclient.networking.NetworkManager;
import info.spicyclient.networking.NetworkUtils;
import info.spicyclient.notifications.Color;
import info.spicyclient.notifications.NotificationManager;
import info.spicyclient.ui.HUD;
import info.spicyclient.ui.fonts.FontUtil;
import info.spicyclient.util.MovementUtils;
import info.spicyclient.util.RandomUtils;
import info.spicyclient.util.RenderUtils;
import info.spicyclient.util.RotationUtils;
import info.spicyclient.util.ServerUtils;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.network.play.server.S45PacketTitle;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Session;

public class SpicyClient {
	
	public static CopyOnWriteArrayList<Module> modules = new CopyOnWriteArrayList<Module>();
	
	public static HUD hud = new HUD();
	
	public static DiscordRP discord;
	
	public static AltInfo altInfo;
	
	public static Config config;
	
	public static AltService TheAltening = new AltService();
	
	public static CommandManager commandManager = new CommandManager();
	
	public static Account account = new Account();
	public static Tabs savedTabs = new Tabs();
	
	public static String originalUsername = "Not Set";
	public static Boolean originalAccountOnline;
	
	// volatile needed so it doesn't get stuck
	public static volatile boolean discordFailedToStart = true, musicPlayerFailedToStart = true;
	
	public static int currentVersionNum = 28, currentBuildNum = 3;
	
	public static boolean currentlyLoadingConfig = false, hasInitViaversion = false;
	
	public static HashMap<String, ResourceLocation> cachedImages = new HashMap<>();
	
	public static boolean bedwarsWarning = false, flyNotice = false;
	
	public static void StartUp() {
		
		try {
			
			if (Minecraft.getMinecraft().getSession().getSessionType().equals(Session.Type.LEGACY)) {
				originalAccountOnline = false;
				originalUsername = Minecraft.getMinecraft().getSession().getUsername();
			}else {
				originalAccountOnline = true;
				originalUsername = Minecraft.getMinecraft().getSession().getUsername();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		new Thread("Music player startup anti crash thread") {
			public void run() {
				try {
					// Does music player stuff
					Media tempMedia = new Media("http://google.com/SpicyClient.mp3");
					MusicManager.getMusicManager();
					musicPlayerFailedToStart = false;
					MusicManager.getMusicManager().mediaPlayer = new MediaPlayer(tempMedia);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();
		
		// Caches images
		
		cachedImages.put("watermarkWhite", new ResourceLocation("spicy/SpicyClientWhite.png"));
		cachedImages.put("watermarkBlack", new ResourceLocation("spicy/SpicyClientBlack.png"));
		cachedImages.put("gearIcon", new ResourceLocation("spicy/clickgui/gear.png"));
		cachedImages.put("dropdownIcon", new ResourceLocation("spicy/clickgui/dropdown.png"));
		cachedImages.put("circleIcon", new ResourceLocation("spicy/clickgui/circle.png"));
		
		for (info.spicyclient.notifications.Type notType : info.spicyclient.notifications.Type.values()) {
			for (info.spicyclient.notifications.Color notColor : info.spicyclient.notifications.Color.values()) {
				
				cachedImages.put("spicy/notifications/" + notType.filePrefix + notColor.fileSuffix + ".png",
						new ResourceLocation(
								"spicy/notifications/" + notType.filePrefix + notColor.fileSuffix + ".png"));
				
			}
		}
		
		for (ResourceLocation resource : cachedImages.values()) {
			String name = resource.getResourcePath();
			System.out.println("Cached " + name);
		}
		
		// Caches images
		
		// Creates a new config with the default values
		config = new Config("Default");
		loadConfig(config);
		
		Display.setTitle(config.clientName + config.clientVersion);
		
		// Used for thealtening api
		try {
			TheAltening.switchService(EnumAltService.MOJANG);
		} catch (NoSuchFieldException | IllegalAccessException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		// Code to load the AltInfo class
		try {
			altInfo = (AltInfo) FileManager.loadAltInfo(altInfo);
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		if (altInfo == null) {
			
			altInfo = new AltInfo();
			altInfo.addAlt("Puro", "You dont have pawmission", false);
			altInfo.addAlt("Puro@alt.com", "You dont have pawmission", true);
			altInfo.addAlt("Puro@example.com", "You dont have pawmission", true);
			altInfo.addAlt("None of them work but this is how they would look in here", "You dont have pawmission", true);
			
			altInfo.alts.get(3).username = "Here are some sample accounts";
			altInfo.alts.get(2).username = "Puro";
			altInfo.alts.get(1).username = "Puro";
			altInfo.alts.get(0).username = "Puro";
			
			try {
				FileManager.saveAltInfo(altInfo);
			} catch (IOException e) {
				
				e.printStackTrace();
			}
		}
		
		// Start the file manager
		FileManager.init();
		
		// Loads the saved account and creates a new file if it doesn't exist
		if (!new File(FileManager.ROOT_DIR, "Account.AccountInfo").exists()) {
			
			System.out.println("Account file not found... Creating a new one");
			try {
				FileManager.saveAccount(SpicyClient.account);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
		}else {
			System.out.println("Account file found... Loading the account data");
		}
		
		try {
			SpicyClient.account = (Account) FileManager.loadAccount(SpicyClient.account);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		// Loads the saved account and creates a new file if it doesn't exist
		
		// Checks if the session is valid
		try {
			
			if (SpicyClient.account.loggedIn) {
				
				JSONObject response = new JSONObject(NetworkManager.getNetworkManager().sendPost(new HttpPost("https://SpicyClient.info/api/V2/SessionLogin.php"), new BasicNameValuePair("session", account.session)));
				
				if (response.getBoolean("error")) {
					
					account.loggedIn = false;
					account.session = "";
					FileManager.saveAccount(account);
					
				}
				
			}
			
		} catch (Exception e) {
			
			account.loggedIn = false;
			account.session = "";
			try {
				FileManager.saveAccount(account);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			e.printStackTrace();
		}
		
		try {
			if (SpicyClient.account.loggedIn) {
				JSONObject response = new JSONObject(NetworkManager.getNetworkManager().sendPost(new HttpPost("https://SpicyClient.info/api/V2/UpdateAlt.php"), new BasicNameValuePair("session", account.session), new BasicNameValuePair("alt", originalUsername)));
			}
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		discord = new DiscordRP();
		
		new Thread("Discord rp startup anti crash thread") {
			public void run() {
				try {
					if (PlatformUtil.isMac()) {
						discordFailedToStart = true;
					}else {
						try {
							// Start the discord rich presence
							discord.start();
							discordFailedToStart = false;
						} catch (Exception e) {
							discordFailedToStart = true;
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();
		
		info.spicyclient.modules.Module.CategoryList = Arrays.asList(Category.values());
		
		// Sets up the clickGui
		float catOffset = Minecraft.getMinecraft().fontRendererObj.FONT_HEIGHT + 7;
		for (Module.Category c : Module.CategoryList) {

			System.out.println("Setting up the " + c.name + " category for the click gui...");
			Tab temp = new Tab();
			temp.setName(c.name);
			temp.setCategory(c);
			temp.setX(30);
			temp.setY(10 + catOffset);
			temp.setOffsetX(0);
			temp.setOffsetY(0);
			savedTabs.tabs.add(temp);
			catOffset += Minecraft.getMinecraft().fontRendererObj.FONT_HEIGHT + 20;
			System.out.println("The " + c.name + " category has been set up");

		}
		
		if (FileManager.canLoadTabs()) {
			try {
				savedTabs = (Tabs) FileManager.loadTabs(savedTabs);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else {
			try {
				FileManager.saveTabs(savedTabs);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		if (FileManager.canLoadHudMods()) {
			try {
		    	HudModule.mods.clear();
				config.hudModConfig = (HudModuleConfig) FileManager.loadHudMods(config.hudModConfig);
				config.hudModConfig.resetFuckingModsListBecauseGoogleFuckingSucksAndTheirLibIsShitAndCannotLoadAFUCKINGClassCorrectlyWithoutFuckingItUpBeondBelief();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else {
			try {
				FileManager.saveHudMods(config.hudModConfig);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		FontUtil.superherofx1.toString();
		FontUtil.superherofx2.toString();
		
	}
	
	public static void shutdown() {
		
		discord.shutdown();
		
		try {
			if (SpicyClient.account.loggedIn) {
				JSONObject response = new JSONObject(NetworkManager.getNetworkManager().sendPost(new HttpPost("https://SpicyClient.info/api/V2/UpdateAlt.php"), new BasicNameValuePair("session", account.session), new BasicNameValuePair("alt", originalUsername)));
			}
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		try {
			FileManager.saveTabs(savedTabs);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static void keypress(int key) {
		
		info.spicyclient.SpicyClient.onEvent(new EventKey(key));
		
		for (Module m : modules) {
			if (m.getKey() == key) {
				m.toggle();
			}
		}
		
	}
	
	public static transient info.spicyclient.util.Timer tabsSaveTimer = new info.spicyclient.util.Timer();
	
	public static void onEvent(Event e) {
		
		if (Minecraft.getMinecraft().thePlayer == null || Minecraft.getMinecraft().theWorld == null) {
			return;
		}
		
		if (currentlyLoadingConfig) {
			e.setCanceled(true);
			return;
		}
		
		if (account.loggedIn) {
			account.onEvent(e);
		}
		
		if (e instanceof EventChatmessage) {
			
			// Will check if the message is a command and if it is a command then will run it
			EventChatmessage chat = (EventChatmessage) e;
			if (chat.message.startsWith(commandManager.prefix)) {
				commandManager.runCommands(chat);
				chat.setCanceled(true);
			}
		}
		
		if (e instanceof EventKey) {
			
			EventKey key = (EventKey) e;
			if (key.key == Keyboard.KEY_PERIOD) {
				Minecraft.getMinecraft().displayGuiScreen(new GuiChat("."));
			}
			
		}
		
		if (e instanceof EventReceivePacket && e.isPre()) {
			
			if (((EventReceivePacket)e).packet instanceof S45PacketTitle) {
				
				try {
					S45PacketTitle packet = ((S45PacketTitle)((EventReceivePacket)e).packet);
					
					ScoreObjective scoreobjective = Minecraft.getMinecraft().theWorld.getScoreboard().getObjectiveInDisplaySlot(1);
					String scoreTitle = scoreobjective.getDisplayName();
					//Command.sendPrivateChatMessage(scoreTitle);
					if (scoreTitle.toLowerCase().contains("bed wars")) {
						if (packet.getMessage().getFormattedText().toLowerCase().contains("respawned")) {
							info.spicyclient.bypass.hypixel.Fly.disabledUntil = System.currentTimeMillis() + 2500;
							NotificationManager.getNotificationManager().createNotification("Bedwars", "You have 2.5 seconds to fly", true, 3000, info.spicyclient.notifications.Type.INFO, Color.PINK);
						}
					}
				} catch (Exception e2) {
					// TODO: handle exception
				}
				
			}
			
		}
		
		if (e instanceof EventReceivePacket && ServerUtils.isOnHypixel()) {
			EventReceivePacket event = (EventReceivePacket)e;
			if (event.packet instanceof S02PacketChat) {
				S02PacketChat chat = (S02PacketChat) event.packet;
				String[] chatParts = chat.getChatComponent().getFormattedText().split(" ");
				System.out.println(chat.getChatComponent().getFormattedText());
				if (chatParts[0].startsWith("§dFrom") && chatParts[1].endsWith(":")) {
					NotificationManager.getNotificationManager().createNotification("Chat message", chat.getChatComponent().getFormattedText() + "  ", false, 15000, info.spicyclient.notifications.Type.INFO, Color.PINK);
				}
				
			}
		}
		
		if (e instanceof EventUpdate && e.isPre()) {
			if (tabsSaveTimer.hasTimeElapsed(5000, true)) {
				new Thread("Saving info files") {
					public void run() {
						try {
							FileManager.saveTabs(savedTabs);
							FileManager.saveHudMods(config.hudModConfig);
						} catch (IOException e) {
							
						}
					}
				}.start();
			}
			
			if (!Minecraft.getMinecraft().gameSettings.ofShowCapes) {
				Minecraft.getMinecraft().gameSettings.ofShowCapes = true;
			}
			
			RenderUtils.resetPlayerYaw();
			RenderUtils.resetPlayerPitch();
			
			try {
				MusicManager.getMusicManager().changeNotificationColor((EventUpdate) e);
			} catch (Exception e2) {
				// TODO: handle exception
			}
			
			if (ServerUtils.isOnHypixel()) {
				
				try {
					
					if (Minecraft.getMinecraft().thePlayer.ticksExisted == 5) {
						bedwarsWarning = false;
						flyNotice = false;
					}
					
					if (!flyNotice && SpicyClient.config.fly.getKey() != Keyboard.KEY_NONE && SpicyClient.config.fly.mode.is("Hypixel")) {
						ScoreObjective scoreobjective = Minecraft.getMinecraft().theWorld.getScoreboard().getObjectiveInDisplaySlot(1);
						String scoreTitle = scoreobjective.getDisplayName();
						//Command.sendPrivateChatMessage(scoreTitle);
						if (scoreTitle.toLowerCase().contains("skywars")) {
							
							//Command.sendPrivateChatMessage(RandomUtils.getTeamName(11, Minecraft.getMinecraft().theWorld.getScoreboard()));
							
							if (RandomUtils.getTeamName(10, Minecraft.getMinecraft().theWorld.getScoreboard()).toLowerCase().contains("next event")) {
								info.spicyclient.bypass.hypixel.Fly.disabledUntil = System.currentTimeMillis() + 2500;
								NotificationManager.getNotificationManager().createNotification("Skywars", "You have 2.5 seconds to fly", true, 3000, info.spicyclient.notifications.Type.INFO, Color.PINK);
								flyNotice = true;
							}
							
						}
					}
					
					if (!bedwarsWarning) {
						ScoreObjective scoreobjective = Minecraft.getMinecraft().theWorld.getScoreboard().getObjectiveInDisplaySlot(1);
						String scoreTitle = scoreobjective.getDisplayName();
						//Command.sendPrivateChatMessage(scoreTitle);
						if (scoreTitle.toLowerCase().contains("bed wars")) {
							
							//Command.sendPrivateChatMessage(RandomUtils.getTeamName(11, Minecraft.getMinecraft().theWorld.getScoreboard()));
							
							if (RandomUtils.getTeamName(11, Minecraft.getMinecraft().theWorld.getScoreboard()).toLowerCase().contains("diamond ii in")) {
								NotificationManager.getNotificationManager().createNotification("Warning", "Flying now may result in a ban", true, 15000, info.spicyclient.notifications.Type.WARNING, Color.RED);
								bedwarsWarning = true;
							}
							
						}
					}
					
				} catch (Exception e2) {
					//e2.printStackTrace();
				}
				
			}
			
		}
		
		for (Module m : modules) {
			
			//if (!m.toggled)
			//	continue;
			
			if (m.toggled || m instanceof Hud) {
				
				boolean sendTwice = false;
				
				if (e.isPre()) {
					e.setType(EventType.BEFOREPRE);
					sendTwice = true;
				}
				else if (e.isPost()) {
					e.setType(EventType.BEFOREPOST);
					sendTwice = true;
				}
				
				if (sendTwice) {
					m.onEvent(e);
				}
				
				if (e.isBeforePre()) {
					e.setType(EventType.PRE);
				}
				else if (e.isBeforePost()) {
					e.setType(EventType.POST);
				}
				
				m.onEvent(e);
				
			}
			else if (!m.toggled || m instanceof Hud) {
				
				boolean sendTwice = false;
				
				if (e.isPre()) {
					e.setType(EventType.BEFOREPRE);
					sendTwice = true;
				}
				else if (e.isPost()) {
					e.setType(EventType.BEFOREPOST);
					sendTwice = true;
				}
				
				if (sendTwice) {
					m.onEventWhenDisabled(e);
				}
				
				if (e.isBeforePre()) {
					e.setType(EventType.PRE);
				}
				else if (e.isBeforePost()) {
					e.setType(EventType.POST);
				}
				
				m.onEventWhenDisabled(e);
				
			}
			
		}
		
	}
	
	public static void onSettingChange(info.spicyclient.settings.SettingChangeEvent e) {
		
		for (Module m : modules) {
			
			m.onSettingChange(e);
			
		}
		
	}
	
	public static void loadConfig(Config c) {
		
		modules.clear();
		modules = Config.getModulesForConfig(c);
		
		int failedToLoad = 0;
		
		for (Module temp : SpicyClient.modules) {
			
			if (temp == null) {
				modules.remove(temp);
				failedToLoad++;
			}else {
				temp.name = temp.name.replaceAll("\\s+","");
			}
			
		}
		
		if (failedToLoad > 0) {
			
			Command.sendPrivateChatMessage("There are missing modules in this config (outdated?), attempting to restore them");
			
			CopyOnWriteArrayList<Module> restoreModules = new CopyOnWriteArrayList<>();
			restoreModules = Config.getModulesForConfig(new Config("Restore Modules"));
			
			for (Module m : modules) {
				for (Module r : restoreModules) {
					if (m.getClass().getName().equals(r.getClass().getName())) {
						restoreModules.remove(r);
					}
				}
			}
			
			for (Module merge : restoreModules) {
				modules.add(merge);
				Command.sendPrivateChatMessage("Successfully restored the " + merge.name + " module");
			}
			
		}
		
		for (Module temp : SpicyClient.modules) {
			if (temp.additionalInformation.equalsIgnoreCase(""
					+ "")) {
				temp.additionalInformation = "";
			}
			
		}
		
		
		for (Module temp : SpicyClient.modules) {
			
			temp.ClickGuiExpanded = false;
			temp.expanded = false;
			temp.index = 0;
			
		}
	}
	
	public static List<Module> getModulesByCategory(Category c){
		
		List<Module> modules = new ArrayList<Module>();
		
		for (Module m : info.spicyclient.SpicyClient.modules) {
			
			if (m.category == c) {
				
				modules.add(m);
				
			}
			
		}
		
		return modules;
		
	}
	
	public static void setWindowIcons() {
		
		Class cls = null;
		try {
			cls = Class.forName("info.spicyclient.SpicyClient");
		} catch (ClassNotFoundException e3) {
			e3.printStackTrace();
			return;
		}
		
		/*
		
		From the Minecraft source code
		
		inputstream = this.mcDefaultResourcePack.getInputStreamAssets(new ResourceLocation("icons/icon_16x16.png"));
        inputstream1 = this.mcDefaultResourcePack.getInputStreamAssets(new ResourceLocation("icons/icon_32x32.png"));
        
		 */
		
		System.out.println("Setting the icons...");
		
		// Use a 32 by 32 image
		InputStream inputstream = cls.getResourceAsStream("/assets/minecraft/spicy/icons/SpicyClientLogo32x32.png");
		InputStream inputstream1 = cls.getResourceAsStream("/assets/minecraft/spicy/icons/SpicyClientLogo32x32.png");
		// Use a 32 by 32 image
		
		if (inputstream != null && inputstream1 != null) {
			
			try {
				Display.setIcon(new ByteBuffer[] {Minecraft.getMinecraft().readImageToBuffer(inputstream), Minecraft.getMinecraft().readImageToBuffer(inputstream1)});
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
            IOUtils.closeQuietly(inputstream);
            IOUtils.closeQuietly(inputstream1);
			
			System.out.println("Icons set");
			
		}
		
	}
	
	// This is the big screen that says Mojang on startup
	public static void setMojangSplashScreen() {
		
		System.out.println("Setting the splash screen...");
		// Uses 512x512 images
		
		// Use this to select a random image
		//Minecraft.getMinecraft().mojangLogo = RandomBackgrounds.values()[new Random().nextInt(RandomBackgrounds.values().length)].image;
		
		Minecraft.getMinecraft().mojangLogo = RandomBackgrounds.SPICYCLIENT.image;
		
		int lowChance = new Random().nextInt(1000);
		
		if (lowChance == 1) {
			
			Minecraft.getMinecraft().mojangLogo = RandomBackgrounds.LAVAFLOWGLOW.image;
			
		}
		else if (lowChance == 2) {
			
			Minecraft.getMinecraft().mojangLogo = RandomBackgrounds.FLOOFYFOX1.image;
			
		}
		
		// Uses 512x512 images
		System.out.println("Splash screen set");
		
	}
	
	// This is at the start of the minecraft crash report
	public static void setCrashReportHeader(StringBuilder builder) {
		
		builder.append("---- SpicyClient ----");
		
		builder.append("\n");
		
		try {
			builder.append("Version: " + SpicyClient.currentVersionNum);
		} catch (Exception e) {
			builder.append("Version: ERROR");
		}
		
		builder.append("\n");
		
		try {
			builder.append("Config version: " + SpicyClient.config.version);
		} catch (Exception e) {
			builder.append("Config version: ERROR");
		}
		
		builder.append("\n");
		
		try {
			builder.append("Config display name: " + SpicyClient.config.clientName);
		} catch (Exception e) {
			builder.append("Config display name: ERROR");
		}
		
		builder.append("\n");
		
		try {
			builder.append("Config display version: " + SpicyClient.config.clientVersion);
		} catch (Exception e) {
			builder.append("Config display version: ERROR");
		}
		
		builder.append("\n");
		
		try {
			builder.append("Currently loading config: " + currentlyLoadingConfig);
		} catch (Exception e) {
			builder.append("Currently loading config: ERROR");
		}
		
		builder.append("\n");
		
		try {
			builder.append("Discord rp running: " + discord.running);
		} catch (Exception e) {
			builder.append("Discord rp running: ERROR");
		}
		
		builder.append("\n");
		
		try {
			builder.append("Discord failed to start: " + discordFailedToStart);
		} catch (Exception e) {
			builder.append("Discord failed to start: ERROR");
		}
		
		builder.append("\n");
		
		try {
			builder.append("Music player failed to start: " + musicPlayerFailedToStart);
		} catch (Exception e) {
			builder.append("Music player failed to start: ERROR");
		}
		
		builder.append("\n");
		
		try {
			builder.append("Is using premium account: " + originalAccountOnline);
		} catch (Exception e) {
			builder.append("Is using premium account: ERROR");
		}
		
		builder.append("\n");
		
		try {
			builder.append("Account username: " + originalUsername);
		} catch (Exception e) {
			builder.append("Account username: ERROR");
		}
		
		builder.append("\n");
		
		builder.append("---- SpicyClient ----");
		
	}
	
}
