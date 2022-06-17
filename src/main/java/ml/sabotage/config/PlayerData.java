package ml.sabotage.config;

import java.io.File;
import java.util.UUID;

import ml.sabotage.Main;
import ml.zer0dasho.config.Config;
import ml.zer0dasho.config.format.yaml.YAMLFormat;

public class PlayerData extends Config {
	
	protected PlayerData() {}
	
	public static PlayerData load(UUID uuid) {
		return Config.load(
				PlayerData.class, 
				new File(Main.DATA_FOLDER + "/players/" + uuid.toString()), 
				YAMLFormat.FORMATTER, 
				() -> YAMLFormat.FORMATTER.write(new PlayerData()));
	}
	
    public int karma = 200,
    		   lifetime = 200, 
    		   wins, 
    		   losses, 
    		   sabpasses,
    		   kills, 
    		   correct_kills, 
    		   wrong_kills, 
    		   deaths, 
    		   correct_deaths,
    		   wrong_deaths;
    
    public boolean canBuild = false, autojoin = true;
}