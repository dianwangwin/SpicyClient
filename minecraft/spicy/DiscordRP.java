package spicy;

import net.arikia.dev.drpc.DiscordEventHandlers;
import net.arikia.dev.drpc.DiscordRPC;
import net.arikia.dev.drpc.DiscordRichPresence;
import net.arikia.dev.drpc.DiscordUser;
import net.arikia.dev.drpc.callbacks.ReadyCallback;

public class DiscordRP {
	
	private boolean running = true;
	private long created = 0;
	
	public void start() {
		
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
	
	public void update(String firstline, String secondline) {
		
		DiscordRichPresence.Builder b = new DiscordRichPresence.Builder(secondline);
		b.setBigImage("profile4", "Hacking in minecraft with " + SpicyClient.config.clientName + SpicyClient.config.clientVersion);
		b.setDetails("Client Name: " + firstline);
		b.setStartTimestamps(created);
		
		DiscordRPC.discordUpdatePresence(b.build());
		
		
	}
	
}
