package ml.zer0dasho.plumber.config;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

/**
 * Read and write objects in a specific format.
 * @author 0-o#9646
 */
public interface DataRW {

	/**
	 * Format an object and write it out to a stream.
	 * @param out    - Output stream
	 * @param object - Object to write
	 */
	public <T> void write(OutputStream out, T object) throws IOException;
	
	/**
	 * Read a formatted stream as an object.
	 * @param data - Input stream
	 * @param type - Target object type
	 * @return
	 */
	public <T> T read(InputStream data, Class<T> type) throws IOException;
	
	static final DataRW JSONRW = new DataRW() {

		@Override
		public <T> void write(OutputStream out, T object) throws IOException {
			out.write(YamlUtil.GSON.toJson(object).getBytes());
			out.flush();
		}

		@Override
		public <T> T read(InputStream data, Class<T> type) throws IOException {
			return YamlUtil.GSON.fromJson(new InputStreamReader(data), type);
		}
		
	};
	
	static final DataRW YAMLRW = new DataRW() {

		@Override
		public <T> void write(OutputStream out, T object) throws IOException {
			out.write(YamlUtil.dumpUsingGSON(object).getBytes());
			out.flush();
		}

		@Override
		public <T> T read(InputStream data, Class<T> type) throws IOException {
			return YamlUtil.loadUsingGSON(data, type);
		}
		
	};
}