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

    public int getKarma(){
        return config.karma;
    }

    public int getKarmaTotal(){
        return config.lifetime;
    }

    public int getWins(){
        return config.wins;
    }

    public int getLosses(){
        return config.losses;
    }

    public int getKills(){
        return config.kills;
    }

    public int getDeaths(){
        return config.deaths;
    }

    public int getRightKills(){
        return config.correct_kills;
    }

    public int getWrongKills(){
        return config.wrong_kills;
    }

    public int getRightDeaths(){
        return config.correct_deaths;
    }

    public int getWrongDeaths() {
        return config.wrong_deaths;
    }


    public void resetKarma(){
        config.karma = 200;
        config.lifetime = 200;
        updateKarma();
    }

}