package ml.sabotage.game;

import org.bukkit.entity.Player;

import ml.sabotage.commands.GenericCommands;
import ml.sabotage.config.PlayerData;

public class SabPlayer {
	
    public final Player player;
    public final PlayerData config;

    public SabPlayer(Player player) {
        this.player = player;
        this.config = PlayerData.load(player.getUniqueId());
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