package ml.zer0dasho.plumber.game.arena;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.scheduler.BukkitRunnable;

import ml.sabotage.Main;

/**
 * Represents a temporary world.
 * Worlds are copied from a source folder into the default world container.
 *
 * @author 0-o#9646
 * @see World
 */
public class CopiedArena implements IArena {

	public World world;
	public File source;
	
	private CopiedArena() {}
	
	/**
	 * Constructor for Arena.
	 * 
	 * @param name - Name of arena's (new/copied) world folder
	 * @param source - Source world folder
	 * @throws IOException
	 */
	public CopiedArena(String name, File source) throws IOException {
		this.source = new File(Bukkit.getWorldContainer(), name);
		FileUtils.copyDirectory(source, this.source);
		this.world = new WorldCreator(name).createWorld();
		this.world.setAutoSave(false);
	}
	
	/**
	 * Warning! 
	 * This method doesn't copy the provided world.
	 * Deleting this arena will delete the provided world, and it will not be recoverable.
	 */
	public static CopiedArena of(World world) {
		CopiedArena arena = new CopiedArena();
		arena.world = world;
		arena.source = world.getWorldFolder();
		
		return arena;
	}
	
	/**
	 * Kicks players out of the arena and then deletes its world folder.
	 */
	public void delete(World kickPlayersTo) {
		new BukkitRunnable() {

			@Override
			public void run() {
				deleteNow(kickPlayersTo);
			}
			
		}.runTaskLater(Main.plugin, 200L);
	}
	
	public void deleteNow(World kickPlayersTo) {
		if(kickPlayersTo == null)
			kickPlayersTo = Bukkit.getWorlds().get(0);
		
		final World dest = kickPlayersTo;
		world.getPlayers().forEach(p -> p.teleport(dest.getSpawnLocation()));

		try {
			Bukkit.unloadWorld(world, false);
			FileUtils.forceDelete(source);
		} catch (IOException ex) {
			System.err.println(String.format("[Plumber] Failed to delete CopiedArena '%s'", world.getName()));
			//ex.printStackTrace(System.err);
		}
	}

	@Override
	public World getWorld() {
		return world;
	}	
}