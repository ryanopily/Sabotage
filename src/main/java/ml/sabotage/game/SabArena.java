package ml.sabotage.game;

import java.io.File;
import java.io.IOException;

import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

import ml.sabotage.Main;
import ml.sabotage.utils.SabUtils;
import ml.zer0dasho.plumber.game.arena.CopiedArena;

public class SabArena extends CopiedArena {
	
	public SabArena(String worldName) throws IOException {
		super(worldName, new File(SabUtils.SOURCE, worldName));
		Main.ACTIVE_ARENAS.add(this);
	}

	public void delete(World kickPlayersTo) {
		SabArena arena = this;

		new BukkitRunnable() {
			@Override
			public void run() {
				arena.delete(kickPlayersTo);
			}
		}.runTaskLater(Main.plugin, 50L);
	}
}