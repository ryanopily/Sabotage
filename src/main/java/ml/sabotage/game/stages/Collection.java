package ml.sabotage.game.stages;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.google.common.collect.Sets;

import ml.sabotage.Main;
import ml.sabotage.commands.GenericCommands;
import ml.sabotage.commands.Permissions;
import ml.sabotage.commands.VoteCommand;
import ml.sabotage.game.SabArena;
import ml.sabotage.game.SharedListener;
import ml.sabotage.game.gui.CollectionGui;
import ml.sabotage.game.managers.MapManager;
import ml.zer0dasho.plumber.game.Timer;
import ml.zer0dasho.plumber.game.arena.IArena;
import ml.zer0dasho.plumber.utils.Sprink;
import ml.zer0dasho.plumber.utils.Trycat;

public class Collection implements Listener {
	
	Set<UUID> players;
	MapManager mapManager; 
	IArena map;
	Timer timer;
	
	private final Sabotage sabotage;
	
    public final CollectionGui GUI; 
 
	public Collection(Sabotage sabotage) {
    	this.sabotage = sabotage;
    	this.players = Sets.newHashSet();
    	this.timer = Main.config.collection.getTimer();
    	this.GUI = new CollectionGui(timer);
	}
	
    public void add(UUID uuid) {
    	add(Bukkit.getPlayer(uuid));
    }
    
    public void add(Player player) {
    	players.add(player.getUniqueId());
    	
        Sprink.clearEffects(player);
        Sprink.clearInventory(player, false);
        player.setHealth(20.0);
        player.setGameMode(GameMode.SURVIVAL);
        player.getInventory().setItem(8, GenerateMagnifier());
        player.teleport(map.getWorld().getSpawnLocation());
        player.setScoreboard(GUI.scoreboard);
        GUI.update();

        Main.SAB_PLAYERS.get(player.getUniqueId()).updateKarma();
    }

    public void remove(UUID uuid) {
    	remove(Bukkit.getPlayer(uuid));
    }
    
    public void remove(Player player) {
    	players.remove(player.getUniqueId());
    }
	
	void start() throws IOException {
		Set<UUID> players = sabotage.lobby.players;
		
		this.map = new SabArena(VoteCommand.getMap());
		this.mapManager = new MapManager(map, players.size());
		players.forEach(this::add);
		
		Trycat.Try(() -> sabotage.lobby.hub.delete(this.map.getWorld()), (e) -> {});
		
    	Bukkit.getPluginManager().registerEvents(this, Main.plugin);
		this.timer.reset();
	}
	
    void stop() {
    	HandlerList.unregisterAll(this);
    }
	
	boolean run() {
		boolean status = !GenericCommands.PAUSE && timer.tick();
		GUI.update();
		
		if(status)
			sabotage.endCollection();
		
		return status;
	}
	
	/* Events */
    
	@EventHandler
    public void respawnOnDeath(PlayerMoveEvent e) {
		if(!sabotage.players.contains(e.getPlayer().getUniqueId()))
			return;
		
		if(e.getPlayer().getLocation().getY() < 0)
			e.getPlayer().teleport(this.map.getWorld().getSpawnLocation());
    }
    
    @EventHandler
    public void onDropItem(PlayerDropItemEvent e) {
		if(!sabotage.players.contains(e.getPlayer().getUniqueId()))
			return;
		
    	SharedListener.droppedShears(e);
    }
    
    @EventHandler
    public void onRightClickBlock(PlayerInteractEvent e) {
		if(!sabotage.players.contains(e.getPlayer().getUniqueId()))
			return;
		
    	SharedListener.rightClickBlock(e);
    }
    
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
		if(!sabotage.players.contains(e.getPlayer().getUniqueId()))
			return;
		
    	SharedListener.onBlockPlace(e);
    }
    
    @EventHandler
    public void onDamage(EntityDamageEvent e) {
		if(!sabotage.players.contains(e.getEntity().getUniqueId()))
			return;
		
    	if(e.getEntity().hasPermission(Permissions.NO_DAMAGE)) 
    		e.setCancelled(true);
    }
    
	/* Getters */
	
    public Set<UUID> getPlayers() {
    	return Collections.unmodifiableSet(this.players);
    }
    
    public MapManager getMapManager() {
    	return this.mapManager;
    }
    
    public World getWorld() {
    	return this.map.getWorld();
    }
    
    public Timer getTimer() {
    	return this.timer;
    }
    
    public void setTimer(Timer timer) {
    	if(timer != null)
    		this.timer = timer;
    }

	public ItemStack GenerateMagnifier(){
		ItemStack MAGNIFIER = new ItemStack(Material.GLASS_BOTTLE);
		ItemMeta meta = MAGNIFIER.getItemMeta();
		assert meta != null;
		meta.setDisplayName(Sprink.color("&C&lMagnifying glass"));
		meta.setLore(Collections.singletonList(Sprink.color("&cRightclick a player to check for blood")));
		MAGNIFIER.setItemMeta(meta);
		return MAGNIFIER;
	}

}