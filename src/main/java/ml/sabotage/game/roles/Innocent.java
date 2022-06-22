package ml.sabotage.game.roles;

import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import ml.sabotage.Main;
import ml.sabotage.config.ConfigSettings.Karma;
import ml.sabotage.game.SabPlayer;
import ml.zer0dasho.plumber.utils.Sprink;

public class Innocent extends IngamePlayer {
	
    public Innocent(SabPlayer sabPlayer) {
        super(sabPlayer);
    }
    
    @Override
    public void sendRoleMessage(Detective detective) {
        player.sendMessage(Sprink.color("&6You are an &a&lInnocent &r&6 this game!"));
        player.sendMessage(Sprink.color("&6Your job is to kill the &cSaboteurs."));
        player.sendMessage(Sprink.color("&6The detective is &9") + detective.player.getName() + ".");
    }

    @Override
    public int determineKarma(IngamePlayer victim) {
    	Karma karma = Main.config.innocent;
        if(victim instanceof Innocent) return karma.innocent;
        if(victim instanceof Saboteur) return karma.saboteur;
        if(victim instanceof Detective) return karma.detective;
        
        return 0;
    }
    
	@Override
	public int karmaOnDeath() {
		return Main.config.innocent.death;
	}

    @SHOP
    public void Speed_II() {
        if(!hasKarma(40)) return;
        
        if(player.hasPotionEffect(PotionEffectType.SPEED))
            player.sendMessage(Sprink.color("&cYou already have speed...!"));
        else {
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 600, 2));
            player.sendMessage(Sprink.color("&aYou just bought Speed II!"));
            sabPlayer.addKarma(-40);
        }
    }
    
    @SHOP
    public void Invisibility() {
        if(!hasKarma(80)) return;
        
        if(player.hasPotionEffect(PotionEffectType.INVISIBILITY))
            player.sendMessage(Sprink.color("&cYou already have invisibility...!"));
        else {
            player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 600, 2));
            player.sendMessage(Sprink.color("&aYou just bought Invisibility!"));
            sabPlayer.addKarma(-80);
        }
    }

    @SHOP
    public void Second_Wind() {
        if(!hasKarma(60)) return;

        double currentHealth = player.getHealth();
        if(currentHealth >= 10) {
            player.sendMessage(Sprink.color("&cYou're health is too high!"));
        }else{
            player.setHealth(Math.random() * 4 + 8);
            player.sendMessage(Sprink.color("&aYou just bought Second Wind!"));
            sabPlayer.addKarma(-60);
        }
    }

	@Override
	public String getRole() {
		return Sprink.color("an &a&lInnocent");
	}
}

