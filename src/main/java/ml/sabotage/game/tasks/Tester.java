
package ml.sabotage.game.tasks;

import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import ml.sabotage.Main;
import ml.sabotage.game.events.TesterEvent;
import ml.sabotage.game.managers.MapManager;
import ml.sabotage.game.roles.Detective;
import ml.sabotage.game.roles.IngamePlayer;
import ml.sabotage.game.roles.Saboteur;
import ml.zer0dasho.plumber.game.Timer;

public class Tester extends BukkitRunnable {
	
    public final Timer time, reload;
    public final Set<Location> wool, bars;
    
    private Material color;
    private IngamePlayer tester;

    public Tester(MapManager mapManager, IngamePlayer tester) {
    	this.tester = tester;
        this.wool = mapManager.getLamps();
        this.bars = mapManager.getBars();
        this.reload = new Timer(null, Main.config.tester.reload);
        this.time = Main.config.tester.getTimer();
        
        time.onFinish = () -> {
        	color = getColor(); 
        	tester.player.removePotionEffect(PotionEffectType.SLOW); 
        	Bukkit.getPluginManager().callEvent(new TesterEvent(this));
        };
        
        reload.onFinish = () -> { reload(); cancel(); };
    }

    @Override
    public void run() {
    	if(!time.isFinished()) {
    		test();
    		time.tick();
    	}
    	
    	else if(!reload.isFinished()) {
    		release();
    		reload.tick();
    	}
    }

    private void test() {
        bars.forEach(location -> location.getBlock().setType(Material.IRON_BARS));
        tester.player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 100, 100));
    }

    private void release() {
        setLamps(color);
        bars.forEach(location -> location.getBlock().setType(Material.AIR));
    }

    private void reload() {
        setLamps(Material.WHITE_WOOL);
    }

    private void setLamps(Material color) {
        for (Location loc : this.wool) {
            loc.getBlock().setType(color);
        }
    }

    private Material getColor() {
        if(tester instanceof Saboteur) {
        	Saboteur saboteur = (Saboteur) tester;
            if(saboteur.hack) {
            	saboteur.hack = false;
                return Material.LIME_WOOL;
            }
            return Material.RED_WOOL;
        }
        else if(tester instanceof Detective) return Material.BLUE_WOOL;
        else return Material.LIME_WOOL;
    }
    
    public void setColor(Material color) {
    	if(color != null)
    		this.color = color;
    }
}