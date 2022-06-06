package ml.sabotage;

import java.io.File;
import java.util.ArrayList;

import ml.sabotage.config.ConfigSettings;
import ml.sabotage.utils.SabUtils;
import ml.zer0dasho.plumber.utils.Sprink;

public class Validate {
	
	public static boolean validateMaps() {
		
		ConfigSettings config = Main.config;
		
		if(!worldExists(config.hub)) {
			Main.disablePlugin("Hub doesn't exist!");
			return false;
		}

		for(String map : new ArrayList<>(config.maps)) {
			if(!validateMap(map)) 
				config.maps.remove(map);
		}
		
		if(config.maps.size() <= 0) {
			Main.disablePlugin("No maps exist!");
			return false;
		}
		
		return true;
	}

	public static boolean validate() {
		
		ConfigSettings config = Main.config;
		
		if(config == null) {
			Main.disablePlugin("Failed to load config.yml!");
			return false;
		}
		
		return validateMaps();
	}
	
	public static boolean validateMap(String map) {
		
		if(!worldExists(map)) {
			Main.plugin.getServer().broadcastMessage(
					Sprink.color(
							String.format("&cWarning: Map '%s' does not exist.", map)));
			return false;
		}
		
		return true;
	}
	
	private static boolean worldExists(String name) {
		return new File(SabUtils.SOURCE, name).exists();
	}
	
}