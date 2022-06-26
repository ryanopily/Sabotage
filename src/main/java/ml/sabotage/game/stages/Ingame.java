package ml.sabotage.game.stages;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Team;

import ml.sabotage.Main;
import ml.sabotage.commands.GenericCommands;
import ml.sabotage.config.PlayerData;
import ml.sabotage.game.SharedListener;
import ml.sabotage.game.events.ResurrectionEvent;
import ml.sabotage.game.events.SmiteEvent;
import ml.sabotage.game.events.TestCorpseEvent;
import ml.sabotage.game.events.TesterEvent;
import ml.sabotage.game.gui.IngameGui;
import ml.sabotage.game.managers.MapManager;
import ml.sabotage.game.managers.PlayerManager;
import ml.sabotage.game.roles.Detective;
import ml.sabotage.game.roles.IngamePlayer;
import ml.sabotage.game.roles.Saboteur;
import ml.sabotage.game.tasks.Panic;
import ml.sabotage.game.tasks.TestCorpse;
import ml.sabotage.game.tasks.Tester;
import ml.zer0dasho.corpseimmortal.CorpseImmortal;
import ml.zer0dasho.corpseimmortal.events.CorpseOpenInventoryEvent;
import ml.zer0dasho.plumber.game.Timer;
import ml.zer0dasho.plumber.utils.Sprink;

public class Ingame implements Listener {

    PlayerManager playerManager;
    Timer timer, refill;
    Tester tester;
    TestCorpse testCorpse;
    boolean rewarded;
    
    public final IngameGui GUI;
    
    private final Sabotage sabotage;
  
    public Ingame(Sabotage sabotage) {
    	this.sabotage = sabotage;
    	this.timer = Main.config.ingame.getTimer();
    	this.refill = Main.config.refill.getTimer();
    	this.GUI = new IngameGui(timer);

    	refill.onFinish = () -> sabotage.collection.mapManager.refill();
    }
    
    void start() {
    	this.playerManager = new PlayerManager(sabotage.collection.players);
    	this.rewarded = false;

    	this.initScoreboard();
    	
    	Bukkit.getPluginManager().registerEvents(this, Main.plugin);
    	this.timer.reset();
    	this.refill.reset();
    }
    
    void stop() {
    	HandlerList.unregisterAll(this);
    }
    
    boolean run() {
    	if(!GenericCommands.PAUSE) {
    		if(checkDeath() || GUI.getTimer().tick()) {
    			sabotage.endIngame();
    			return true;
    		}
    		
        	refill.tick();
        	GUI.update();
    	}
    	
    	return false;
    }
    
    /**
     * Returns all innocents including detectives.
     * 
     * @return
     */
    public List<IngamePlayer> innocents() {
    	List<IngamePlayer> innocents = playerManager.innocents(true);
    	innocents.addAll(playerManager.detectives(true));
    	return innocents;
    }
    
    private void innocentsWin() {
    	rewardWinners(innocents(), "&aInnocents win!");
        punishLosers(playerManager.saboteurs(true));
    }
    
    private void saboteursWin() {
    	rewardWinners(playerManager.saboteurs(true), "&cSaboteurs win!");
    	punishLosers(innocents());
    }
    
    private void rewardWinners(List<IngamePlayer> winners, String message) {
        sabotage.broadcastAll(message);
        
        for(IngamePlayer ingamePlayer : winners) {
        	PlayerData data = ingamePlayer.sabPlayer.config;
        	data.wins += 1;
        	ingamePlayer.sabPlayer.addKarma(40);
        }
        
        rewarded = true;
    }

    private void punishLosers(List<IngamePlayer> losers) {
        for (IngamePlayer ingamePlayer : losers) {
        	PlayerData data = ingamePlayer.sabPlayer.config;
            data.losses += 1;
            data.save();
        }
    }
    
    private void revealSaboteurs() {
        sabotage.broadcastAll(Sprink.color("&cThe saboteurs were ") + getSaboteurList());
    }
    
    private String getSaboteurList() {
    	StringBuilder result = new StringBuilder();
    	List<String> saboteurs = playerManager.saboteurs(false).stream().map(igp -> igp.player.getName()).collect(Collectors.toList());
    	
    	for(int i = 0; i < saboteurs.size(); i++) {
    		if(i == saboteurs.size() - 1) {
    			if(saboteurs.size() != 1) result.append("and ");
    			result.append(saboteurs.get(i) + ".");
    		}
    		else result.append(saboteurs.get(i) + ", ");
    	}
    	
    	return result.toString();
    }

