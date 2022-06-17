package ml.sabotage.commands;

import static ml.sabotage.commands.Permissions.BUILD;
import static ml.sabotage.commands.Permissions.DEFAULT;
import static ml.sabotage.commands.Permissions.SAB_PAUSE;
import static ml.sabotage.commands.Permissions.SAB_RESURRECT;
import static ml.sabotage.commands.Permissions.SAB_START;
import static ml.sabotage.commands.Permissions.SAB_TEST;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import com.google.common.collect.Lists;

import ml.sabotage.Main;
import ml.sabotage.config.InventoryData;
import ml.sabotage.config.PlayerData;
import ml.sabotage.game.stages.Ingame;
import ml.sabotage.game.stages.Sabotage;
import ml.zer0dasho.plumber.utils.Sprink;

public class GenericCommands implements CommandExecutor, TabCompleter {

	public static boolean TEST, PAUSE;

    private static final String 
    	PREFIX    = "&3[SabotageX] &7- ",
		DEV_ON    = Sprink.color(PREFIX + "&aDeveloper mode enabled."),
		DEV_OFF   = Sprink.color(PREFIX + "&cDeveloper mode disabled."),
		BUILD_ON  = Sprink.color(PREFIX + "&aBuilder mode enabled."),
		BUILD_OFF = Sprink.color(PREFIX + "&cBuilder mode disabled."),
		PAUSED    = Sprink.color(PREFIX + "&cPaused."),
		UNPAUSED  = Sprink.color(PREFIX + "&aUnpaused."),
		INFO = Sprink.color(
			"&c&m----------&r &eSabotage X Info &c&m-----------\n" +
			"&3About: &7Sabotage is a Trouble in Terrorist Town plugin.\n" + 
			"&3Author: &70-o#9646\n" + 
			"&3Version: &71.0.0\n" + 
			"&3Credit to: &7ThaRedstoner\n" +
			"&c&m------------------------------------");
    
    public GenericCommands() {
    	Main.plugin.getCommand("sabotage").setExecutor(this);
    	Main.plugin.getCommand("sabotage").setTabCompleter(this);
    }
    
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		try {
			String cmd = String.join(" ", args);
	
			if(cmd.matches("(info|version).*")) 
				sender.sendMessage(INFO);
			else if(cmd.matches("leave.*")) 
				leave((Player) sender);
			else if(cmd.matches("join.*")) 
				join((Player) sender);
			else if(cmd.matches("start.*") && sender.hasPermission(SAB_START)) 
				start(sender, args.length >= 2 ? args[1] : null);
			else if(cmd.matches("test.*") && sender.hasPermission(SAB_TEST)) 
				test(sender, true);
			else if(cmd.matches("meta.*") && sender.hasPermission(SAB_TEST))
				meta((Player)sender);
			else if(cmd.matches("pause.*") && sender.hasPermission(SAB_PAUSE)) 
				pause(sender, true);
			else if(cmd.matches("build.*") && sender.hasPermission(BUILD)) 
				build((Player) sender, true);
			else if(cmd.matches("resurrect.*") && sender.hasPermission(SAB_RESURRECT)) 
				resurrect(sender, args.length >= 2 ? Bukkit.getPlayer(args[1]).getPlayer() : (Player)sender);
			else 
				sendHelp(sender);
		}
		catch(NullPointerException ex) {
			sender.sendMessage(Sprink.color("&cInvalid player!"));
		} 
		catch(ClassCastException ex) {
			sender.sendMessage(Sprink.color("&cOnly players can use this command!"));
		} 
		catch(Exception ex) {
			sender.sendMessage(Sprink.color("&cSomething went wrong..."));
			ex.printStackTrace();
		}
		
