package ml.sabotage.game.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import ml.sabotage.game.tasks.Tester;

public class TesterEvent extends Event {
	
	private static final HandlerList handlers = new HandlerList();
	
	private Tester tester;
	
	public TesterEvent(Tester tester) {
		this.tester = tester;
	}
	
	public Tester getTester() {
		return this.tester;
	}
	
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
