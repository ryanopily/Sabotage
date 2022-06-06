package ml.sabotage.game.roles;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import ml.sabotage.Main;
import ml.sabotage.config.ConfigSettings.Karma;
import ml.sabotage.game.SabPlayer;
import ml.zer0dasho.plumber.utils.Sprink;

public class Saboteur extends IngamePlayer {
	
    public boolean hack, martyr;

    public Saboteur(SabPlayer sabPlayer) {
        super(sabPlayer);
    }

    @Override
    public void sendRoleMessage(Detective detective) {
        player.sendMessage(Sprink.color("&6You are a &c&lSaboteur &r&6 this game!"));
        player.sendMessage(Sprink.color("&6Your job is to kill all the &aInnocent &6players and the &9Detective."));
        player.sendMessage(Sprink.color("&6The detective is &9") + detective.player.getName() + ".");
    }

    @Override
    public int determineKarma(IngamePlayer victim) {
    	Karma karma = Main.config.saboteur;
        if(victim instanceof Innocent) return karma.innocent;
        if(victim instanceof Saboteur) return karma.saboteur;
        if(victim instanceof Detective) return karma.detective;
        
        return 0;
    }
    
	@Override
	public int karmaOnDeath() {
		return Main.config.saboteur.death;
	}
    
    @SHOP
    public void Surprise_Chest() {
        if (hasKarma(30)) {
            player.getInventory().addItem(new ItemStack[]{new ItemStack(Material.CHEST, 1)});
            player.sendMessage(Sprink.color("&aYou just bought a Surprise Chest!"));
            sabPlayer.addKarma(-30);
        }
    }

    @SHOP
    public void Hack_Revelation() {
        if(!hasKarma(50)) return;
        if(hack) 
            player.sendMessage(Sprink.color("&cYou already have hack revelation...!"));
        else {
            hack = true;
            player.sendMessage(Sprink.color("&aYou just bought Hack Revelation!"));
            sabPlayer.addKarma(-50);
        }
    }

    @SHOP
    public void Martyr() {
        if(!hasKarma(50)) return;
        
        if(martyr) 
            player.sendMessage(Sprink.color("&cYou already have martyr...!"));
        else {
            player.sendMessage(Sprink.color("&aYou just bought Martyr!"));
            sabPlayer.addKarma(-50);
            martyr = true;
        }
    }

	@Override
	public String getRole() {
    	return Sprink.color("a &c&lSaboteur");
	}
}

