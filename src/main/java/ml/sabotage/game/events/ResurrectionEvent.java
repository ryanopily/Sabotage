package ml.sabotage.game.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import ml.sabotage.game.roles.IngamePlayer;

public class ResurrectionEvent extends Event {

	private IngamePlayer player;
	
	public ResurrectionEvent(IngamePlayer player) {
		this.player = player;
	}
	
	public IngamePlayer getPlayer() {
		return this.player;
	}
	
	private static final HandlerList HANDLERS = new HandlerList();

    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
