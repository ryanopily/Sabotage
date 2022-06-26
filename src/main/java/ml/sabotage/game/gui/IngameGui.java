package ml.sabotage.game.gui;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;

import ml.sabotage.game.roles.Detective;
import ml.sabotage.game.roles.IngamePlayer;
import ml.sabotage.game.roles.Innocent;
import ml.sabotage.game.roles.Saboteur;
import ml.zer0dasho.plumber.game.ScoreMenu;
import ml.zer0dasho.plumber.game.Timer;
import ml.zer0dasho.plumber.utils.Sprink;
import org.bukkit.ChatColor;

public class IngameGui {

	Timer timer;
	ScoreMenu innocent, saboteur, detective, spectator;
	
	public IngameGui(Timer timer) {
		this.timer = timer;
		
		this.innocent = getMenu("&aInnocent");
		innocent.newTeam("Innocent", ChatColor.YELLOW, "", "", false, true);
		innocent.newTeam("Detective", ChatColor.BLUE, "", "", false, true);
		
		this.saboteur = getMenu("&cSaboteur");
		saboteur.newTeam("Innocent", ChatColor.GREEN, "", "", false, true);
		saboteur.newTeam("Saboteur", ChatColor.RED, "", "", false, true);
		saboteur.newTeam("Detective", ChatColor.BLUE, "", "", false, true);
		
		this.detective = getMenu("&9Detective");
		detective.newTeam("Innocent", ChatColor.YELLOW, "", "", false, true);
		detective.newTeam("Detective", ChatColor.BLUE, "", "", false, true);
		
		this.spectator = getMenu("&dSpectator");
		spectator.newTeam("Detective", ChatColor.BLUE, "", "", false, true);
		spectator.newTeam("Else", ChatColor.YELLOW, "", "", false, true);
	}
	
	public void update() {
        innocent.newScore(DisplaySlot.SIDEBAR, Sprink.color("&aTimer: &f") + timer.time.get(), 2);
        saboteur.newScore(DisplaySlot.SIDEBAR, Sprink.color("&aTimer: &f") + timer.time.get(), 2);
        detective.newScore(DisplaySlot.SIDEBAR, Sprink.color("&aTimer: &f") + timer.time.get(), 2);
        spectator.newScore(DisplaySlot.SIDEBAR, Sprink.color("&aTimer: &f") + timer.time.get(), 2);
	}
	
	public void addPlayer(IngamePlayer player) {
		if(player instanceof Innocent) 
			player.player.setScoreboard(innocent.scoreboard);
		
		if(player instanceof Saboteur) 
			player.player.setScoreboard(saboteur.scoreboard);
		
		if(player instanceof Detective) 
			player.player.setScoreboard(detective.scoreboard);
	}
	
	public void addSpectator(Player player) {
		player.setScoreboard(spectator.scoreboard);
	}
	
	private ScoreMenu getMenu(String role) {
		ScoreMenu menu = new ScoreMenu();

        menu.newObjective(LobbyGui.PLUGIN_NAME, "dummy", DisplaySlot.SIDEBAR);    
        menu.newScore(DisplaySlot.SIDEBAR, " ", 4);
        menu.newScore(DisplaySlot.SIDEBAR, Sprink.color("&bStatus: &7In-game"), 3);
        menu.newScore(DisplaySlot.SIDEBAR, Sprink.color("&aTimer: &f") + timer.time.get(), 2);
        menu.newScore(DisplaySlot.SIDEBAR, "  ", 1);
        menu.newScore(DisplaySlot.SIDEBAR, Sprink.color("&f&lRole: &r" + role), 0);
        
        return menu;
	}
	
	/* Getters & Setters */
	
	public Timer getTimer() {
		return this.timer;
	}
	
	public ScoreMenu getInnocent() {
		return innocent;
	}

	public ScoreMenu getSaboteur() {
		return saboteur;
	}

	public ScoreMenu getDetective() {
		return detective;
	}

	public ScoreMenu getSpectator() {
		return spectator;
	}

	public void setTimer(Timer timer) {
		if(timer != null)
			this.timer = timer;
	}
}