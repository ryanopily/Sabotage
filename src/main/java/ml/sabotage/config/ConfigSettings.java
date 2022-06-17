package ml.sabotage.config;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.bukkit.inventory.ItemStack;

import com.google.common.collect.Lists;

import ml.sabotage.Main;
import ml.zer0dasho.config.Config;
import ml.zer0dasho.config.format.json.JSONFormat;
import ml.zer0dasho.plumber.RandomCollection;
import ml.zer0dasho.plumber.game.Timer;

public class ConfigSettings extends Config {
	
	protected ConfigSettings() {}
	
	public static ConfigSettings load() {
		return Config.load(
				ConfigSettings.class, 
				new File(Main.DATA_FOLDER + "/config.json"), 
				JSONFormat.FORMATTER, 
				() -> new String("format: \"json\"\n"));
	}
	
	public String hub = "hub";
	
	public List<Item> items = Lists.newArrayList();
	public List<Item> special_items = Lists.newArrayList();
	public List<String> maps = Lists.newArrayList();
	
	public Karma detective = new Karma(), 
				 innocent = new Karma(), 
				 saboteur = new Karma();
	
	public Time lobby = new Time(), 
				tester = new Time(), 
				refill = new Time(), 
				ingame = new Time(), 
				panic_life = new Time(),
				collection = new Time(), 
				corpse_tester = new Time();
	
	public int test_corpse_range = 10, max_panic_blocks = 5;
	
	public RandomCollection<List<ItemStack>> items() {
		return randomCollection(items);
	}
	
	public RandomCollection<List<ItemStack>> special_items() {
		return randomCollection(special_items);
	}
	
	private RandomCollection<List<ItemStack>> randomCollection(Collection<Item> items) {
		RandomCollection<List<ItemStack>> collect = new RandomCollection<>();
		items.forEach((item) -> collect.add(item.weight, item.items));
		return collect;
	}
	
	public static class Item {
		public double weight;
		public List<ItemStack> items;
		
		public Item() {}
		
		public Item(double weight, ItemStack...items) {
			this.weight = weight;
			this.items = Arrays.asList(items);
		}
	}
	
	public static class Karma {
		
		public int detective, innocent, saboteur, death;
	}

	public static class Time {
		
		public int hours, minutes, seconds, reload;
		
		public Timer getTimer() {
			return new Timer(null, hours, minutes, seconds);
		}
	}
}