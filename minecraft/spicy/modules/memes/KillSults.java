package spicy.modules.memes;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.lwjgl.input.Keyboard;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.network.play.client.C02PacketUseEntity.Action;
import spicy.events.Event;
import spicy.events.listeners.EventChatmessage;
import spicy.events.listeners.EventMotion;
import spicy.events.listeners.EventPacket;
import spicy.events.listeners.EventUpdate;
import spicy.modules.Module;
import spicy.settings.BooleanSetting;
import spicy.settings.ModeSetting;
import spicy.settings.SettingChangeEvent;

public class KillSults extends Module {
	
	private ModeSetting messageMode = new ModeSetting("Message Type", "Furry", "Furry", "Retarded Furry", "Annoying", "SpicyClient Ads");
	private ModeSetting serverMode = new ModeSetting("Server Mode", "PvpLands", "PvpLands", "Test");
	
	public BooleanSetting pvplandsPayback = new BooleanSetting("Payback", false);
	
	public KillSults() {
		super("KillSults", Keyboard.KEY_NONE, Category.MEMES);
		setupMessageLists();
		resetSettings();
		this.additionalInformation = messageMode.getMode();
	}
	
	private static ArrayList<String> furryKillsults = new ArrayList<String>();
	private static ArrayList<String> retardedFurryKillsults = new ArrayList<String>();
	private static ArrayList<String> annoyingKillsults = new ArrayList<String>();
	private static ArrayList<String> spicyClientAdsKillsults = new ArrayList<String>();
	
	@Override
	public void resetSettings() {
		this.settings.clear();
		this.addSettings(messageMode, serverMode, pvplandsPayback);
	}
	
	@Override
	public void onSettingChange(SettingChangeEvent e) {
		
		if (e != null && e.setting != null) {
			
			if (e.setting.equals(serverMode)) {
				
				if (serverMode.is("PvpLands")) {
					
					if (!this.settings.contains(pvplandsPayback)) {
						
						settings.add(pvplandsPayback);
						this.settings.sort(Comparator.comparing(s -> s == keycode ? 1 : 0));
						
					}
					
				}
				else if (serverMode.is("Test")) {
					
					if (this.settings.contains(pvplandsPayback)) {
						
						settings.remove(pvplandsPayback);
						this.settings.sort(Comparator.comparing(s -> s == keycode ? 1 : 0));
						
					}
					
				}
				
			}
			
		}
		
	}
	
	public void onEnable() {
		
	}
	
	public void onDisable() {
		
	}
	
	
	public void onEvent(Event e) {
		
		if (e instanceof EventUpdate) {
			
			if (e.isPre()) {
				
				this.additionalInformation = messageMode.getMode();
				
			}
			
		}
		
		if (e instanceof EventPacket) {
			
			if (e.isPre()) {
				
				if (e.isIncoming()) {
					
					EventPacket event = (EventPacket) e;
					
					if (event.packet instanceof S02PacketChat) {
						
						S02PacketChat packet = (S02PacketChat) event.packet;
						
						String playerName = null;
						boolean OwOifier = false;
						boolean sendMessage = false;
						
						ArrayList<String> killsults = new ArrayList<String>();
						
						if (messageMode.getMode().toLowerCase().contains("furry".toLowerCase())) {
							
							OwOifier = true;
							
						}
						
						if (messageMode.is("Furry")) {
							
							killsults = this.furryKillsults;
							
						}
						else if (messageMode.is("Retarded Furry")) {
							
							killsults = this.retardedFurryKillsults;
							
						}
						else if (messageMode.is("Annoying")) {
							
							killsults = this.annoyingKillsults;
							
						}
						else if (messageMode.is("SpicyClient Ads")) {
							
							killsults = this.spicyClientAdsKillsults;
							
						}
						
						if (serverMode.is("PvpLands")) {
							
							sendMessage = true;
							
							if (packet.getChatComponent().getFormattedText().contains("§r§aYou have killed ") && packet.getChatComponent().getFormattedText().contains(" for ")) {
								
								String[] strings = packet.getChatComponent().getFormattedText().split(" ");
								
								playerName = strings[3];
								String moneyBack = strings[5];
								moneyBack = moneyBack.replace("§", "");
								if (pvplandsPayback.enabled) {
									mc.thePlayer.sendChatMessage("/pay " + playerName + " " + moneyBack);
								}
								
							}else {
								return;
							}
							
						}
						
						Random random = new Random();
						
						String message = killsults.get(random.nextInt(killsults.size()));
						
						if (message == null) {
							return;
						}
						message = message.replaceAll("<PlayerName>", playerName);
						
						if (OwOifier) {
							
							//message = message.replace("l", "w").replace("L", "W").replace("r", "w").replace("R", "W").replace("o", "u").replace("O", "U");
							
						}
						
						if (sendMessage) {
							
							mc.thePlayer.sendChatMessage(message);
							
						}
						
					}
					
				}
				
			}
			
		}
		
	}
	
