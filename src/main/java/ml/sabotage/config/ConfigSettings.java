package ml.sabotage.config;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.bukkit.inventory.ItemStack;

import com.google.common.collect.Lists;

import ml.sabotage.Main;
import ml.zer0dasho.plumber.RandomCollection;
import ml.zer0dasho.plumber.config.Config;
import ml.zer0dasho.plumber.config.DataRW;
import ml.zer0dasho.plumber.game.Timer;

public class ConfigSettings extends Config {
	
	public ConfigSettings() {
		super(new File(Main.DATA_FOLDER + "/config.yml"), DataRW.YAMLRW, Main.plugin.getResource("config.yml"));
	}
	
	public String hub = "hub";
	
	private List<Item> items = Lists.newArrayList();
	private List<Item> special_items = Lists.newArrayList();
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