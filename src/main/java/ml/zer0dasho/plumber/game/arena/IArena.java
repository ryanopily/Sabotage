package ml.zer0dasho.plumber.game.arena;

import org.bukkit.World;

/**
 * Represents a temporary world.
 * 
 * @author 0-o#9646
 * @see World
 */
public interface IArena {

	public World getWorld();
	
	/**
	 * Kicks players out of the arena and then deletes it.
	 * 
	 * @param kickPlayersTo - world to kick players to
	 * @see World
	 */
	public void delete(World kickPlayersTo);
	
}