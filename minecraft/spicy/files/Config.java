package spicy.files;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;

import net.minecraft.client.Minecraft;
import spicy.SpicyClient;
import spicy.chatCommands.Command;
import spicy.modules.Module;
import spicy.modules.combat.*;
import spicy.modules.memes.*;
import spicy.modules.movement.*;
import spicy.modules.player.*;
import spicy.modules.render.*;
import spicy.modules.world.*;
import spicy.settings.BooleanSetting;
import spicy.settings.ModeSetting;
import spicy.settings.NumberSetting;

public class Config {
	
	public String name;
	public String version;
	
	public TabGUI tabgui = new TabGUI();
	public ClickGUI clickgui = new ClickGUI();
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
	public ArmorHud armorHud = new ArmorHud();
	public CsgoSpinbot csgoSpinbot = new CsgoSpinbot();
	public YawAndPitchSpoof yawAndPitchSpoof = new YawAndPitchSpoof();
	public Antibot antibot = new Antibot();
	public PingSpoof pingSpoof = new PingSpoof();
	public KillSults killSults = new KillSults();
	public AutoLog autoLog = new AutoLog();
	
	public String clientName = "SpicyClient ", clientVersion = "B3 Beta";
	
	public Config(String name) {
		this.name = name;
		this.version = clientVersion;
	}
	
	public void saveConfig() {
		
	}
	
	public boolean updateConfig() {
		
		this.autoLog = new AutoLog();
		
		Config temp = new Config("temp");
		if (this.version.equalsIgnoreCase(temp.version)) {
			return false;
		}
		
		Command.sendPrivateChatMessage("Outdated config detected!");
		Command.sendPrivateChatMessage("This config is from the version " + this.version);
		Command.sendPrivateChatMessage("Updating the config to the version " + temp.version + "...");
		
		if (this.version.equalsIgnoreCase("B1") || this.version.equalsIgnoreCase("B2 Beta")) {
			
			Command.sendPrivateChatMessage("Legacy configs are not supported, legacy configs are configs from the versions B1 and B2 Beta");
			return true;
			
		}
		else if (this.version.equalsIgnoreCase("B2")) {
			
			this.killSults.pvplandsPayback = new BooleanSetting("Payback", false);
			this.killaura.dontHitDeadEntitys = new BooleanSetting("Don't hit dead entitys", false);
			this.killaura.newAutoblock = new ModeSetting("Autoblock mode", "None", "None", "Vanilla", "Hypixel");
			this.killaura.newAutoblock.cycle(false);
			this.killaura.newAutoblock.cycle(false);
			this.killaura.targetingMode = new ModeSetting("Targeting mode", "Single", "Single", "Switch");
			this.killaura.switchTime = new NumberSetting("Switch Time", 2, 0.1, 10, 0.1);
			
			this.nofall.noFallMode = new ModeSetting("NoFall Mode", "Vanilla", "Vanilla", "Packet");
			this.nofall.noFallMode.cycle(false);
			
			this.noClip = new NoClip();
			this.autoLog = new AutoLog();
			
		}
		
		this.version = temp.version;
		
		Command.sendPrivateChatMessage("Config updated :)");
		
		return false;
		
	}
	
	public void loadConfig() {
		
		if (updateConfig()) {
			Command.sendPrivateChatMessage("Config load canceled");
			return;
		}
		
		SpicyClient.loadConfig(this);
		
		Minecraft.getMinecraft().timer.ticksPerSecond = 20;
		
		for (Module m : SpicyClient.modules) {
			m.resetSettings();
			m.toggle();
			m.toggle();
		}
		
		SpicyClient.config = this;
		
		try {
			FileManager.save_config(this.name);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}
