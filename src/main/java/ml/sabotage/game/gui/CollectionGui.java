package ml.sabotage.game.gui;

import org.bukkit.scoreboard.DisplaySlot;

import ml.zer0dasho.plumber.game.ScoreMenu;
import ml.zer0dasho.plumber.game.Timer;
import ml.zer0dasho.plumber.utils.Sprink;

public class CollectionGui extends ScoreMenu {

	Timer timer;
	
	public CollectionGui(Timer timer) {
		super();
		
		this.timer = timer;
        this.newObjective(LobbyGui.PLUGIN_NAME, "dummy", DisplaySlot.SIDEBAR);

        this.newScore(DisplaySlot.SIDEBAR, " ", 4);
        this.newScore(DisplaySlot.SIDEBAR, Sprink.color("&bStatus: &7 Collection"), 3);
        this.newScore(DisplaySlot.SIDEBAR, Sprink.color("&aTimer: &f") + timer.time.get(), 2);
        this.newScore(DisplaySlot.SIDEBAR, "  ", 1);
	}
	
	public void update() {
        this.newScore(DisplaySlot.SIDEBAR, Sprink.color("&aTimer: &f") + timer.time.get(), 2);
	}
	
	/* Getters & Setters */
	
	public Timer getTimer() {
		return this.timer;
	}
	
	public void setTimer(Timer timer) {
		this.timer = timer;
	}
}
