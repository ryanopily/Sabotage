package ml.sabotage.game.roles;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import ml.sabotage.Main;
import ml.sabotage.config.ConfigSettings.Karma;
import ml.sabotage.game.SabPlayer;
import ml.zer0dasho.plumber.utils.Sprink;
import ml.zer0dasho.plumber.utils.builders.ItemBuilder;

public class Detective extends IngamePlayer {
	
    public boolean insight;
    public int insight_limit = 3;

    public Detective(SabPlayer sabPlayer) {
        super(sabPlayer);
		player.getInventory().setItem(7, GenerateForceps());
    }
    
    @Override
    public void sendRoleMessage(Detective detective) {
        player.sendMessage(Sprink.color("&6You are the &9&lDetective &r&6this game!"));
        player.sendMessage(Sprink.color("&6Your job is to kill the &cSaboteurs."));
        player.sendMessage(Sprink.color("&6The detective is &9") + player.getName() + ".");
    }

    @Override
    public int determineKarma(IngamePlayer victim) {
    	Karma detective = Main.config.detective;
        if(victim instanceof Innocent) return detective.innocent;
        if(victim instanceof Saboteur) return detective.saboteur;
        
        return 0;
    }
    
	@Override
	public int karmaOnDeath() {
		return Main.config.detective.death;
	}

    @SHOP
    public void Speed_II() {
        if(!hasKarma(40)) return;
        
        if(sabPlayer.player.hasPotionEffect(PotionEffectType.SPEED)) {
        	sabPlayer.player.sendMessage(Sprink.color("&cYou already have speed...!"));
        }else {
           player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 600, 2));
           player.sendMessage(Sprink.color("&aYou just bought Speed II!"));
            sabPlayer.addKarma(-40);
        }
    }

    @SHOP
    public void Insight() {
        if(!hasKarma(60)) return;
        
        if(insight)
        	player.sendMessage(Sprink.color("&cYou already have insight..."));
        else if (insight_limit <= 0)
        	player.sendMessage(Sprink.color("&cYou can't buy any more insight..."));
        else {
        	player.sendMessage(Sprink.color("&aYou just bought Insight!"));
            sabPlayer.addKarma(-60);
        	insight = true;
        	insight_limit--;
        }
    }

    private ItemStack GenerateForceps(){
    	return new ItemBuilder(Material.SHEARS)
    		.displayName("&a&lForceps")
    		.lore("&aRightclick a corpse to check player's role")
    		.create(true);
    }

	@Override
	public String getRole() {
		return Sprink.color("a &9&lDetective");
	}
}

