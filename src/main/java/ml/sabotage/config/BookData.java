package ml.sabotage.config;

import java.io.File;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import ml.sabotage.Main;
import ml.zer0dasho.config.Config;
import ml.zer0dasho.config.format.yaml.YAMLFormat;

public class BookData extends Config {

	protected BookData() {}
	
	public static BookData load() {
		return Config.load(
				BookData.class, 
				new File(Main.DATA_FOLDER + "/book.yml"), 
				YAMLFormat.FORMATTER, 
				() -> new String(Main.plugin.getResource("book.yml").readAllBytes()));
	}
	
	public ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
}
