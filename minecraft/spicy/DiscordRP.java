package spicy;

import net.arikia.dev.drpc.DiscordEventHandlers;
import net.arikia.dev.drpc.DiscordRPC;
import net.arikia.dev.drpc.DiscordRichPresence;
import net.arikia.dev.drpc.DiscordUser;
import net.arikia.dev.drpc.callbacks.ReadyCallback;
import spicy.chatCommands.Command;
import spicy.modules.Module;
import spicy.modules.render.ClickGUI;

public class DiscordRP {
	
	public boolean running = false;
	public String lastLine = "";
	private long created = 0;
	
	public void start() {
		
		this.running = true;
		this.created = System.currentTimeMillis();
		
		DiscordEventHandlers handlers = new DiscordEventHandlers.Builder().setReadyEventHandler(new ReadyCallback() {
			
			public void apply(DiscordUser user) {
				
				
				
			}
			
		}).build();
		
		DiscordRPC.discordInitialize("733832488199389246", handlers, true);
		
		new Thread("Discord RPC Callback") {
			
			@Override
			public void run() {
				
				while(running) {
					DiscordRPC.discordRunCallbacks();
				}
				
			}
			
		}.start();
		
	}
	
	public void shutdown() {
		
		running = false;
		DiscordRPC.discordShutdown();
		
	}
	
	public void refresh() {
		
		update(lastLine);
		
	}
	
	public void update(String secondline) {
		
		lastLine = secondline;
		
		int toggled = 0;
		
		for (Module m : SpicyClient.modules) {
			
			if (m.isEnabled() && !(m instanceof ClickGUI)) {
				toggled++;
			}
			
		}
		
		DiscordRichPresence.Builder b = new DiscordRichPresence.Builder(secondline);
		b.setBigImage("profile4", "Hacking in minecraft with " + SpicyClient.config.clientName + SpicyClient.config.clientVersion);
		
		if (SpicyClient.config.floofyFoxes.isEnabled() || (SpicyClient.config.hideName.isEnabled() && SpicyClient.config.hideName.mode.getMode().toLowerCase().contains("floof"))){
			b.setSmallImage("floofyfox1", "This person is probably a furry...");
		}
		
		b.setDetails(toggled + "/" + (SpicyClient.modules.size() - 2) + " Modules enabled");
		b.setStartTimestamps(created);
		
		DiscordRPC.discordUpdatePresence(b.build());
		
		
	}
	
}
