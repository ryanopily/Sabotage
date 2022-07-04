package ml.sabotage.utils;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.ai.goals.WanderGoal;
import net.citizensnpcs.api.event.SpawnReason;
import net.citizensnpcs.api.npc.MemoryNPCDataStore;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import net.citizensnpcs.api.trait.trait.Equipment;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.mcmonkey.sentinel.SentinelTrait;
import org.mcmonkey.sentinel.integration.SentinelHealth;

public class NPCManager {

    private static final NPCRegistry registry = CitizensAPI.createAnonymousNPCRegistry(new MemoryNPCDataStore());

    public static NPC createFakePlayer(String playerName, Location location) {
        Player player = Bukkit.getPlayer(playerName);
        ItemStack helmet = player.getEquipment().getHelmet();
        ItemStack chestplate = player.getEquipment().getChestplate();
        ItemStack leggings = player.getEquipment().getLeggings();
        ItemStack boots = player.getEquipment().getBoots();
        ItemStack currentItem = player.getEquipment().getItem(EquipmentSlot.HAND);

        NPC npc = registry.createNPC(EntityType.PLAYER, playerName);

        npc.getOrAddTrait(Equipment.class).set(Equipment.EquipmentSlot.HELMET, helmet);
        npc.getOrAddTrait(Equipment.class).set(Equipment.EquipmentSlot.CHESTPLATE, chestplate);
        npc.getOrAddTrait(Equipment.class).set(Equipment.EquipmentSlot.LEGGINGS, leggings);
        npc.getOrAddTrait(Equipment.class).set(Equipment.EquipmentSlot.BOOTS, boots);
        npc.getOrAddTrait(Equipment.class).set(Equipment.EquipmentSlot.HAND, currentItem);
        
        npc.getOrAddTrait(SentinelTrait.class);
        WanderGoal runaround = WanderGoal.builder(npc).xrange(30).build();
        npc.getDefaultGoalController().addGoal(runaround, 1);
        npc.getNavigator().getDefaultParameters().baseSpeed(1.5f).stuckAction(null);
        npc.spawn(location, SpawnReason.PLUGIN);

        return npc;
    }
}