		return true;
	}
	
	
	private static final List<String> COMMANDS = Arrays.asList(
			"info", "version", "join", "leave", "start", "test", "pause", "build", "resurrect");
	
	private static final List<String> PERMISSIONS = Arrays.asList(
			DEFAULT, DEFAULT, DEFAULT, DEFAULT, SAB_START, SAB_TEST, SAB_PAUSE, BUILD, SAB_RESURRECT);
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		String cmd = String.join(" ", args);
		List<String> result = Lists.newArrayList();
		
		/* Suggest maps */
		if(sender.hasPermission(SAB_START)) {
			List<String> maps = Main.config.maps;
			if(cmd.matches("start .+"))
				result.addAll(maps.stream()
						.filter(map -> map.startsWith(args[1]))
						.collect(Collectors.toList()));
			
			else if(cmd.matches("start.*"))
				result.addAll(maps);
		}
		
		/* Suggest players */
		if(Main.sabotage.getCurrent_state() == Sabotage.INGAME && sender.hasPermission(SAB_RESURRECT)) {
			List<String> dead = Main.sabotage.getIngame().getPlayerManager().dead().values().stream()
					.map(igp -> igp.player.getName())
					.collect(Collectors.toList());
			
			if(cmd.matches("resurrect .+"))
				result.addAll(dead.stream()
						.filter(name -> name.startsWith(args[1]))
						.collect(Collectors.toList()));
			
			else if(cmd.matches("resurrect.*"))
				result.addAll(dead);
		}
		
		/* Suggest commands */
		if(result.size() == 0 && args.length <= 1) {
			for(int i = 0; i < COMMANDS.size(); i++) {
				String commandName = COMMANDS.get(i), permission = PERMISSIONS.get(i);
				
				if(commandName.startsWith(args[0]) && sender.hasPermission(permission))
					result.add(commandName);
			}
		}
		
		return result;
	}
	
	/* Commands */
	
	/**
	 * Initiates collection on selectedMap, or the first available option if null.
	 * 
	 * @param sender
	 * @param selectedMap
	 */
	public static void start(CommandSender sender, String selectedMap) {
		String option = selectedMap == null ? 
						VoteCommand.getMap() :
						Main.config.maps.stream().filter(map -> map.startsWith(selectedMap)).findFirst().get();
			
		if(option != null) {
				VoteCommand.VOTES.clear();
				VoteCommand.VOTES.put(UUID.randomUUID(), option);
				Main.sabotage.endLobby();
		}
			
		else 
			sender.sendMessage(Sprink.color("&cInvalid map!"));
	}
	
	/**
	 * Toggles developer mode. 
	 * If on, the game will never end, and karma will not be used on shop items.
	 * 
	 * @param sender
	 * @param toggle
	 */
	public static void test(CommandSender sender, boolean toggle) {
		TEST = toggle ? !TEST : TEST;
		sender.sendMessage(TEST ? DEV_ON : DEV_OFF);
	}
	
	/**
	 * Gets damage of held item.
	 * 
	 * @param sender
	 */
	public static void meta(Player player) {
		InventoryData inventoryData = InventoryData.load();
		inventoryData.inventory = Lists.newArrayList(player.getInventory().getContents());
		inventoryData.save();
	}
	
	/**
	 * Pauses all timers.
	 * 
	 * @param sender
	 * @param toggle
	 */
	public static void pause(CommandSender sender, boolean toggle) {
		PAUSE = toggle ? !PAUSE : PAUSE;
		sender.sendMessage(PAUSE ? PAUSED : UNPAUSED);
	}
	
	/**
	 * Toggles a player's ability to place blocks.
	 * 
	 * @param player
	 * @param toggle
	 */
	public static void build(Player player, boolean toggle) {
		PlayerData data = Main.SAB_PLAYERS.get(player.getUniqueId()).config;
		data.canBuild = toggle ? !data.canBuild : data.canBuild;
		data.save();
		
		player.sendMessage(data.canBuild ? BUILD_ON : BUILD_OFF);
	}
	
	/**
	 * Player opts-in the game.
	 * 
	 * @param player
	 */
	public static void join(Player player) {
		Main.sabotage.add(player);
		PlayerData data = Main.SAB_PLAYERS.get(player.getUniqueId()).config;
		data.autojoin = true;
		data.save();
	}
	
	/**
	 * Player opts-out the game.
	 * 
	 * @param player
	 */
	public static void leave(Player player) {
		Main.sabotage.remove(player);
		PlayerData data = Main.SAB_PLAYERS.get(player.getUniqueId()).config;
		data.autojoin = false;
		data.save();
	}

	/**
	 * Brings a player back from the dead during ingame.
	 * 
	 * @param sender
	 * @param player
	 */
	public static void resurrect(CommandSender sender, Player player) {
		if(Main.sabotage.getCurrent_state() != Sabotage.INGAME) {
			sender.sendMessage(Sprink.color("&cYou aren't ingame!"));
			return;
		}
		
		Ingame ingame = Main.sabotage.getIngame();
		
		if(ingame.getPlayerManager().isAlive(player.getUniqueId())) {
			sender.sendMessage(Sprink.color("&cPlayer is already alive!"));
			return;
		}
		
		ingame.getPlayerManager().resurrect(player, false);
	}
	
	/* API */
    
	private static void sendHelp(CommandSender sender) {
		StringBuilder result = new StringBuilder();
		result.append("&c&m----------&r &eSabotage X Help &c&m-----------\n");
		result.append("&3/sab info &8- &7Returns plugin and server information.\n");
		result.append("&3/sab join &8- &7Join sabotage.\n");
		result.append("&3/sab leave &8- &7Leave sabotage.\n");
		if(sender.hasPermission(BUILD))			result.append("&3/sab build &8- &7Toggle builder mode.\n");
		if(sender.hasPermission(SAB_TEST))		result.append("&3/sab test &8- &7Toggle developer mode.\n");
		if(sender.hasPermission(SAB_PAUSE))		result.append("&3/sab pause &8- &7Toggle pause.\n");
		if(sender.hasPermission(SAB_START))		result.append("&3/sab start &8- &7Force start the game on voted map.\n");
		if(sender.hasPermission(SAB_START))		result.append("&3/sab start [map] &8- &7Force start the game on specified map.\n");
		if(sender.hasPermission(SAB_RESURRECT))	result.append("&3/sab resurrect [player] &8- &7Bring back a specified player.\n");
		result.append("&c&m------------------------------------");
		sender.sendMessage(Sprink.color(result.toString()));
	}
}