package ml.zer0dasho.plumber.config;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.Map;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;

/**
 * Utility class for dealing with the YAML format
 * @author 0-o#9646
 */
public class YamlUtil {	
	
	/**
	 * Takes in a YAML stream and returns an object that represents it.
	 * Converts the YAML to JSON, and uses GSON to build the object.
	 * 
	 * @param input - YAML input stream
	 * @param type - Class to populate
	 * @return the populated object
	 */
	public static <T> T loadUsingGSON(InputStream input, Class<T> type) {
		return GSON.fromJson(
					GSON.toJson(
						YAML.loadAs(input, Map.class)), type);
	}
	
	/**
	 * Takes in an object, and returns a YAML string representing that object.
	 * Uses GSON & YAML internally.
	 * 
	 * @param object
	 * @return
	 */
	public static <T> String dumpUsingGSON(T object) {
		return YAML.dumpAsMap(
					GSON.fromJson(
							GSON.toJson(object), Map.class));
	}
	
	private static final DumperOptions OPTIONS = new DumperOptions() {
		{
		    setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
		    setPrettyFlow(true);
		    setIndent(4);
		}
	};
	
	public static final Gson GSON = new GsonBuilder()
			.setPrettyPrinting()
			.registerTypeHierarchyAdapter(ConfigurationSerializable.class, new ConfigurationSerializableAdapter())
			.create();
	
	public static final Yaml YAML = new Yaml(OPTIONS);	
	
	public static class ConfigurationSerializableAdapter implements JsonSerializer<ConfigurationSerializable>, JsonDeserializer<ConfigurationSerializable> {

	    final Type objectStringMapType = new TypeToken<Map<String, Object>>() {}.getType();

	    public ConfigurationSerializable deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException{
	        final Map<String, Object> map = new LinkedHashMap<>();

	        for (Map.Entry<String, JsonElement> entry : json.getAsJsonObject().entrySet()) {
	            final JsonElement value = entry.getValue();
	            final String name = entry.getKey();
	            
	            Object result = context.deserialize(value, Object.class);
	            map.put(name, result);
	        }

	       map.put(ConfigurationSerialization.SERIALIZED_TYPE_KEY, typeOfT.getTypeName());
	       ConfigurationSerializable result = ConfigurationSerialization.deserializeObject(map);
	       return result;
	    }

	    public JsonElement serialize(ConfigurationSerializable src, Type typeOfSrc, JsonSerializationContext context) {
	        final Map<String, Object> map = new LinkedHashMap<>();
	        map.putAll(src.serialize());
	        return context.serialize(map, objectStringMapType);
	    }
	}
}