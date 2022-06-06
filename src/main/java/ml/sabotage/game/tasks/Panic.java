package ml.sabotage.game.tasks;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.scheduler.BukkitRunnable;

import ml.sabotage.Main;
import ml.sabotage.game.roles.IngamePlayer;
import ml.zer0dasho.plumber.game.Timer;

public class Panic extends BukkitRunnable {
	
    public final Timer life;
    public final IngamePlayer ingamePlayer;
    
    private final Location location;

    public Panic(IngamePlayer ingamePlayer, Location location) {
    	
    	this.life = Main.config.panic_life.getTimer();
        this.ingamePlayer = ingamePlayer;
        this.location = location;
        
        location.getBlock().setType(Material.OAK_LEAVES);
        
        if(ingamePlayer.getPanics() >= Main.config.max_panic_blocks)
			ingamePlayer.timeout = true;
        
        ingamePlayer.addPanic(this);
    }

    @Override
    public void run() {
        if(life.tick())
            this.stop();
    }
    
    public void stop() {
    	ingamePlayer.removePanic(this);
        location.getBlock().setType(Material.AIR);
        this.cancel();
    }
}