package ml.sabotage.game;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import ml.sabotage.Main;

public class SharedListener {

    public static void droppedShears(PlayerDropItemEvent e) {
        if(e.getItemDrop().getItemStack().getType().equals(Material.SHEARS)) 
        	e.setCancelled(true);    
    }
    
    
    public static void rightClickBlock(PlayerInteractEvent e) {
    	if(e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
    		
    		switch(e.getClickedBlock().getType()) {
   	    		case CHEST:
	    			if(e.getClickedBlock().hasMetadata("rigged_chest")) 
	    				explode(e.getClickedBlock());
	            	else 
	            		openChest(e, Main.config.items().next());
	    			break;
	    			
	    		case ENDER_CHEST:
	    			openChest(e, Main.config.special_items().next());
	    			break;
	    			
	    		default:
	    			break;
    		}
        }
    }
    
    public static void onBlockPlace(BlockPlaceEvent e) {
    	
        switch(e.getBlock().getType()) {
	        case TNT:
	        	SharedListener.explode(e.getBlock());
	        	return;
	        	
	        case CHEST:
	        	 e.getBlock().setMetadata("rigged_chest", new FixedMetadataValue(Main.plugin,"explode"));
	        	 return;
	        	 
	        default:
	        	break;
        }
        
        if(!Main.SAB_PLAYERS.get(e.getPlayer().getUniqueId()).config.canBuild) 
        	e.setCancelled(true);
    }

    public static void openChest(PlayerInteractEvent e, List<ItemStack> items) {
        e.getClickedBlock().setType(Material.AIR);
        e.getPlayer().getInventory().addItem(items.toArray(new ItemStack[0]));
    }
    
    public static void explode(Block block, int ticks) {
        block.setType(Material.AIR);
        
        TNTPrimed tntprimed = (TNTPrimed)block.getLocation().getWorld().spawn(block.getLocation(), TNTPrimed.class);
        tntprimed.setFuseTicks(ticks);
        tntprimed.setIsIncendiary(true);
    }
    
    public static void explode(Block block) {
    	explode(block, 40);
    }
}