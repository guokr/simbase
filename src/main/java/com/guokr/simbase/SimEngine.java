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
import com.esotericsoftware.kryo.KryoException;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class SimEngine {

	private static String dir = System.getProperty("user.dir")
			+ System.getProperty("file.separator");
	private static final Logger logger = LoggerFactory
			.getLogger(SimEngine.class);

	private ExecutorService service = Executors.newSingleThreadExecutor();
	private final SimTable table = new SimTable();
	private final Kryo kryo = new Kryo();
	private int counter = 0;

	public void load(final String key) throws FileNotFoundException {
		Input input = null;
		String path = dir + "/backup/" + key;

		try {
			input = new Input(new FileInputStream(path + ".dmp"));
			table.read(kryo, input);
		} catch (KryoException e) {
			input = new Input(new FileInputStream(path + ".dmp.backup"));
			table.read(kryo, input);
		} finally {
			if (input != null) {
				input.close();
			}
		}
	}

	public void clear() {
		service.execute(new Runnable() {
			public void run() {
				final SimTable data = table.clone();
				table.reload(data);
			}
		});
	}

	public void save(final String key) throws FileNotFoundException {
		service.execute(new Runnable() {
			public void run() {
				final SimTable data = table.clone();
				Runnable runner = new Runnable() {
					@Override
					public void run() {
						Output output = null;
						String path = dir + "/backup/" + key;
						try {
							Process p = Runtime.getRuntime().exec(
									"cp " + path + ".dmp " + path
											+ ".dmp.backup");
							p.waitFor();
							output = new Output(new FileOutputStream(path
									+ ".dmp"));
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
				counter++;
				if (counter % 100 == 0) {
					logger.info("add:" + counter);
				}
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
