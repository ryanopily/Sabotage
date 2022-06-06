package ml.zer0dasho.plumber.utils.builders;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import ml.zer0dasho.plumber.utils.Sprink;

/**
 * Utility class for creating ItemStacks.
 * @author 0-o#9646
 */
public class ItemBuilder {

	private Material material;
	private int amount;
	private short durability;
	private String displayName;
	private List<String> lore;
	
	public ItemBuilder(Material material) {
		this.material = material;
		this.amount = 1;
		this.durability = (short) 0;
	}

	public ItemBuilder amount(int amount) {
		this.amount = amount;
		return this;
	}

	public ItemBuilder durability(short durability) {
		this.durability = durability;
		return this;
	}

	public ItemBuilder displayName(String displayName) {
		this.displayName = displayName;
		return this;
	}
	
	public ItemBuilder lore(String...lore) {
		return lore(Arrays.asList(lore));
	}

	public ItemBuilder lore(List<String> lore) {
		this.lore = lore;
		return this;
	}	
	
	public ItemStack create(boolean color) {
		ItemStack item = new ItemStack(material, amount, durability);

		ItemMeta meta = item.getItemMeta();
		Optional.ofNullable(displayName).ifPresent(Name -> meta.setDisplayName(color ? Sprink.color(Name) : Name));
		Optional.ofNullable(lore).ifPresent(Lore -> meta.setLore(color ? Sprink.color(Lore) : Lore));
		
		item.setItemMeta(meta);
		return item;
	}
}