    private boolean checkDeath() {
    	
    	if(GenericCommands.TEST)
    		return false;
 
    	List<IngamePlayer> innocents = innocents();
    	List<IngamePlayer> saboteurs = playerManager.saboteurs(true);
    
    	if((innocents.size() == 0 || saboteurs.size() == 0) && !rewarded) {
    		if(innocents.size() == 0)
    			this.saboteursWin();
    		else
    			this.innocentsWin();

    		revealSaboteurs();
    		rewarded = true;
    		return true;
    	}  
        
        return false;
    }
    
    private void initScoreboard() {
    	playerManager.innocents(false).stream().forEach(igp -> {
    		GUI.addPlayer(igp);
    		//System.out.println(igp.player.getDisplayName());
    		GUI.getInnocent().getTeam("Innocent").addPlayer(igp.player);
    		GUI.getSaboteur().getTeam("Innocent").addPlayer(igp.player);
    		GUI.getDetective().getTeam("Innocent").addPlayer(igp.player);
    		GUI.getSpectator().getTeam("Else").addPlayer(igp.player);
    	});
    	
    	playerManager.saboteurs(false).stream().forEach(igp -> {
    		GUI.addPlayer(igp);
    		//System.out.println(igp.player.getDisplayName());
    		GUI.getInnocent().getTeam("Innocent").addPlayer(igp.player);
    		GUI.getSaboteur().getTeam("Saboteur").addPlayer(igp.player);
    		GUI.getDetective().getTeam("Innocent").addPlayer(igp.player);
    		GUI.getSpectator().getTeam("Else").addPlayer(igp.player);
       	});
   
    	if(playerManager.getDetective() != null) {
	    	GUI.addPlayer(playerManager.getDetective());
	    	//System.out.println(playerManager.getDetective().player.getDisplayName());
			GUI.getInnocent().getTeam("Detective").addPlayer(playerManager.getDetective().player);
			GUI.getSaboteur().getTeam("Detective").addPlayer(playerManager.getDetective().player);
			GUI.getDetective().getTeam("Detective").addPlayer(playerManager.getDetective().player);
			GUI.getSpectator().getTeam("Detective").addPlayer(playerManager.getDetective().player);
    	}
    }
    
    /* Events */
    
    @EventHandler
    public void onSmite(SmiteEvent event) {
    	Player player = event.getPlayer();
    	player.setHealth(20.0);
    	player.setGameMode(GameMode.SPECTATOR);
    	player.teleport(this.getWorld().getSpawnLocation());
    	GUI.addSpectator(player);
    }
    
    @EventHandler
    public void onResurrection(ResurrectionEvent event) {
    	IngamePlayer ingamePlayer = event.getPlayer();
    	Player player = ingamePlayer.player;

    	GUI.addPlayer(ingamePlayer);
    	player.setHealth(20.0);
    	player.setGameMode(GameMode.SURVIVAL);
    	player.teleport(sabotage.collection.map.getWorld().getSpawnLocation());
    }

    @EventHandler
    public void onTest(TesterEvent e) {
    	if(e.getTester() == this.tester)
    		this.tester = null;
    }
    
    @EventHandler
    public void onTestCorpse(TestCorpseEvent e) {
    	if(e.getTestCorpse() == this.testCorpse)
    		this.testCorpse = null;
    }
    
    @EventHandler
    public void droppedShears(PlayerDropItemEvent e) {
		if(!this.playerManager.getDetective().player.equals(e.getPlayer()))
			return;
		
    	SharedListener.droppedShears(e);
    }
    
    @EventHandler
    public void onDamage(EntityDamageEvent e) {
    	if(!sabotage.players.contains(e.getEntity().getUniqueId()))
    		return;
    	
    	if(rewarded)
    		e.setCancelled(true);
    	
    	else if(e.getEntity() instanceof Player && this.playerManager.isAlive(e.getEntity().getUniqueId())) {
    		
    		Player p = (Player) e.getEntity();
	    	
    		/* Player died */
    		if(p.getHealth() - e.getFinalDamage() <= 0) {
	    		p.setHealth(20.0);
	        	sabotage.broadcastAll(Sprink.color("&cA player has died... " + (playerManager.players(true).size() - 1) + " players remain."));
	        	kill2(p);
	    	}
    	}
    }