	public void setupMessageLists() {
		
		// Use <PlayerName> in those caps and with the <> to print out the player that you killed
		
		// for the furry killsults list
		furryKillsults.add("<PlayerName> Just got killed by a furry");
		furryKillsults.add("<PlayerName> OwO");
		furryKillsults.add("<PlayerName> UwU");
		furryKillsults.add("<PlayerName> Awoo");
		furryKillsults.add("OwO <PlayerName>");
		furryKillsults.add("UwU <PlayerName>");
		furryKillsults.add("Hello <PlayerName> would you like to OwO with me");
		furryKillsults.add("Hello <PlayerName> would you like to UwU with me");
		furryKillsults.add("Hello <PlayerName> would you like to Awoo with me");
		furryKillsults.add("<PlayerName> Should legalize awoo");
		furryKillsults.add("<PlayerName> Help me legalize awoo");
		furryKillsults.add("<PlayerName> #LegalizeAwoo");
		furryKillsults.add("<PlayerName> is a furry confirmed?!?!?!?!!");
		furryKillsults.add("<PlayerName> should visit http://spicyclient.info/furry1.gif");
		furryKillsults.add("<PlayerName> really likes this meme http://spicyclient.info/furry2.jpg");
		furryKillsults.add("<PlayerName> Should check out http://spicyclient.info/furry3.png");
		furryKillsults.add("<PlayerName> browses furaffinity");
		furryKillsults.add("<PlayerName> joined r/furryirl");
		furryKillsults.add("<PlayerName> Probably watches FurryFoofi (not me) on youtube");
		furryKillsults.add("<PlayerName> Probably watches Majira Strawberry (not me) on youtube");
		furryKillsults.add("<PlayerName> Probably watches BetaEtaDelota (not me) on youtube");
		furryKillsults.add("<PlayerName> Probably watches furry youtubers");
		
		// for the retarded furry killsults list
		// Writing these made me cringe so hard and I refuse to make more
		retardedFurryKillsults.add("It's <PlayerName>s time to E621 and cry");
		retardedFurryKillsults.add("Hey <PlayerName> want to yiff with me?");
		retardedFurryKillsults.add("Hey everyone, did you know that <PlayerName> is a bottom");
		retardedFurryKillsults.add("<PlayerName> is a bottom");
		retardedFurryKillsults.add("<PlayerName> likes horse cock");
		retardedFurryKillsults.add("<PlayerName> browses E621 in their free time");
		retardedFurryKillsults.add("<PlayerName> Did you know that there is no cock like horse cock");
		retardedFurryKillsults.add("<PlayerName> bought a bad dragon dildo");
		retardedFurryKillsults.add("waww x3 nuzzwes pounces on <PlayerName> uwu <PlayerName> so wawm>");
		
		// for the annoying killsults list
		annoyingKillsults.add("<PlayerName> L");
		annoyingKillsults.add("<PlayerName> EZ");
		annoyingKillsults.add("<PlayerName> bad");
		annoyingKillsults.add("<PlayerName> get good noob");
		annoyingKillsults.add("<PlayerName> LLL");
		annoyingKillsults.add("<PlayerName> LLLL");
		annoyingKillsults.add("<PlayerName> uninstall the game");
		annoyingKillsults.add("<PlayerName> noob");
		annoyingKillsults.add("<PlayerName> is bad");
		annoyingKillsults.add("<PlayerName> EZ EZ EZ");
		annoyingKillsults.add("<PlayerName> bad lol");
		annoyingKillsults.add("<PlayerName> git gud");
		
		// for the spicyclient ads killsults list
		spicyClientAdsKillsults.add("<PlayerName> SpicyClient.info is your new home");
		spicyClientAdsKillsults.add("<PlayerName> Download SpicyClient at SpicyClient.info");
		spicyClientAdsKillsults.add("<PlayerName> SpicyClient is the easy way to get kills");
		spicyClientAdsKillsults.add("<PlayerName> Should install SpicyClient");
		spicyClientAdsKillsults.add("<PlayerName> SpicyClient increases your skill");
		spicyClientAdsKillsults.add("<PlayerName> Doesn't use SpicyClient");
		
	}
	
}
