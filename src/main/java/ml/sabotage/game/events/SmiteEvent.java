package ml.sabotage.game.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class SmiteEvent extends Event {

	private static final HandlerList handlers = new HandlerList();

	private Player player;
	
	public SmiteEvent(Player player) {
		this.player = player;
	}
	
	public Player getPlayer() {
		return this.player;
	}
	
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
