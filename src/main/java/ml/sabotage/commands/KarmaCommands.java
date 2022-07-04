package ml.sabotage.commands;

import com.google.common.collect.Lists;
import ml.sabotage.Main;
import ml.sabotage.game.SabPlayer;
import ml.zer0dasho.plumber.utils.Sprink;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

import static ml.sabotage.commands.Permissions.*;

public class KarmaCommands implements CommandExecutor, TabCompleter {


    private static final String
            PREFIX    = "&3[SabotageX] &7- ";

    public KarmaCommands() {
        Main.plugin.getCommand("karma").setExecutor(this);
        Main.plugin.getCommand("karma").setTabCompleter(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        try{
            String cmd = String.join(" ", args);
            if(cmd.matches("add .+"))
                addKarma(sender, args[1], Integer.parseInt(args[2]));
            else if(cmd.matches("set .+"))
                setKarma(sender, args[1], Integer.parseInt(args[2]));
            else if(cmd.matches("get .+"))
                getKarma(sender, args[1]);
            else if(cmd.matches("reset.*"))
                resetKarma(sender, args[1]);
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
            "add", "set", "get", "reset");

    private static final List<String> PERMISSIONS = Arrays.asList(
            KARMA_ADD, KARMA_SET, KARMA_GET, KARMA_RESET);

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> result = Lists.newArrayList();

        /* Suggest commands */
        if(result.size() == 0 && args.length <= 1) {
            for(int i = 0; i < COMMANDS.size(); i++) {
                String commandName = COMMANDS.get(i), permission = PERMISSIONS.get(i);

                if(commandName.startsWith(args[0]) && sender.hasPermission(permission))
                    result.add(commandName);
            }
        }

        /* Suggest players */
        if(result.size() == 0 && args.length == 2) {
            for(Player player : Bukkit.getOnlinePlayers()) {
                if(player.getName().startsWith(args[1]))
                    result.add(player.getName());
            }
        }

        if(result.size() == 0 && args.length == 3 && !args[0].equalsIgnoreCase("reset")) {
            result.add("[number]");
        }
        return result;
    }

    /* Commands */

    /**
     * get karma of player
     *
     * @param sender - The player who initiated the command.
     * @param player the player to get the karma of.
     */
    public static void getKarma(CommandSender sender, String player) {
        Player p = Bukkit.getPlayer(player);
        if(p == null) {
            sender.sendMessage(Sprink.color(PREFIX + "Player not found!"));
            return;
        }
        sender.sendMessage(Sprink.color(PREFIX + "Karma of " + player + ": " + Main.SAB_PLAYERS.get(p.getUniqueId()).getKarma()));
    }

    /**
     * set karma of player
     *
     * @param sender - The player who initiated the command.
     * @param player the player to set the karma of.
     * @param karma the karma to set.
     */
    public static void setKarma(CommandSender sender, String player, int karma) {
        Player p = Bukkit.getPlayer(player);
        if(p == null) {
            sender.sendMessage(Sprink.color(PREFIX + "Player not found!"));
            return;
        }
        Main.SAB_PLAYERS.get(p.getUniqueId()).setKarma(karma);
        sender.sendMessage(Sprink.color(PREFIX + "Karma of " + player + " set to " + karma));
    }

    /**
     * add karma to player
     *
     * @param sender - The player who initiated the command.
     * @param player the player to add the karma to.
     * @param karma the karma to add.
     */
    public static void addKarma(CommandSender sender, String player, int karma) {
        Player p = Bukkit.getPlayer(player);
        if(p == null) {
            sender.sendMessage(Sprink.color(PREFIX + "Player not found!"));
            return;
        }
        Main.SAB_PLAYERS.get(p.getUniqueId()).addKarma(karma);
        sender.sendMessage(Sprink.color(PREFIX + "Karma of " + player + " increased by " + karma));
    }

    /**
     * reset karma of player
     *
     * @param sender - The player who initiated the command.
     * @param player the player to reset the karma of.
     */
    public static void resetKarma(CommandSender sender, String player) {
        Player p = Bukkit.getPlayer(player);
        if(p == null) {
            sender.sendMessage(Sprink.color(PREFIX + "Player not found!"));
            return;
        }
        Main.SAB_PLAYERS.get(p.getUniqueId()).resetKarma();
        sender.sendMessage(Sprink.color(PREFIX + "Karma of " + player + " reset"));
    }

    /* API */

    private static void sendHelp(CommandSender sender) {
        StringBuilder result = new StringBuilder();
        result.append("&c&m----------&r &eSabotage X Help &c&m-----------\n");
        if(sender.hasPermission(KARMA_ADD))	    result.append("&3/karma add [player] [karma] &8- &7Add karma to player.\n");
        if(sender.hasPermission(KARMA_SET))	    result.append("&3/karma set [player] [karma] &8- &7Set karma of player.\n");
        if(sender.hasPermission(KARMA_GET))	    result.append("&3/karma get [player] &8- &7Get karma of player.\n");
        if(sender.hasPermission(KARMA_RESET))	result.append("&3/karma reset [player] &8- &7Reset karma of player.\n");
        result.append("&c&m------------------------------------");
        sender.sendMessage(Sprink.color(result.toString()));
    }

}
