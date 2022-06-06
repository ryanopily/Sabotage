package ml.sabotage.game;

import org.bukkit.entity.Player;

import ml.sabotage.commands.GenericCommands;
import ml.sabotage.config.PlayerData;
import ml.zer0dasho.plumber.config.Config;

public class SabPlayer {
	
    public final Player player;
    public final PlayerData config;

    public SabPlayer(Player player) {
        this.player = player;
        this.config = Config.create(new PlayerData(player.getUniqueId()), PlayerData.class, false);
    }

    public void updateKarma() {
        player.setExp(0.0f);
        player.setLevel(config.karma);
    }

    public void addKarma(int karma) {
    	if(!GenericCommands.TEST) {
            if(karma > 0)
	        	config.lifetime += karma;
	        config.karma += karma;
	        config.save();
    	}
        
        updateKarma();
    }
}