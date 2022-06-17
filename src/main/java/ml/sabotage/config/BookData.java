package ml.sabotage.config;

import java.io.File;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import ml.sabotage.Main;
import ml.zer0dasho.config.Config;
import ml.zer0dasho.config.format.json.JSONFormat;
import ml.zer0dasho.plumber.utils.Sprink;

public class BookData extends Config {

	protected BookData() {}
	
	public static BookData load() {
		return Config.load(
				BookData.class, 
				new File(Main.DATA_FOLDER + "/book.json"), 
				JSONFormat.FORMATTER, 
				() -> new String(Main.plugin.getResource("book.json").readAllBytes()));
	}
	
	public Book book = new Book();
	
	public static class Book {
		public String title;
		public String author;
		public List<String> pages;
	}
	
	public ItemStack getBook() {
		ItemStack result = new ItemStack(Material.WRITTEN_BOOK);
		BookMeta meta = (BookMeta) result.getItemMeta();

		meta.setAuthor(Sprink.color(book.author));
		meta.setTitle(Sprink.color(book.title));
		meta.setPages(Sprink.color(book.pages));
		
		result.setItemMeta(meta);
		return result;
	}
}
