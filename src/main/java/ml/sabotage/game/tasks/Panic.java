package ml.sabotage.game.tasks;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.scheduler.BukkitRunnable;

import ml.sabotage.Main;
import ml.sabotage.game.roles.IngamePlayer;
import ml.zer0dasho.plumber.game.Timer;

public class Panic extends BukkitRunnable {

    public boolean alive;

    public final Timer life;
    public final IngamePlayer ingamePlayer;

    private final Location location;

    public Panic(IngamePlayer ingamePlayer, Location location) {
        this.alive = true;
    	this.life = Main.config.panic_life.getTimer();
        this.ingamePlayer = ingamePlayer;
        this.location = location;
        
        location.getBlock().setType(Material.OAK_LEAVES);
        ingamePlayer.panics.add(this);

        if(ingamePlayer.panics.size() >= Main.config.max_panic_blocks)
			ingamePlayer.timeout = true;
    }

    @Override
    public void run() {
        if(life.tick()) 
            this.stop();
    }
    
    public void stop() {
        alive = false;
        location.getBlock().setType(Material.AIR);
        this.cancel();
    }
}