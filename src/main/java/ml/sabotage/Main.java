package ml.sabotage;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import ml.sabotage.commands.*;
import ml.sabotage.config.ConfigSettings;
import ml.sabotage.game.SabPlayer;
import ml.sabotage.game.stages.Sabotage;
import ml.sabotage.utils.PlaceholderManager;
import ml.zer0dasho.plumber.game.arena.IArena;

public class Main extends JavaPlugin {
	
	public static Main plugin;
	public static Sabotage sabotage;
	public static ConfigSettings config;

	public static final String PLUGIN_NAME = "SABOTAGE";
    public static final String DATA_FOLDER = "plugins/Sabotage"; 
    public static String CurrentMap;

    public static final List<IArena> ACTIVE_ARENAS = Lists.newArrayList();
	public static final Map<UUID, SabPlayer> SAB_PLAYERS = Maps.newHashMap();
    
    public static void disablePlugin(String reason) {
    	Main.plugin.getLogger().log(Level.SEVERE, reason);
    	Bukkit.getPluginManager().disablePlugin(Main.plugin);
    }

	@Override
    public void onEnable() {    
    	Main.plugin = this;  
        Main.config = ConfigSettings.load();
        

        new File(DATA_FOLDER + "/players").mkdirs();
        new File(DATA_FOLDER + "/worlds").mkdirs();

		if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
			Main.plugin.getLogger().log(Level.INFO, "PlaceholderAPI found, enabling Placeholders!");
			new PlaceholderManager(this).register();
		}
			
        if(Validate.validate()) {
        	Main.sabotage = new Sabotage();
			addCommands();
			Bukkit.getOnlinePlayers().forEach(player -> Bukkit.getPluginManager().callEvent(new PlayerJoinEvent(player, "")));
        }
	}

    @Override
    public void onDisable() {
    	World main = Bukkit.getWorlds().get(0);
    	Lists.newArrayList(ACTIVE_ARENAS.iterator()).forEach(arena -> arena.deleteNow(main));
    }
    
    private void addCommands() {
    	new GenericCommands();
    	new SabMapCommand();
    	new ShopCommand();
    	new VoteCommand();
		new KarmaCommands();
    }
}