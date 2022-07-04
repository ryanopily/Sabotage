
package ml.sabotage.game.roles;

import java.util.Set;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.google.common.collect.Sets;

import ml.sabotage.commands.GenericCommands;
import ml.sabotage.config.PlayerData;
import ml.sabotage.game.SabPlayer;
import ml.sabotage.game.tasks.Panic;
import ml.zer0dasho.plumber.utils.Sprink;

public abstract class IngamePlayer {

    public double blood;
    public boolean timeout;

    public final Player player;
    public final SabPlayer sabPlayer;
    public final Set<Panic> panics;

    public IngamePlayer(SabPlayer sabPlayer) {
        this.sabPlayer = sabPlayer;
        this.player = sabPlayer.player;
        this.panics = Sets.newHashSet();
    }

    public String getBloodMessage() {
    	String result = null;
    	
        switch((int)blood) {
        	case 0:
        		result = "&bThis player is clean";
        		break;
        	case 1:
        		result = "&bYou notice a few drops of blood, but nothing to be suspicious about";
        		break;
        	case 2:
        		result = "&bYou notice blood on their sleeve. Maybe you should keep an eye out...";
        		break;
        	case 3:
        		result = "&bThis player is covered in blood - Did they just kill someone?";
        		break;
        	default:
        		result = "&bThey're drenched in blood - How many people have they killed?";
        		break;
        }
        
        return Sprink.color(result);
    }

    public void kill(IngamePlayer victim) {
    	if(victim == null) return;
    	
        int delta = determineKarma(victim);
        this.sabPlayer.addKarma(delta);
        
        PlayerData data = sabPlayer.config;
       
        if(delta < 0) 
        	data.wrong_kills += 1;
        else 
        	data.correct_kills += 1;
        
        data.kills += 1;
        data.save();  
    }

    public void die(IngamePlayer killer) {
    	PlayerData data = sabPlayer.config;
    	
        if(killer != null) {
            int delta = killer.determineKarma(this);
            
            if(delta < 0) 
            	data.wrong_deaths += 1;
            else 
            	data.correct_deaths += 1;
        }
       
        data.deaths += 1;
        sabPlayer.addKarma(karmaOnDeath());
        data.save();
    }
    
    public abstract String getRole();
    public abstract void sendRoleMessage(Detective detective);

    public abstract int karmaOnDeath();
    public abstract int determineKarma(IngamePlayer ingamePlayer);
    
    public boolean hasKarma(int karma) {
    	if(GenericCommands.TEST || sabPlayer.config.karma >= karma) 
    		return true;
    	
        sabPlayer.player.sendMessage(Sprink.color("&cYou don't have enough karma..."));
        return false;
    }
    
    @SHOP
    public void Compass_() {
        if (hasKarma(20)) {
            player.getInventory().addItem(new ItemStack(Material.COMPASS));
            player.sendMessage(Sprink.color("&aYou just bought a Compass!"));
            sabPlayer.addKarma(-20);
        }
    }

    @SHOP
    public void Panic_Kit() {
        if (hasKarma(40)) {
            player.getInventory().addItem(new ItemStack[]{new ItemStack(Material.OAK_LEAVES, 5)});
            player.sendMessage(Sprink.color("&aYou just bought a Panic Kit!"));
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
}
