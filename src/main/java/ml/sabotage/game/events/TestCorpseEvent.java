package ml.sabotage.game.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import ml.sabotage.game.tasks.TestCorpse;

public class TestCorpseEvent extends Event {
	
	private static final HandlerList handlers = new HandlerList();

	private TestCorpse testCorpse;
	
	public TestCorpseEvent(TestCorpse testCorpse) {
		this.testCorpse = testCorpse;
	}
	
	public TestCorpse getTestCorpse() {
		return this.testCorpse;
	}
	
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
