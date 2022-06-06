package ml.sabotage.commands;

import static ml.sabotage.commands.Permissions.SHOP;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ml.sabotage.Main;
import ml.sabotage.game.roles.IngamePlayer;
import ml.sabotage.game.roles.SHOP;
import ml.sabotage.game.stages.Sabotage;
import ml.zer0dasho.plumber.utils.Sprink;

public class ShopCommand implements CommandExecutor {

	public ShopCommand() {
		Main.plugin.getCommand("shop").setExecutor(this);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		try {
			String cmd = String.join(" ", args);
						
			if(sender.hasPermission(SHOP)) {
				if(cmd.matches("\\d+$")) 
					buy((Player) sender, Integer.parseInt(args[0]));
				else 
					list((Player) sender);
			}
		} 
		catch(ClassCastException ex) {
			sender.sendMessage(Sprink.color("&cOnly players can use this command!"));
		} 
		catch(ArrayIndexOutOfBoundsException ex) {
			sender.sendMessage(Sprink.color("&cInvalid item... Try /shop!"));
		} 
		catch(Exception ex) {
			sender.sendMessage(Sprink.color("&cSomething went wrong..."));
			ex.printStackTrace();
		}
		
		return true;
	}
	
	/* Commands */
	
	/**
	 * Buys a shop item for the player.
	 * NOTE - This will use their karma.
	 * 
	 * @param player
	 * @param item
	 * @throws Exception
	 */
	public static void buy(Player player, Integer item) throws Exception {
        IngamePlayer ingamePlayer =  inIngame(player);
        
		if(ingamePlayer != null) {		
	        List<Method> shop = getAnnotatedMethods(ingamePlayer.getClass(), SHOP.class);
	        shop.get(item - 1).invoke(ingamePlayer, new Object[0]);
		}
	}
	
	/**
	 * Lists shop items for the player.
	 * 
	 * @param player
	 */
	public static void list(Player player) {
        IngamePlayer ingamePlayer = inIngame(player);
        
		if(ingamePlayer != null) {
	        List<Method> shop = getAnnotatedMethods(ingamePlayer.getClass(), SHOP.class);
	        
	        player.sendMessage(Sprink.color("&c&m--------&r &eShop &c&m---------"));
	        shop.forEach(method -> {
	        	int index = shop.indexOf(method) + 1;
	        	String name = method.getName().replaceAll("_", " ");
	        	player.sendMessage(Sprink.color("&3    ") + index + ". " + name);
	        });
	        player.sendMessage(Sprink.color("&c&m----------------------"));
		}
	}
	
	/* API */

    private static List<Method> getAnnotatedMethods(Class<?> clazz, Class<? extends Annotation> annotation0) {
        List<Method> methods = new ArrayList<Method>();
        
        for(Method method : clazz.getMethods()) {
        	Annotation annotation1 = method.getAnnotation(annotation0);
        	if(annotation1 != null) methods.add(method);
        }
        
        return methods;
    }
    
	private static IngamePlayer inIngame(Player player) {
		if(Main.sabotage.getCurrent_state() != Sabotage.INGAME) {
			player.sendMessage(Sprink.color("&cYou must be ingame!"));
			return null;
		}
		
		IngamePlayer alive = Main.sabotage.getIngame().getPlayerManager().getRole(player.getUniqueId());
		if(alive == null)
			player.sendMessage(Sprink.color("&cYou must be alive!"));
		
		return alive;
	}
}