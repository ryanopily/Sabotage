package ml.sabotage.game.managers;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import ml.sabotage.Main;
import ml.sabotage.game.SabPlayer;
import ml.sabotage.game.events.ResurrectionEvent;
import ml.sabotage.game.events.SmiteEvent;
import ml.sabotage.game.roles.Detective;
import ml.sabotage.game.roles.IngamePlayer;
import ml.sabotage.game.roles.Innocent;
import ml.sabotage.game.roles.Saboteur;
import ml.sabotage.utils.SabUtils;
import ml.zer0dasho.plumber.utils.Sprink;

public class PlayerManager {

    Detective detective;
    Map<UUID, IngamePlayer> roles = Maps.newHashMap();
    Map<UUID, IngamePlayer> alive = Maps.newHashMap();
    
    public PlayerManager(Collection<UUID> players) {
    	this.assignRoles(players);
    }
    
    /**
     * Returns all players.
     * 
     * @param aliveOnly
     * @return
     */
    public List<IngamePlayer> players(boolean aliveOnly) {
    	return roles.values().stream().filter(igp -> !aliveOnly || alive.containsValue(igp)).collect(Collectors.toList());
    }
    
    /**
     * Returns all detectives. 
     * 
     * @param aliveOnly
     * @return
     */
    public List<IngamePlayer> detectives(boolean aliveOnly) {
    	return roles.values().stream().filter(Detective.class::isInstance).filter(igp -> !aliveOnly || alive.containsValue(igp)).collect(Collectors.toList());
    }
    
    /**
     * Returns all innocents.
     * 
     * @param aliveOnly
     * @return
     */
    public List<IngamePlayer> innocents(boolean aliveOnly) {
    	return roles.values().stream().filter(Innocent.class::isInstance).filter(igp -> !aliveOnly || alive.containsValue(igp)).collect(Collectors.toList());
    }
    
    /**
     * Returns all saboteurs.
     * 
     * @param aliveOnly
     * @return
     */
    public List<IngamePlayer> saboteurs(boolean aliveOnly) {
    	return roles.values().stream().filter(Saboteur.class::isInstance).filter(igp -> !aliveOnly || alive.containsValue(igp)).collect(Collectors.toList());
    }
    
    /**
     * Returns all dead players.
     * 
     * @return
     */
    public Map<UUID, IngamePlayer> dead() {
    	Map<UUID, IngamePlayer> dead = Maps.newHashMap(roles);
    	alive.forEach((id, igp) -> dead.remove(id));
    	return dead;
    }
    
    private static final List<Function<SabPlayer, ? extends IngamePlayer>> ROLES = Arrays.asList(Innocent::new, Saboteur::new);

    /**
     * Brings a player back from the dead.
     * 
     * @param player
     * @param newRole
     * @return
     */
    public IngamePlayer resurrect(Player player, boolean newRole) {

    	Optional<IngamePlayer> iplayer = Optional.ofNullable(newRole ? null : roles.get(player.getUniqueId()));
    	iplayer.ifPresent(igp -> {
    		alive.put(player.getUniqueId(), igp);
        	Bukkit.getPluginManager().callEvent(new ResurrectionEvent(igp));
    	});

    	return iplayer.orElseGet(() -> resurrect(player, Sprink.randomElement(ROLES, false)));
    }

    /**
     * Brings a player back from the dead with the specified role.
     * 
     * @param <T>
     * @param player
     * @param igp
     * @return
     */
    
    public <T extends IngamePlayer> T resurrect(Player player, Function<SabPlayer, T> igp) {
    	SabPlayer sabPlayer = Main.SAB_PLAYERS.get(player.getUniqueId());
    	
    	T ingamePlayer = igp.apply(sabPlayer);
    	
    	roles.put(player.getUniqueId(), ingamePlayer);
    	alive.put(player.getUniqueId(), ingamePlayer);
    	
    	Bukkit.getPluginManager().callEvent(new ResurrectionEvent(ingamePlayer));
    	
    	return ingamePlayer;
    }
    
    /**
     * Kills a player.
     * 
     * @param player
     */
    public void smite(Player player) {
    	Bukkit.getPluginManager().callEvent(new SmiteEvent(alive.remove(player.getUniqueId())));
    }

    void assignRoles(Collection<UUID> players) {
    	roles.clear();
    	alive.clear();
    	
    	List<UUID> toAssign = Lists.newArrayList(players);
        Integer neededSaboteurs = SabUtils.calcSabs(players.size());

        while(toAssign.size() > 0) {
        	UUID id = Sprink.randomElement(toAssign, true);
        	Player assignment = Bukkit.getPlayer(id);
        	
        	if(detective == null) {
        		detective = resurrect(assignment, Detective::new);
        		detective.sendRoleMessage(detective);
        	} 
        	
        	else if(neededSaboteurs > 0) {
        		Saboteur saboteur = resurrect(assignment, Saboteur::new);
        		saboteur.sendRoleMessage(detective);
        		neededSaboteurs--;
        	} 
        	
        	else {
        		Innocent innocent = resurrect(assignment, Innocent::new);
        		innocent.sendRoleMessage(detective);
        	}
        }
    }
    
    /**
     * Checks if a player is alive.
     * 
     * @param uuid
     * @return
     */
    public boolean isAlive(UUID uuid) {
    	return alive.containsKey(uuid);
    }
    
    /**
     * Returns main detective.
     * 
     * @return
     */
    public Detective getDetective() {
    	return detective;
    }
    
    /**
     * Get a player's role from their UUID.
     * 
     * @param uuid
     * @return
     */
    public IngamePlayer getRole(UUID uuid) {
    	return roles.get(uuid);
    }
}