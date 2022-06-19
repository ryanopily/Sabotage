package ml.sabotage.config;

import java.io.File;
import java.util.List;

import org.bukkit.inventory.ItemStack;

import com.google.common.collect.Lists;

import ml.sabotage.Main;
import ml.zer0dasho.config.Config;
import ml.zer0dasho.config.format.yaml.YAMLFormat;

public class InventoryData extends Config {

	protected InventoryData() {}
	
	public static InventoryData load() {
		return Config.load(
				InventoryData.class, 
				new File(Main.DATA_FOLDER, "inventory.yml"), 
				YAMLFormat.FORMATTER,
				() -> YAMLFormat.FORMATTER.write(new InventoryData()));
	}
	
	public List<ItemStack> inventory = Lists.newArrayList();
}
