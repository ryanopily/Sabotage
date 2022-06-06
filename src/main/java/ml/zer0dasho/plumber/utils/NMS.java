package ml.zer0dasho.plumber.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import org.bukkit.Bukkit;

import ml.zer0dasho.plumber.utils.Trycat.TryConsumer;

public class NMS {
	
	// https://bukkit.org/threads/getting-nms-classes-not-packets-with-reflection.195118/
	public static String getNMSVersion() {
		return  Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
	}

	// https://gist.github.com/daviga404/7322545
	public static Class<?> getNMSClass(String nmsClass) {
		return Trycat.Get(() -> Class.forName(String.format(nmsClass, getNMSVersion())), null);
	}
	
	// http://www.java2s.com/example/java/reflection/set-private-final-field.html
	public static boolean accessField(Class<?> clazz, String fieldName, TryConsumer<Field> tryI) {
	    return Trycat.Try(() -> {
	    	Field field = clazz.getField(fieldName);
	        field.setAccessible(true);

	        Field modifiersField = Field.class.getDeclaredField("modifiers");
	        modifiersField.setAccessible(true);
	        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
	        
	        tryI.Try(field);
	    	
	    }, (e) -> {
	    	e.printStackTrace(System.err);
	    });
	}
}
