package ml.sabotage.commands;

import static ml.sabotage.commands.Permissions.SABMAP_LOAD;
import static ml.sabotage.commands.Permissions.SABMAP_UNLOAD;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import ml.sabotage.Main;
import ml.sabotage.utils.SabUtils;
import ml.zer0dasho.plumber.utils.Sprink;
import ml.zer0dasho.plumber.utils.Trycat;

public class SabMapCommand implements CommandExecutor, TabCompleter {

	private static Map<String, World> loadedMaps = Maps.newHashMap();
	
    public SabMapCommand() {
    	Main.plugin.getCommand("sabmap").setExecutor(this);
    	Main.plugin.getCommand("sabmap").setTabCompleter(this);
    }
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		try {
			String cmd = String.join(" ", args);
			
			if(cmd.matches("load .+")) 
				load((Player) sender, args[1]);
			else if (cmd.matches("unload .+")) 
				unload((Player) sender, args[1]);
			else
				sendHelp(sender);
				
		} catch(ClassCastException ex) {
			sender.sendMessage(Sprink.color("&cOnly players can use this command!"));
		}
		
		return true;
	}
	
	private static final List<String> COMMANDS = Arrays.asList(
			"load", "unload");
	
	private static final List<String> PERMISSIONS = Arrays.asList(
			SABMAP_LOAD, SABMAP_UNLOAD);
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		String cmd = String.join(" ", args);
		List<String> result = Lists.newArrayList();

		for(int i = 0; i < COMMANDS.size(); i++) {
			String commandName = COMMANDS.get(i), permission = PERMISSIONS.get(i);
			
			if(commandName.startsWith(args[0]) && sender.hasPermission(permission)) {
				if(args.length <= 1)
					result.add(commandName);
				
				else if(cmd.matches(".+ .+"))
					result.addAll(
						Main.config.maps.stream()
						.filter(map -> map.startsWith(args[1]))
						.collect(Collectors.toList())
					);
				
				else if(cmd.matches(".+.*"))
					result.addAll(Main.config.maps);
			}
		}
		
		return result;
	}
	
	public static void load(Player sender, String worldName) {
		
		if(!loadedMaps.containsKey(worldName)) {
			File src = new File(SabUtils.SOURCE, worldName);
			File dest = new File(Bukkit.getWorldContainer(), UUID.randomUUID().toString());
			
			if(!src.exists())  {
				sender.sendMessage(Sprink.color("&cMap doesn't exist!"));
				return;
			}
			
			Trycat.Try(() -> {
				
				SabUtils.cleanMap(src);
				FileUtils.copyDirectory(src, dest);
				
				World arena = Bukkit.createWorld(new WorldCreator(dest.getName()));
				loadedMaps.put(worldName, arena);	
				
			}, (e) -> {
				sender.sendMessage(Sprink.color("&cSomething went wrong..."));
				e.printStackTrace();
			});
		}
		
		if(loadedMaps.containsKey(worldName))
			sender.teleport(loadedMaps.get(worldName).getSpawnLocation());
	}	
	
	public static void unload(CommandSender sender, String worldName) {
		World arena = loadedMaps.get(worldName);

		if(arena == null)
			sender.sendMessage(Sprink.color(String.format("&c%s isn't loaded", worldName)));
		
		else {
			loadedMaps.remove(worldName);
			
			World world = Bukkit.getWorlds().get(0);
			File srcFolder = arena.getWorldFolder();
			File destFolder = new File(SabUtils.SOURCE, worldName);
			
			arena.getPlayers().forEach(p -> p.teleport(world.getSpawnLocation()));
			Bukkit.unloadWorld(arena, true);
			
			new BukkitRunnable() {
				@Override
				public void run() {
					Trycat.Try(() -> {
						SabUtils.cleanMap(srcFolder);
						
						FileUtils.forceDelete(destFolder);
						FileUtils.copyDirectory(srcFolder, destFolder);
						FileUtils.forceDelete(srcFolder);
					},
					(e) -> {
						sender.sendMessage(Sprink.color("&cSomething went wrong..."));
						e.printStackTrace();
					});
				}
			}.runTaskLater(Main.plugin, 100L);
		}
	}
	
	private static void sendHelp(CommandSender sender) {
		StringBuilder result = new StringBuilder();
		result.append("&c&m----------&r &eSabotage X Help &c&m-----------\n");
		if(sender.hasPermission(SABMAP_LOAD))	result.append("&3/sabmap load [map] &8- &7Edit map.\n");
		if(sender.hasPermission(SABMAP_UNLOAD))	result.append("&3/sab unload [map] &8- &7Save map.\n");
		result.append("&c&m------------------------------------");
		sender.sendMessage(Sprink.color(result.toString()));
	}
}