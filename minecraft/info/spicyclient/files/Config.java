package info.spicyclient.files;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;

import info.spicyclient.SpicyClient;
import info.spicyclient.chatCommands.Command;
import info.spicyclient.modules.Module;
import info.spicyclient.modules.Module.Category;
import info.spicyclient.modules.beta.TestModuleOne;
import info.spicyclient.modules.combat.*;
import info.spicyclient.modules.community.Speed;
import info.spicyclient.modules.memes.*;
import info.spicyclient.modules.movement.*;
import info.spicyclient.modules.player.*;
import info.spicyclient.modules.render.*;
import info.spicyclient.modules.render.Snow.Snowflake;
import info.spicyclient.modules.world.*;
import info.spicyclient.notifications.NotificationManager;
import info.spicyclient.settings.BooleanSetting;
import info.spicyclient.settings.ModeSetting;
import info.spicyclient.settings.NumberSetting;
import net.minecraft.client.Minecraft;

public class Config {
	
	public String name;
	public String version;
	
	public TabGUI tabgui = new TabGUI();
	public ClickGui clickgui = new ClickGui();
	public Killaura killaura = new Killaura();
	public Fly fly = new Fly();
	public Sprint sprint = new Sprint();
	public Bhop bhop = new Bhop();
	public RainbowGUI rainbowgui = new RainbowGUI();
	public Fullbright fullbright = new Fullbright();
	public NoFall nofall = new NoFall();
	public Keystrokes keystrokes = new Keystrokes();
	public FastPlace fastplace = new FastPlace();
	public Step step = new Step();
	public NoHead noHead = new NoHead();
	public OldHitting oldHitting = new OldHitting();
	public NoSlow noSlow = new NoSlow();
	public OwOifier owoifier = new OwOifier();
	public ChatBypass chatBypass = new ChatBypass();
	public Safewalk safewalk = new Safewalk();
	public BlockFly blockFly = new BlockFly();
	public PlayerESP playerESP = new PlayerESP();
	public AntiVoid antiVoid = new AntiVoid();
	public LongJump longJump = new LongJump();
	public Spider spider = new Spider();
	public AltManager altManager = new AltManager();
	public Timer timer = new Timer();
	public AntiKnockback antiKnockback = new AntiKnockback();
	public Back back = new Back();
	public NoClip noClip = new NoClip();
	public Blink blink = new Blink();
	public AutoClicker autoClicker = new AutoClicker();
	public FastBreak fastBreak = new FastBreak();
	public InventoryManager inventoryManager = new InventoryManager();
	public Tophats tophat = new Tophats();
	public WorldTime worldTime = new WorldTime();
	public ChestStealer chestStealer = new ChestStealer();
	public NoRotate noRotate = new NoRotate();
	public SkyColor skyColor = new SkyColor();
	public Reach reach = new Reach();
	public CsgoSpinbot csgoSpinbot = new CsgoSpinbot();
	public YawAndPitchSpoof yawAndPitchSpoof = new YawAndPitchSpoof();
	public Antibot antibot = new Antibot();
	public PingSpoof pingSpoof = new PingSpoof();
	public KillSults killSults = new KillSults();
	public AutoLog autoLog = new AutoLog();
	public FloofyFoxes floofyFoxes = new FloofyFoxes();
	public Jesus jesus = new Jesus();
	public Phase phase = new Phase();
	public DougDimmadome dougDimmadome = new DougDimmadome();
	public Criticals criticals = new Criticals();
	public Wtap wtap = new Wtap();
	public TriggerBot triggerBot = new TriggerBot();
	public Trail trail = new Trail();
	public ReachNotify reachNotify = new ReachNotify();
	public HideName hideName = new HideName();
	public DiscordRichPresence discordRichPresence = new DiscordRichPresence();
	public AutoArmor autoArmor = new AutoArmor();
	public AntiLava antiLava = new AntiLava();
	public InvWalk invWalk = new InvWalk();
	public Mike mike = new Mike();
	public Disabler disabler = new Disabler();
	public SmallItems smallItems = new SmallItems();
	public LSD lsd = new LSD();
	public Tracers tracers = new Tracers();
	public BlockCoding blockCoding = new BlockCoding();
	public TestModuleOne testModuleOne = new TestModuleOne();
	public Hypixel5SecDisabler hypixel5SecDisabler = new Hypixel5SecDisabler();
	public Hud hud = new Hud();
	public Snow snow = new Snow();
	public TargetStrafe targetStrafe = new TargetStrafe();
	public Eagle eagle = new Eagle();
	public Parkour parkour = new Parkour();
	public Furries furries = new Furries();
	public BlueScreenOfDeathWithChrome blueScreenOfDeath = new BlueScreenOfDeathWithChrome();
	public AutoTool autoTool = new AutoTool();
	public BedBreaker bedBreaker = new BedBreaker();
	public InfinitePlace infinitePlace = new InfinitePlace();
	public DragonWings dragonWings = new DragonWings();
	public EntityDesync entityDesync = new EntityDesync();
	public AntiAntiXray antiAntiXray = new AntiAntiXray();
	public FirstPerson firstPerson = new FirstPerson();
	public Spammer spammer = new Spammer();
	public FpsBooster fpsBooster = new FpsBooster();
	public SuperHeroFX superHeroFX = new SuperHeroFX();
	public NameTags nameTags = new NameTags();
	public JelloForSpicy jelloForSpicy = new JelloForSpicy();
	public Speed speed = new Speed();
	public IrcChat ircChat = new IrcChat();
	public AutoPaperChallenge autoPaperChallenge = new AutoPaperChallenge();
	public HypixelClickTeleport hypixelClickTeleport = new HypixelClickTeleport();
	
