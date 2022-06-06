package ml.sabotage.game;

import java.io.File;
import java.io.IOException;

import org.bukkit.World;

import ml.sabotage.Main;
import ml.sabotage.utils.SabUtils;
import ml.zer0dasho.plumber.game.arena.CopiedArena;

public class SabArena extends CopiedArena {
	
	public SabArena(String worldName) throws IOException {
		super(worldName, new File(SabUtils.SOURCE, worldName));
		Main.ACTIVE_ARENAS.add(this);
	}

	public void delete(World kickPlayersTo) {
		super.delete(kickPlayersTo);
		Main.ACTIVE_ARENAS.remove(this);
	}
}