    @EventHandler
    public void onCorpseClick(CorpseOpenInventoryEvent e) {
    	if(!sabotage.players.contains(e.getClicker().getUniqueId()))
    		return;
    	
    	IngamePlayer clicker = playerManager.getRole(e.getClicker().getUniqueId());
    	IngamePlayer corpse  = playerManager.dead().get(e.getCorpse().getId());
    	
		if(clicker == null || !playerManager.isAlive(clicker.player.getUniqueId())) {
			e.setCancelled(true);
			return;
		}

    	if(clicker instanceof Detective && e.getClicker().getInventory().getItemInMainHand().getType().equals(Material.SHEARS) && corpse != null) {
    		if(testCorpse == null) {
    			this.testCorpse = new TestCorpse(this,corpse,e.getCorpse().getBody().getStoredLocation());
    			this.testCorpse.runTaskTimer(Main.plugin, 0L, 20L);
    		}
   
    		e.setCancelled(true);
    	}
    }
    
    @EventHandler
    public void onRightClickPlayer(PlayerInteractEntityEvent e) {    	
        IngamePlayer clicker = playerManager.getRole(e.getPlayer().getUniqueId());
        IngamePlayer clicked = playerManager.getRole(e.getRightClicked().getUniqueId());
        ItemStack item = e.getPlayer().getInventory().getItemInMainHand();
        
        if(clicker == null || clicked == null) return;
        if(item.getType().equals(Material.AIR) || !this.playerManager.isAlive(e.getPlayer().getUniqueId())) return;
        
        switch(item.getType()) {
	        case GLASS_BOTTLE:
	        	clicker.player.sendMessage(clicked.getBloodMessage());
	        	break;
	        	
	        case SHEARS:
	        	if(clicker instanceof Detective) {
		            Detective det = (Detective) clicker;
		            
		            if (det.insight) {
			            e.getPlayer().sendMessage(Sprink.color("&e" + clicked.player.getName() + " &eis ") + clicked.getRole());
			            det.insight = false;
		            }
		            
		            break;
	        	}
	            
        	default:
        		break;
        }
    }
    
    @EventHandler
    public void onPlayerHit(EntityDamageByEntityEvent e) { 
    	if(!sabotage.players.contains(e.getEntity().getUniqueId()) || !this.playerManager.isAlive(e.getEntity().getUniqueId()))
    		return;
    	
    	IngamePlayer damager = playerManager.getRole(e.getDamager().getUniqueId());

		if(damager != null && this.playerManager.isAlive(damager.player.getUniqueId())) {
		    double result = damager.blood < 2.0 ? 0.2 : 0.0;
	        damager.blood += result;
		}
    }
    
    @EventHandler
    public void blockPlace(BlockPlaceEvent e) {
		if(!sabotage.players.contains(e.getPlayer().getUniqueId()))
			return;
		
    	if(!playerManager.isAlive(e.getPlayer().getUniqueId())) {
    		e.setCancelled(true);
    		return;
    	}
    	
    	IngamePlayer placer = playerManager.getRole(e.getPlayer().getUniqueId());

        if(e.getBlock().getType().equals(Material.OAK_LEAVES)) {
	        	
	            if(placer.getPanics() == 0) 
	            	placer.timeout = false;
	            
	            if(placer.timeout)  
	            	e.setCancelled(true);
	            
	            else 
	            	new Panic(placer, e.getBlock().getLocation()).runTaskTimer(Main.plugin, 0L, 20L);
        }
        
        else SharedListener.onBlockPlace(e);
    }

    
    @EventHandler
    public void onClick(PlayerInteractEvent e) {
		if(!sabotage.players.contains(e.getPlayer().getUniqueId()) || !playerManager.isAlive(e.getPlayer().getUniqueId()))
			return;
    	
    	if(e.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.COMPASS)) 
    		doCompass(e);
    	
