package ml.sabotage.commands;

import static ml.sabotage.commands.Permissions.VOTE;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import ml.sabotage.Main;
import ml.sabotage.Validate;
import ml.sabotage.game.stages.Sabotage;
import ml.zer0dasho.plumber.utils.Sprink;
import ml.zer0dasho.plumber.utils.Trycat;

public class VoteCommand implements CommandExecutor {
	
    static final Map<UUID, String> VOTES = Maps.newHashMap();
	static final List<String> SELECTION = Lists.newArrayList();
	
    public VoteCommand() {
    	Main.plugin.getCommand("vote").setExecutor(this);
    }
    
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		try {
			String cmd = String.join(" ", args);
			
			if(sender.hasPermission(VOTE)) {
				if(cmd.matches("(1|2|3)$")) 
					vote(sender, Integer.parseInt(args[0]));
				else if(cmd.matches("reset.*") && sender.hasPermission(Permissions.VOTE_RESET)) 
					reset(sender);
				else 
					list(sender);
			}
		} 
		catch(Exception ex) {
			sender.sendMessage(Sprink.color("&cSomething went wrong..."));
			ex.printStackTrace();
		}
		
		return true;
	}

	/* Commands */
	
	/**
	 * Sender votes for the map corresponding the index 'vote - 1'.
	 * 
	 * @param sender
	 * @param vote
	 */
    public static void vote(CommandSender sender, Integer vote)  {
    	if(inLobby(sender)) {
	    	String selectedMap = SELECTION.get(vote - 1); 
	    	UUID playerId = Trycat.Get(() -> ((Player)sender).getUniqueId(), UUID.randomUUID());
	    		
	    	VOTES.put(playerId, selectedMap);
	    	list(sender);
    	}
    }
    
    /**
     * Resets the current map selection.
     * 
     * @param sender
     */
    public static void reset(CommandSender sender) {
    	if(inLobby(sender)) {
	    	VoteCommand.resetMapSelection();
	    	list(sender);
    	}
    }
	
    /**
     * Lists current votes to sender.
     * 
     * @param sender
     */
    public static void list(CommandSender sender) {
    	if(inLobby(sender))
    		listVotes(sender);
    }
    
    /* API */
    
    /**
     * Returns the most-voted map.
     * 
     * @return
     */
    public static String getMap() {
    	String map = Trycat.Get(() -> Sprink.mostFrequentValue(VOTES), SELECTION.get(0));
    	return Validate.validateMap(map) ? map : null;
    }
    
    /**
     * Resets the current map selection.
     * 
     */
    public static void resetMapSelection() {
        Validate.validateMaps();
        
        List<String> maps = new ArrayList<String>(Main.config.maps);
        SELECTION.clear();

        while(maps.size() > 0 && SELECTION.size() < 3) 
            SELECTION.add(Sprink.randomElement(maps, true));
    }
    
    private static boolean inLobby(CommandSender sender) {
    	if(Main.sabotage.getCurrent_state() != Sabotage.LOBBY) {
    		sender.sendMessage(Sprink.color("&cYou can only vote in lobby!"));
    		return false;
    	}
    	
    	return true;
    }
    
    private static boolean listVotes(CommandSender sender) {
        sender.sendMessage(Sprink.color("&c&m--------&r &eVotes &c&m---------"));
        
        SELECTION.forEach((map) -> {
        	int numVotes = Collections.frequency(VOTES.values(), map);
        	String cleanedName = map.replaceAll("_", " ");
        	sender.sendMessage(Sprink.color("    &3" + cleanedName + ": " + numVotes));
        });
        
        sender.sendMessage(Sprink.color("&c&m-----------------------"));
        return true;
    }
}