package ml.sabotage.game.stages;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import com.google.common.collect.Sets;

import ml.sabotage.Main;
import ml.sabotage.commands.GenericCommands;
import ml.sabotage.commands.Permissions;
import ml.sabotage.commands.VoteCommand;
import ml.sabotage.config.BookData;
import ml.sabotage.game.SabArena;
import ml.sabotage.game.gui.LobbyGui;
import ml.zer0dasho.plumber.config.Config;
import ml.zer0dasho.plumber.game.Timer;
import ml.zer0dasho.plumber.game.arena.IArena;
import ml.zer0dasho.plumber.utils.Sprink;
import ml.zer0dasho.plumber.utils.Trycat;

public class Lobby implements Listener {
	
    Set<UUID> players;    
    IArena hub;
    Timer timer;
    
	private Sabotage sabotage;
	
    public final LobbyGui GUI;
    public final BookData bookData;
    
    public Lobby(Sabotage sabotage) {
    	this.sabotage = sabotage; 
       	this.players = Sets.newHashSet();
    	this.timer = Main.config.lobby.getTimer();
       	this.GUI = new LobbyGui(timer, players);
    	this.bookData = Config.create(new BookData(), BookData.class, false);
    }
    
    public void add(UUID uuid) {
    	add(Bukkit.getPlayer(uuid));
    }
    
    public void add(Player player) {
    	this.players.add(player.getUniqueId());
    	
    	Sprink.clearEffects(player);
    	Sprink.clearInventory(player, true);
    	player.setHealth(20.0);
    	player.setGameMode(GameMode.SURVIVAL);
    	player.teleport(hub.getWorld().getSpawnLocation());
    	player.setScoreboard(GUI.scoreboard);
       	GUI.update();
       	
		Trycat.Try(() -> player.getInventory().addItem(bookData.getBook()), (e) -> e.printStackTrace());
    }
    
    public void remove(UUID uuid) {
    	remove(Bukkit.getPlayer(uuid));
    }
    
    public void remove(Player player) {
    	players.remove(player.getUniqueId());
    	GUI.update();
    }
    
    void start() throws IOException {
    	VoteCommand.resetMapSelection();
    	
    	this.hub = new SabArena(Main.config.hub);	
    	sabotage.players.forEach(this::add);
       	Trycat.Try(() -> sabotage.collection.map.delete(this.hub.getWorld()), (e) -> {});
     	
    	Bukkit.getPluginManager().registerEvents(this, Main.plugin);
    	this.timer.reset();
    }
    
    void stop() {
    	HandlerList.unregisterAll(this);
    }
    
    boolean run() {    	
    	if(!GenericCommands.PAUSE && players.size() >= 3 && timer.tick()) {
    		sabotage.endLobby();
    		return true;
    	}
    		
    	GUI.update();
    	return false;
    }
    
    /* Events */
    
	@EventHandler
	public void onTakeDamage(EntityDamageEvent e) {
		if(!sabotage.players.contains(e.getEntity().getUniqueId()))
			return;
		
		if(e.getEntity().hasPermission(Permissions.NO_DAMAGE))
			e.setCancelled(true);
	}
	
	@EventHandler
	public void onBuild(BlockPlaceEvent e) {
		if(!sabotage.players.contains(e.getPlayer().getUniqueId()))
			return;
		
		if(!Main.SAB_PLAYERS.get(e.getPlayer().getUniqueId()).config.canBuild)
			e.setCancelled(true);
	}
	
	/* Getters */
	
    public World getWorld() {
    	return this.hub.getWorld();
    }
    
    public Timer getTimer() {
    	return this.timer;
    }
	
    public Set<UUID> getPlayers() {
    	return Collections.unmodifiableSet(this.players);
    }
    
    public void setTimer(Timer timer) {
    	if(timer != null)
    		this.timer = timer;
    }
}