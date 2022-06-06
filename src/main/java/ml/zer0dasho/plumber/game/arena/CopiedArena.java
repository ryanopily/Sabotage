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
		FileUtils.copyDirectory(source, new File(Bukkit.getWorldContainer(), name));
		
		this.source = new File(Bukkit.getWorldContainer(), name);
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
		if(kickPlayersTo == null)
			kickPlayersTo = Bukkit.getWorlds().get(0);
		
		final World dest = kickPlayersTo;
		world.getPlayers().forEach(p -> p.teleport(dest.getSpawnLocation()));
		
		new BukkitRunnable() {

			@Override
			public void run() {
				try {
					Bukkit.unloadWorld(world, false);
					FileUtils.deleteDirectory(source);
				} catch (IOException ex) {
					System.err.println(String.format("[Plumber] Failed to delete CopiedArena '%s'", world.getName()));
					ex.printStackTrace(System.err);
				}
			}
			
		}.runTaskLater(Main.plugin, 100L);
		

	}

	@Override
	public World getWorld() {
		return world;
	}	
}