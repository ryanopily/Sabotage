package ml.sabotage.game.tasks;

import ml.sabotage.Main;
import ml.zer0dasho.plumber.game.Timer;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import static ml.sabotage.utils.NPCManager.createFakePlayer;

public class Illusion extends BukkitRunnable {

    public NPC illusion;
    public final Timer life;

    public Illusion(Player p) {

        this.life = Main.config.mirror_illusion.getTimer();
        illusion = createFakePlayer(p.getName(), p.getLocation());

    }

    @Override
    public void run() {
        if(life.tick())
            this.stop();
    }

    public void stop() {
        illusion.destroy();
        this.cancel();
    }
}
