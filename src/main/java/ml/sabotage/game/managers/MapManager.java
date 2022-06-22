package ml.sabotage.game.managers;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Stream;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Sign;
import org.bukkit.entity.Item;

import com.google.common.collect.Sets;

import ml.zer0dasho.plumber.game.arena.IArena;
import ml.zer0dasho.plumber.utils.Sprink;
import org.bukkit.scheduler.BukkitRunnable;

import static ml.sabotage.Main.plugin;

public class MapManager {
	
    IArena map;
    Set<Location> bars = Sets.newHashSet();
    Set<Location> lamps = Sets.newHashSet();
    Set<Location> chests = Sets.newHashSet();
    Set<Location> enderchests = Sets.newHashSet();
    
    public MapManager(IArena map, int size) {
    	this.map = map;
    	setMap(size);
    }

    /**
     * Respawns all chests.
     */
    public void refill() {
    	this.chests.forEach(location -> location.getBlock().setType(Material.CHEST));
    	this.enderchests.forEach(location -> location.getBlock().setType(Material.ENDER_CHEST));
    }
    
    void setMap(int size) {

		new BukkitRunnable() {

			@Override
			public void run() {
				Arrays.stream(map.getWorld().getLoadedChunks()).flatMap(chunk -> Stream.of(chunk.getTileEntities())).forEach(block -> {
					switch(block.getType()) {

						case CHEST:
							chests.add(block.getLocation());
							break;

						case ENDER_CHEST:
							enderchests.add(block.getLocation());
							break;

						case OAK_SIGN:
							Sign sign = (Sign) block;

							if(sign.getLine(0).equalsIgnoreCase("[wool_lamp]"))
								lamps.add(block.getLocation());

							else if(sign.getLine(0).equalsIgnoreCase("[bar]"))
								bars.add(block.getLocation());
							break;

						default:
							break;
					}
				});
			}
		}.runTaskLater(plugin, 10L);
            
    	int limit = 50 + size * 20;
    	
        while(chests.size() > limit)
        	Sprink.randomElement(chests, true).getBlock().setType(Material.AIR);
        
        limit = Math.round(limit * 0.025f);
        
        while(enderchests.size() < limit && chests.size() > 0) {
        	Location random = Sprink.randomElement(chests, true);
            enderchests.add(random);
            random.getBlock().setType(Material.ENDER_CHEST);
        }


		new BukkitRunnable() {
			@Override
			public void run() {
				lamps.forEach(location -> {
					lamps.forEach(lamp -> lamp.getBlock().setType(Material.WHITE_WOOL));
					bars.forEach(bar -> bar.getBlock().setType(Material.AIR));
				});
			}
		}.runTaskLater(plugin, 10L);

        new BukkitRunnable() {
			@Override
			public void run() {
				map.getWorld().getEntities().forEach(entity -> {
					if (entity instanceof Item)
						entity.remove();
				});
			}
		}.runTaskLater(plugin, 5L);
        refill();
    }
    
    /* Getters */
    
    public World getWorld() {
    	return this.map.getWorld();
    }

	public Set<Location> getBars() {
		return bars;
	}

	public Set<Location> getLamps() {
		return lamps;
	}

	public Set<Location> getChests() {
		return chests;
	}

	public Set<Location> getEnderchests() {
		return enderchests;
	}
}