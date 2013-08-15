package com.guokr.simbase;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Date;
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
	private static final long cloneInterval = 30000;

	private ExecutorService service = Executors.newSingleThreadExecutor();
	private final SimTable table = new SimTable();
	private final Kryo kryo = new Kryo();
	private int counter = 0;
	private long timestamp = -1;

	/**
	 * clone 函数之前必须验证
	 * 
	 * @return cloneInterval 秒内clone过，则返回false,否则true
	 **/
	private boolean validateTime() {
		long current = new Date().getTime();
		if (current - timestamp < cloneInterval) {
			logger.info("Already cloned in " + cloneInterval / 1000
					+ "s, abort;");
			return false;
		} else {
			timestamp = current;
			return true;
		}
	}

	public void load(final String key) throws FileNotFoundException {
		Input input = null;
		String path = dir + "/data/" + key;

		try {
			logger.info("Loading....");
			input = new Input(new FileInputStream(path + ".dmp"));
			table.read(kryo, input);
			logger.info("Load finish");
		} catch (KryoException e) {
			input = new Input(new FileInputStream(path + ".bak"));
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
				logger.info("Clean begin...");
				if (validateTime()) {
					SimTable data = table.clone();
					table.reload(data);
					data = null;
					System.gc();
				}
				logger.info("Clean finish!");
			}
		});
	}

	public void save(final String key) {
		service.execute(new Runnable() {
			public void run() {

				Runnable runner = new Runnable() {
					@Override
					public void run() {
						logger.info("Saving....");
						if (!validateTime()) {
							return;
						}
						SimTable data = table.clone();
						Output output = null;
						String path = dir + "/data/" + key;
						try {
							Process p = Runtime.getRuntime().exec(
									"mv " + path + ".dmp " + path + ".bak");
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
						data = null;
						System.gc();
						logger.info("Save finish");
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
					logger.info("Being delete " + docid);
					table.delete(docid);
					logger.info("Delete finish");
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
