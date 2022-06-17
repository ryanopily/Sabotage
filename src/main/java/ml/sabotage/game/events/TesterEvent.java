package ml.sabotage.game.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import ml.sabotage.game.tasks.Tester;

public class TesterEvent extends Event {
	
	private Tester tester;
	
	public TesterEvent(Tester tester) {
		this.tester = tester;
	}
	
	public Tester getTester() {
		return this.tester;
	}
	
	private static final HandlerList HANDLERS = new HandlerList();
	
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
