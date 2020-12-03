package info.spicyclient.chatCommands.commands;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import info.spicyclient.chatCommands.Command;
import info.spicyclient.files.FileManager;
import info.spicyclient.music.MusicManager;

public class Music extends Command {

	public Music() {
		super("music", "music play/stop/list/shuffle song.mp3", 1);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void commandAction(String message) {
		

		String[] splitMessage = message.split(" ");
		String musicName = "";;
		for (int i = 0; i < splitMessage.length; i++) {
			if (i >= 2) {
				musicName += splitMessage[i] + " ";
			}
		}
		
		if (musicName != "") {
			musicName = musicName.replaceFirst(".music ", "");
			musicName = musicName.substring(0, musicName.length() - 1);
		}
		
		if (splitMessage[1].equalsIgnoreCase("play") && musicName != "") {
			
			MusicManager.getMusicManager().playMp3(new File(FileManager.music + "\\" + musicName).toURI().toString().replaceAll(" ", "%20"));
			
		}
		else if (splitMessage[1].equalsIgnoreCase("stop")) {
			MusicManager.getMusicManager().stopPlaying();
		}
		else if (splitMessage[1].equalsIgnoreCase("shuffle")) {
			
			File[] files = FileManager.music.listFiles();
			
			if (files == null) {
				
				sendPrivateChatMessage("You have 0 mp4 files");
				return;
				
			}
			
			MusicManager.getMusicManager().playMp3(files[new Random().nextInt(files.length)].toURI().toString().replaceAll(" ", "%20"));
		}
		else if (splitMessage[1].equalsIgnoreCase("list")) {
			File[] files = FileManager.music.listFiles();
			
			if (files == null) {
				
				sendPrivateChatMessage("You have 0 mp3 files");
				return;
				
			}
			
			sendPrivateChatMessage("You have " + files.length + " mp3 files");
			
			for (File file : files) {
			    if (file.isFile()) {
			    	
			    	sendPrivateChatMessage(" - " + file.getName());
			    	
			    }
			}
			
		}else {
			incorrectParameters();
		}
		
		
	}
	
	@Override
	public void incorrectParameters() {
		
		sendPrivateChatMessage("Please use .music play/stop/list/shuffle song.mp3");
		
	}
	
}