	public String clientName = "Spicy ", clientVersion = "B3 Beta";
	
	public static CopyOnWriteArrayList<Module> getModulesForConfig(Config con){
		
		CopyOnWriteArrayList<Module> modules = new CopyOnWriteArrayList<>();
		
		modules.add(con.tabgui);
		modules.add(con.clickgui);
		modules.add(con.killaura);
		modules.add(con.fly);
		modules.add(con.sprint);
		modules.add(con.bhop);
		modules.add(con.rainbowgui);
		modules.add(con.fullbright);
		modules.add(con.nofall);
		modules.add(con.keystrokes);
		modules.add(con.fastplace);
		modules.add(con.step);
		modules.add(con.noHead);
		modules.add(con.oldHitting);
		modules.add(con.noSlow);
		modules.add(con.owoifier);
		modules.add(con.chatBypass);
		modules.add(con.safewalk);
		modules.add(con.blockFly);
		modules.add(con.playerESP);
		modules.add(con.antiVoid);
		modules.add(con.longJump);
		modules.add(con.spider);
		modules.add(con.altManager);
		modules.add(con.timer);
		modules.add(con.antiKnockback);
		modules.add(con.back);
		modules.add(con.noClip);
		modules.add(con.blink);
		modules.add(con.autoClicker);
		modules.add(con.fastBreak);
		modules.add(con.inventoryManager);
		modules.add(con.tophat);
		modules.add(con.worldTime);
		modules.add(con.chestStealer);
		modules.add(con.noRotate);
		modules.add(con.skyColor);
		modules.add(con.reach);
		modules.add(con.csgoSpinbot);
		modules.add(con.yawAndPitchSpoof);
		modules.add(con.antibot);
		modules.add(con.pingSpoof);
		modules.add(con.killSults);
		modules.add(con.autoLog);
		modules.add(con.floofyFoxes);
		modules.add(con.jesus);
		modules.add(con.phase);
		modules.add(con.dougDimmadome);
		modules.add(con.criticals);
		modules.add(con.wtap);
		modules.add(con.triggerBot);
		modules.add(con.trail);
		modules.add(con.reachNotify);
		modules.add(con.hideName);
		modules.add(con.discordRichPresence);
		modules.add(con.autoArmor);
		modules.add(con.antiLava);
		modules.add(con.invWalk);
		modules.add(con.mike);
		modules.add(con.disabler);
		modules.add(con.smallItems);
		modules.add(con.lsd);
		modules.add(con.tracers);
		modules.add(con.blockCoding);
		modules.add(con.testModuleOne);
		modules.add(con.hypixel5SecDisabler);
		modules.add(con.hud);
		modules.add(con.snow);
		modules.add(con.targetStrafe);
		modules.add(con.eagle);
		modules.add(con.parkour);
		modules.add(con.furries);
		modules.add(con.blueScreenOfDeath);
		modules.add(con.autoTool);
		modules.add(con.bedBreaker);
		modules.add(con.infinitePlace);
		modules.add(con.dragonWings);
		modules.add(con.entityDesync);
		modules.add(con.antiAntiXray);
		modules.add(con.firstPerson);
		modules.add(con.spammer);
		modules.add(con.fpsBooster);
		modules.add(con.superHeroFX);
		modules.add(con.nameTags);
		modules.add(con.jelloForSpicy);
		modules.add(con.speed);
		modules.add(con.ircChat);
		modules.add(con.autoPaperChallenge);
		modules.add(con.hypixelClickTeleport);
		
		return modules;
		
	}
	
