package ml.sabotage.game.gui;

import java.util.Collection;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.scoreboard.DisplaySlot;

import ml.zer0dasho.plumber.game.ScoreMenu;
import ml.zer0dasho.plumber.game.Timer;
import ml.zer0dasho.plumber.utils.Sprink;

public class LobbyGui extends ScoreMenu {

	Timer timer;
	Collection<UUID> players;
	
	public LobbyGui(Timer timer, Collection<UUID> players) {
		super();
		this.timer = timer;
		this.players = players;
		
        this.newObjective(LobbyGui.PLUGIN_NAME, "dummy", DisplaySlot.SIDEBAR);
        
        this.newScore(DisplaySlot.SIDEBAR, " ", 4);
        this.newScore(DisplaySlot.SIDEBAR, Sprink.color("&bStatus: &7Pre-Game"), 3);
        this.newScore(DisplaySlot.SIDEBAR, Sprink.color("&aTimer: &f") + timer.time.get(), 2);
        this.newScore(DisplaySlot.SIDEBAR, "  ", 1);
        this.newScore(DisplaySlot.SIDEBAR, Sprink.color("&ePlayers: &7" + players.size()), 0);
	}
	
	public void update() {
        this.newScore(DisplaySlot.SIDEBAR, Sprink.color("&aTimer: &f") + timer.time.get(), 2);
        this.newScore(DisplaySlot.SIDEBAR, Sprink.color("&ePlayers: &7" + players.size()), 0);
	}
	
	/* Getters & Setters */
	
	public Timer getTimer() {
		return this.timer;
	}
	
	public void setTimer(Timer timer) {
		if(timer != null)
			this.timer = timer;
	}
	
	public static final String PLUGIN_NAME = ChatColor.GOLD + " " + ChatColor.BOLD + "SABOTAGE" + " ";
}
