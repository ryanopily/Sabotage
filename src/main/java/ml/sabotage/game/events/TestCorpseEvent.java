package ml.sabotage.game.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import ml.sabotage.game.tasks.TestCorpse;

public class TestCorpseEvent extends Event {
	
	private TestCorpse testCorpse;
	
	public TestCorpseEvent(TestCorpse testCorpse) {
		this.testCorpse = testCorpse;
	}
	
	public TestCorpse getTestCorpse() {
		return this.testCorpse;
	}
	
	private static final HandlerList HANDLERS = new HandlerList();
	
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