	public Config(String name) {
		this.name = name;
		this.version = clientVersion;
	}
	
	public void saveConfig() {
		
	}
	
	public boolean updateConfig() {
		
		Config temp = new Config("temp");
		
		if (this.version.equalsIgnoreCase(temp.version)) {
			return false;
		}
		
		Command.sendPrivateChatMessage("Outdated config detected!");
		Command.sendPrivateChatMessage("This config is from the version " + this.version);
		Command.sendPrivateChatMessage("Updating the config to the version " + temp.version + "...");
		
		if (this.version.equalsIgnoreCase("B1") || this.version.equalsIgnoreCase("B2 Beta")) {
			
			Command.sendPrivateChatMessage("Could not load config (Outdated?)");
			return true;
			
		}
		else if (this.version.equalsIgnoreCase("B2")) {
			
			this.noClip = new NoClip();
			this.autoLog = new AutoLog();
			this.floofyFoxes = new FloofyFoxes();
			this.jesus = new Jesus();
			this.phase = new Phase();
			this.dougDimmadome = new DougDimmadome();
			this.playerESP = new PlayerESP();
			this.criticals = new Criticals();
			this.wtap = new Wtap();
			this.triggerBot = new TriggerBot();
			this.chatBypass = new ChatBypass();
			this.trail = new Trail();
			this.reachNotify = new ReachNotify();
			this.oldHitting = new OldHitting();
			this.hideName = new HideName();
			this.killSults = new KillSults();
			this.discordRichPresence = new DiscordRichPresence();
			this.autoArmor = new AutoArmor();
			this.antiLava = new AntiLava();
			this.invWalk = new InvWalk();
			this.mike = new Mike();
			this.disabler = new Disabler();
			this.smallItems = new SmallItems();
			this.lsd = new LSD();
			this.tracers = new Tracers();
			this.blockCoding = new BlockCoding();
			this.testModuleOne = new TestModuleOne();
			this.hypixel5SecDisabler = new Hypixel5SecDisabler();
			this.hud = new Hud();
			this.snow = new Snow();
			this.targetStrafe = new TargetStrafe();
			this.eagle = new Eagle();
			this.parkour = new Parkour();
			this.furries = new Furries();
			this.blueScreenOfDeath = new BlueScreenOfDeathWithChrome();
			this.autoTool = new AutoTool();
			this.bedBreaker = new BedBreaker();
			this.infinitePlace = new InfinitePlace();
			this.dragonWings = new DragonWings();
			this.entityDesync = new EntityDesync();
			this.antiAntiXray = new AntiAntiXray();
			this.firstPerson = new FirstPerson();
			this.spammer = new Spammer();
			this.fpsBooster = new FpsBooster();
			this.superHeroFX = new SuperHeroFX();
			this.nameTags = new NameTags();
			this.jelloForSpicy = new JelloForSpicy();
			this.speed = new Speed();
			this.blockFly = new BlockFly();
			this.ircChat = new IrcChat();
			this.autoPaperChallenge = new AutoPaperChallenge();
			this.hypixelClickTeleport = new HypixelClickTeleport();
			
			this.killaura.dontHitDeadEntitys = new BooleanSetting("Don't hit dead entitys", false);
			this.killaura.newAutoblock = new ModeSetting("Autoblock mode", "None", "None", "Vanilla", "Hypixel");
			this.killaura.newAutoblock.cycle(false);
			this.killaura.newAutoblock.cycle(false);
			this.killaura.targetingMode = new ModeSetting("Targeting mode", "Single", "Single", "Switch");
			this.killaura.switchTime = new NumberSetting("Switch Time", 2, 0.1, 10, 0.1);
			this.killaura.targetsSetting.index = this.killaura.targetModeSetting.index;
			this.killaura.targetsSetting = this.killaura.targetModeSetting;
			this.killaura.rotationSetting = new ModeSetting("Rotation setting", "lock", "lock", "smooth", "Hypixel");
			this.killaura.hitOnHurtTime = new BooleanSetting("Hit on hurt time", false);
			
			this.tabgui.mode = new ModeSetting("Mode", "original", "compressed", "original");
			
			this.nofall.noFallMode = new ModeSetting("NoFall Mode", "Vanilla", "Vanilla", "Packet");
			this.nofall.noFallMode.cycle(false);
			
			this.clickgui.colorSettingRed = new NumberSetting("Red", 255, 0, 255, 1);
			this.clickgui.colorSettingGreen = new NumberSetting("Red", 255, 0, 255, 1);
			this.clickgui.colorSettingBlue = new NumberSetting("Red", 255, 0, 255, 1);
			this.clickgui.clickguiMode = new ModeSetting("ClickGui", "Spicy V2", "Spicy V1", "Spicy V2");
			this.clickgui.padding = new NumberSetting("Padding", 8, 5.5, 10, 0.1);
			
			this.fly.mode = new ModeSetting("Mode", this.fly.mode.getMode(), "Vanilla", "Hypixel", "HypixelFast1");
			this.fly.hypixelFreecamHorizontalFlySpeed = new NumberSetting("Horizontal Speed", 2, 1, 18, 0.2);
			this.fly.hypixelFreecamVerticalFlySpeed = new NumberSetting("Vertical Speed", 0.4, 0.1, 1, 0.01);
			this.fly.viewBobbingSetting = new BooleanSetting("View Bobbing", false);
			this.fly.stopOnDisable = new BooleanSetting("Stop on disable", true);
			this.fly.hypixelUseFireball = new BooleanSetting("Fireball disabler", true);
			this.fly.hypixelUsePearl= new BooleanSetting("Pearl disabler", true);
			this.fly.hypixelPaperChallenge= new BooleanSetting("Paper Challenge disabler", false);
			
			this.bhop.hypixelSpeed = new NumberSetting("Speed", 0.01, 0.0001, 0.03, 0.0001);
			
			this.antiKnockback.horizontalKnockback = new NumberSetting("Horizontal Knockback", 0, 0, 100, 1);
			this.antiKnockback.verticalKnockback = new NumberSetting("Vertical Knockback", 0, 0, 100, 1);
			
			this.killSults.messageMode = new ModeSetting("Message Type", this.killSults.messageMode.getMode(), "Furry", "Retarded Furry", "Annoying", "SpicyClient Ads", "SpicyFacts");
			this.killSults.hypixelShout = new BooleanSetting("/Shout", false);
			
			this.hud.sound = clickgui.sound;
			this.hud.volume = clickgui.volume;
			this.hud.mode = clickgui.mode;
			this.hud.colorSettingRed = clickgui.colorSettingRed;
			this.hud.colorSettingGreen = clickgui.colorSettingGreen;
			this.hud.colorSettingBlue = clickgui.colorSettingBlue;
			this.hud.name = "HUD";
			this.hud.toggled = true;
			
			this.oldHitting.animationSetting.modes.add("Astolfo");
			
			this.antiVoid.jumpFirst = new BooleanSetting("Jump first", false);
			
			this.inventoryManager.category = Category.PLAYER;
			
		}
		
		this.version = temp.version;
		System.out.println(this.clientVersion);
		if (this.clientVersion != "") {
			this.clientVersion = temp.clientVersion;
		}
		
		Command.sendPrivateChatMessage("Config updated :)");
		
		return false;
		
	}
	
