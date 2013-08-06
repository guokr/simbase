package com.guokr.simbase;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Map;
import java.util.SortedSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class SimEngine {

	private ExecutorService service = Executors.newSingleThreadExecutor();
	private final SimTable table = new SimTable();
	private Kryo kryo = new Kryo();
	private String dir = System.getProperty("user.dir")
			+ System.getProperty("file.separator");
	private static final Logger logger = LoggerFactory
			.getLogger(SimEngine.class);

	public void load(final String key) throws FileNotFoundException {
		Input input = null;
		String path = dir + key + ".dmp";

		try {
			input = new Input(new FileInputStream(path));
			table.read(kryo, input);
			// kryo.readObject(input, SimTable.class);
			// table.reload(newTable);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			if (input != null) {
				input.close();
			}
		}
	}

	public void save(final String key) throws FileNotFoundException {
		service.execute(new Runnable() {
			private final SimTable data = table.clone();

			public void run() {
				Runnable runner = new Runnable() {
					@Override
					public void run() {

						Output output = null;
						String path = dir + key + ".dmp";
						try {
							output = new Output(new FileOutputStream(path));
							// kryo.writeObject(output, data);
							data.write(kryo, output);
						} catch (Throwable e) {
							throw new SimBaseException(e);
						} finally {
							if (output != null) {
								output.close();
							}
						}
					}
				};
				new Thread(runner).start();
			}
		});
	}

	public void add(final int docid, final float[] distr) {
		service.execute(new Runnable() {
			public void run() {
				try {
					table.add(docid, distr);
				} catch (Throwable e) {
					logger.error("SimEngine Error:", e);
				}
			}
		});
	}

	public void update(final int docid, final float[] distr) {
		service.execute(new Runnable() {
			public void run() {
				try {
					table.update(docid, distr);
				} catch (Throwable e) {
					logger.error("SimEngine Error:", e);
				}
			}
		});
	}

	public void delete(final int docid) {
		service.execute(new Runnable() {
			public void run() {
				try {
					table.delete(docid);
				} catch (Throwable e) {
					logger.error("SimEngine Error:", e);
				}
			}
		});
	}

	public SortedSet<Map.Entry<Integer, Float>> retrieve(int docid) {
		return table.retrieve(docid);
	}

}
