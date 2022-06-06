package ml.sabotage.game.tasks;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import ml.sabotage.Main;
import ml.sabotage.game.events.TestCorpseEvent;
import ml.sabotage.game.roles.Detective;
import ml.sabotage.game.roles.IngamePlayer;
import ml.sabotage.game.stages.Ingame;
import ml.zer0dasho.plumber.game.Timer;
import ml.zer0dasho.plumber.utils.Sprink;

public class TestCorpse extends BukkitRunnable {

	public Ingame ingame;
	public IngamePlayer corpse;
	public Location location;
	public Timer timer;
	
	public Detective detective;
	
	public TestCorpse(Ingame ingame, IngamePlayer corpse, Location location) {
		this.ingame = ingame;
		this.corpse = corpse;
		this.location = location;
		this.timer = Main.config.corpse_tester.getTimer();
		this.detective = ingame.getPlayerManager().getDetective();
		
		Main.sabotage.broadcastAll(Sprink.color("&9" + detective.player.getName() + " &eis now testing the body of &7" + corpse.player.getName()));
	}

	@Override
	public void run() {
		Player player = detective.player;
		
		if(Main.config.test_corpse_range != -1 && player.getLocation().distance(location) > Main.config.test_corpse_range) {
			player.sendMessage(Sprink.color("&cYou need to stand by the corpse for " + Main.config.tester.seconds + " seconds!"));
			Bukkit.getPluginManager().callEvent(new TestCorpseEvent(this));
			this.cancel();
		}
		
		else if(timer.tick()) {
			Main.sabotage.broadcastAll(Sprink.color("&7" + corpse.player.getName() + " was " + corpse.getRole()));
			Bukkit.getPluginManager().callEvent(new TestCorpseEvent(this));
			this.cancel();
		}
	}

	
	
}
