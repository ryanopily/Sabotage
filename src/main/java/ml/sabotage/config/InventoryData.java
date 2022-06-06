package ml.sabotage.config;

import java.io.File;
import java.util.List;

import org.bukkit.inventory.ItemStack;

import com.google.common.collect.Lists;

import ml.sabotage.Main;
import ml.zer0dasho.plumber.config.Config;
import ml.zer0dasho.plumber.config.DataRW;

public class InventoryData extends Config {

	public InventoryData() {
		super(new File(Main.DATA_FOLDER, "inventory.yml"), DataRW.YAMLRW, null);
	}
	
	public List<ItemStack> inventory = Lists.newArrayList();
}