	public void loadConfig() {
		
		if (updateConfig()) {
			Command.sendPrivateChatMessage("Config load canceled");
			return;
		}
		
		SpicyClient.currentlyLoadingConfig = true;
		
		double originalX = Minecraft.getMinecraft().thePlayer.posX, originalY = Minecraft.getMinecraft().thePlayer.posY,
				originalZ = Minecraft.getMinecraft().thePlayer.posZ,
				originalMotionX = Minecraft.getMinecraft().thePlayer.motionX,
				originalMotionY = Minecraft.getMinecraft().thePlayer.motionY,
				originalMotionZ = Minecraft.getMinecraft().thePlayer.motionZ;
		
		if (this.clientVersion.toLowerCase().replace(this.version.toLowerCase(), "").length() == 0) {
			Config temp = new Config("temp");
			this.clientName = temp.clientName;
			this.clientVersion = this.version;
		}
		
		SpicyClient.loadConfig(this);
		
		Minecraft.getMinecraft().timer.ticksPerSecond = 20;
		
		for (Module m : SpicyClient.modules) {
			
			if (m instanceof BlueScreenOfDeathWithChrome) {
				if (m.isEnabled()) {
					m.toggle();
				}
			}else {
				m.resetSettings();
				m.toggle();
				m.toggle();
			}
			
		}
		
		SpicyClient.config = this;
		
		NotificationManager.getNotificationManager().notificationQueue.clear();
		
		try {
			FileManager.save_config(this.name);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Minecraft.getMinecraft().thePlayer.setPosition(originalX, originalY, originalZ);
		Minecraft.getMinecraft().thePlayer.motionX = originalMotionX;
		Minecraft.getMinecraft().thePlayer.motionY = originalMotionY;
		Minecraft.getMinecraft().thePlayer.motionZ = originalMotionZ;
		SpicyClient.currentlyLoadingConfig = false;
		
	}
	
}