    	else if(e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
    		if(e.getClickedBlock().getState() instanceof Sign)
    			test(e);
    		else 
    			SharedListener.rightClickBlock(e);
    	}
    }
    
    @EventHandler
    public void deadChat(AsyncPlayerChatEvent e) {
		if(!sabotage.players.contains(e.getPlayer().getUniqueId()))
			return;
    	
    	IngamePlayer player = playerManager.getRole(e.getPlayer().getUniqueId());
    	
    	//IF PLAYER IS DEAD
    	if(!playerManager.isAlive(e.getPlayer().getUniqueId())) { 
    		List<Player> players = new ArrayList<Player>(playerManager.dead().keySet().stream().map(Bukkit::getPlayer).collect(Collectors.toList()));
    		e.getRecipients().clear();
    		e.getRecipients().addAll(players);
    		e.setFormat(Sprink.color("&4[Dead] &7" + e.getPlayer().getDisplayName() + " &f" + e.getMessage()));
    	}
    	
    	//IF PLAYER IS ALIVE
    	else {
    		if(e.getMessage().startsWith("@")) {
    			if(player instanceof Saboteur) {
    				List<Player> saboteurs = playerManager.saboteurs(true).stream().map(igp -> igp.player).collect(Collectors.toList());
    				e.getRecipients().clear();
    				e.getRecipients().addAll(saboteurs);
    				e.setFormat(Sprink.color("&4[Saboteur] &c" + e.getPlayer().getName() + " &e" + e.getMessage().substring(1)));
    			}
    			
    			else e.setCancelled(true);
    		}
    	}
    }

    public void doCompass(PlayerInteractEvent e) {
        IngamePlayer tracker = playerManager.getRole(e.getPlayer().getUniqueId());
        
        if(this.getPlayerManager().isAlive(e.getPlayer().getUniqueId())) 
        	return;
        
        Player target = null;
        
        for(IngamePlayer igp : playerManager.players(true)) {	
        	Player player = igp.player;
        	
            if(player.equals(e.getPlayer())) 
            	continue;
            else if(target == null) 
            	target = player;
            else if(target.getLocation().distance(tracker.player.getLocation()) <= player.getPlayer().getLocation().distance(tracker.player.getLocation())) 
            		continue;
            else 
            	target = player;
        }
        
        if(target != null) {
        	tracker.player.setCompassTarget(target.getPlayer().getLocation());
        	tracker.player.sendMessage(ChatColor.YELLOW + "You are currently tracking: " + target.getPlayer().getName());
        }
    }
    
    private void test(PlayerInteractEvent e) {
		if(!sabotage.players.contains(e.getPlayer().getUniqueId()))
			return;

    	IngamePlayer clicker = playerManager.getRole(e.getPlayer().getUniqueId());
    	if(clicker == null || !playerManager.isAlive(e.getPlayer().getUniqueId())) 
    		return;
    	
    	if(e.getClickedBlock().getState() instanceof Sign) {
            Sign sign = (Sign) e.getClickedBlock().getState();
            
            if(!sign.getLine(0).contains("[Test]") || this.tester != null) 
            	return;
            
            this.tester = new Tester(sabotage.collection.mapManager, clicker);
            this.tester.runTaskTimer(Main.plugin, 0L, 20L);
        }
    }

    private void kill2(Player dead) {   
		if(!sabotage.players.contains(dead.getUniqueId()))
			return;
		
        IngamePlayer victim = playerManager.getRole(dead.getUniqueId());
        IngamePlayer killer = dead.getKiller() == null ? null : playerManager.getRole(dead.getKiller().getUniqueId());
        
        if(victim != null) {
        	Location deathbed = victim.player.getLocation();
        	
        	victim.die(killer);
        	playerManager.smite(victim.player);
        	CorpseImmortal.API().spawnCorpse(victim.player.getName(), deathbed);
        	
        	if(killer != null && playerManager.isAlive(killer.player.getUniqueId())) {
            	killer.kill(victim);
            	killer.player.sendMessage(Sprink.color("&e" + victim.player.getName() + " was " + victim.getRole()));
            	
                if(killer.blood < 3.0)
                    killer.blood = 3.0;
                
                else killer.blood += (1d/3d);
        	}
        	
        	if(victim instanceof Saboteur && ((Saboteur)victim).martyr)
        		SharedListener.explode(deathbed.getBlock(), 20); 	
        }
    }
    
	/* Getters */
    
    public MapManager getMapManager() {
    	return this.sabotage.collection.mapManager;
    }
    
    public World getWorld() {
    	return this.sabotage.collection.map.getWorld();
    }

	public PlayerManager getPlayerManager() {
		return playerManager;
	}

	public boolean isRewarded() {
		return rewarded;
	}

	public Timer getTimer() {
		return timer;
	}

	public Timer getRefill() {
		return refill;
	}
	
	public void setTimer(Timer timer) {
		if(refill != null)
			this.timer = timer;
	}
	
	public void setRefill(Timer refill) {
		if(refill != null)
			this.refill = refill;
	}
}