package ml.zer0dasho.plumber.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.FileUtils;

/**
 * Utility for working with configuration files.
 * Subclasses should include fields corresponding to the underlying configuration file.
 * 
 * @author 0-o#9646
 */
public abstract class Config {
	
	protected transient File file;
	protected transient DataRW format;
	protected transient InputStream defaultResource;
	
	/**
	 * @param file - Configuration file
	 * @param format - File format
	 * @param defaultResource - Default file contents
	 */
	protected Config(File file, DataRW format, InputStream defaultResource) {
		this.file = file;
		this.format = format;
		this.defaultResource = defaultResource;
	}
	
	/**
	 * Returns a Config object.
	 * Creates default configuration file if it doesn't exist.
	 * 
	 * @param config - Blank instance of Config object
	 * @param unsafe - return null on error
	 */
	public static <T extends Config> T create(T config, Class<T> type, boolean unsafe) {
		try {
			
			boolean writeDefaults = !config.file.exists();
			
			if(writeDefaults) {
				config.file.getParentFile().mkdirs();
				config.file.createNewFile();	
			}
			
			if(config.defaultResource != null) {
				if(writeDefaults)
					FileUtils.copyInputStreamToFile(config.defaultResource, config.file);
				config.defaultResource.close();
			}
			
			else if(writeDefaults){
				FileOutputStream fos = new FileOutputStream(config.file);
				config.format.write(fos, config);
				fos.close();
			}

			return get(config, type, unsafe);
		} 
		catch (IOException ex) {
			System.err.println(String.format("Failed to create configuration file '%s'...", config.file.getPath()));
			ex.printStackTrace(System.err);
		}
		catch (NullPointerException ex) {
			System.err.println("[Plumber] Unspecified options. Make sure file & format are not null.");
			ex.printStackTrace(System.err);
		}
		
		return unsafe ? null : config;
	}

	/**
	 * Returns a Config object.
	 * 
	 * @param config - Blank instance of Config Object
	 * @param unsafe - return null on error
	 * @return
	 */
	public static <T extends Config> T get(T config, Class<T> type, boolean unsafe) {
		try(InputStream is = new FileInputStream(config.file)) {
			T result = (T) config.format.read(is, type);
			
			if(result != null) {
				result.file = config.file;
				result.format = config.format;
				return result;
			}
		}
		catch (FileNotFoundException ex) {
			System.err.println(String.format("[Plumber] Configuration file '%s' not found...", config.file.getPath()));
			ex.printStackTrace(System.err);
		} 
		catch (IOException ex) {
			System.err.println(String.format("[Plumber] Failed to get configuration file '%s'...", config.file.getPath()));
			ex.printStackTrace(System.err);
		}		
		catch(NullPointerException ex) {
			System.err.println("[Plumber] Unspecified options. Make sure file & format are not null.");
			ex.printStackTrace(System.err);
		}
		
		return unsafe ? null : config;
	}
	 
	 /**
	  * Update configuration settings.
	  * @param data - Updated settings
	  */
	 public void save() {
		 try(OutputStream os = new FileOutputStream(file)) {
			format.write(os, this);
		 }
		 catch (FileNotFoundException e) {
			System.err.println(String.format("[Plumber] Configuration file '%s' not found...", file.getPath()));
			e.printStackTrace(System.err);
		 } 
		 catch (IOException e) {
			System.err.println(String.format("[Plumber] Failed to save configuration file '%s'...", file.getPath()));
			e.printStackTrace(System.err);
		 }
		catch(NullPointerException ex) {
			System.err.println("[Plumber] Unspecified options. Make sure file & format are not null.");
			ex.printStackTrace(System.err);
		}	
	 }
}