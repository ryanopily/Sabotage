package ml.sabotage.config;

import java.io.File;
import java.util.UUID;

import ml.sabotage.Main;
import ml.zer0dasho.plumber.config.Config;
import ml.zer0dasho.plumber.config.DataRW;

public class PlayerData extends Config {
	
	public PlayerData(UUID uuid) {
		super(new File(Main.DATA_FOLDER + "/players/" + uuid.toString()), DataRW.YAMLRW, null);
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