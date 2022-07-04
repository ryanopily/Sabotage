package ml.sabotage.utils;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import ml.sabotage.Main;
import ml.sabotage.game.SabPlayer;
import ml.sabotage.game.stages.Collection;
import ml.sabotage.game.stages.Ingame;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class PlaceholderManager extends PlaceholderExpansion {

    public final Main plugin;

    public PlaceholderManager(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "Sabotage";
    }

    @Override
    public @NotNull String getAuthor() {
        return "AkiraDev";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        if(player == null)
            return null;
        SabPlayer sabPlayer = Main.SAB_PLAYERS.get(player.getUniqueId());
        if (sabPlayer == null) {
            return null;
        }
        if(params.equalsIgnoreCase("karma")){
            return String.valueOf(sabPlayer.getKarma());
        }
        if(params.equalsIgnoreCase("karma_total")){
            return String.valueOf(sabPlayer.getKarmaTotal());
        }
        if(params.equalsIgnoreCase("wins")){
            return String.valueOf(sabPlayer.getWins());
        }
        if(params.equalsIgnoreCase("losses")){
            return String.valueOf(sabPlayer.getLosses());
        }
        if(params.equalsIgnoreCase("kills")){
            return String.valueOf(sabPlayer.getKills());
        }
        if(params.equalsIgnoreCase("deaths")){
            return String.valueOf(sabPlayer.getDeaths());
        }
        if(params.equalsIgnoreCase("kills_correct")){
            return String.valueOf(sabPlayer.getRightKills());
        }
        if(params.equalsIgnoreCase("deaths_correct")){
            return String.valueOf(sabPlayer.getRightDeaths());
        }
        if(params.equalsIgnoreCase("kills_wrong")){
            return String.valueOf(sabPlayer.getWrongKills());
        }
        if(params.equalsIgnoreCase("deaths_wrong")){
            return String.valueOf(sabPlayer.getWrongDeaths());
        }
        if(params.equalsIgnoreCase("map")){
            return Main.CurrentMap;
        }
        return null; // Placeholder is unknown by the Expansion
    }

}
