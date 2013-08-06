package com.guokr.simbase;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wahlque.net.action.ActionRegistry;
import org.wahlque.net.server.Server;

import com.guokr.simbase.action.AddAction;
import com.guokr.simbase.action.DelAction;
import com.guokr.simbase.action.ExitAction;
import com.guokr.simbase.action.GetAction;
import com.guokr.simbase.action.PingAction;
import com.guokr.simbase.action.PutAction;
import com.guokr.simbase.action.SaveAction;
import com.guokr.simbase.action.ShutdownAction;

public class SimBase {

	private static final String dir = System.getProperty("user.dir")
			+ System.getProperty("file.separator");
	private static final String idxFilePath = dir + "keys.idx";
	private static final Logger logger = LoggerFactory.getLogger(SimBase.class);
	private static final long timeInterval = 120000L;

	private Map<String, SimEngine> base = new HashMap<String, SimEngine>();

	public SimBase() throws IOException {
		this.load();// 新建时加载磁盘数据
		this.cron();// 设置定时任务
	}

	public void load() {// 只有全局读取的时候读取文件里的map
		try {
			BufferedReader input = new BufferedReader(new FileReader(
					idxFilePath));
			String[] keys = input.readLine().split("\\|");
			for (String key : keys) {
				logger.info("Loading key-- " + key);// 只有存储才有多进程的情况
				this.load(key);
			}
			input.close();
		} catch (FileNotFoundException e) {
			logger.warn("Backup file not found.Do you have Backup?");
			return;
		} catch (Throwable e) {
			throw new SimBaseException(e);
		}
	}

	public void load(String key) {
		if (!base.containsKey(key)) {
			base.put(key, new SimEngine());
		}
		try {
			base.get(key).load(key);
		} catch (FileNotFoundException e) {
			logger.warn("File not found,do you have saved?");
			return;
		} catch (Throwable e) {
			throw new SimBaseException(e);
		}
	}

	public void save() {// 只有全局保存的时候把map写到文件里
		FileWriter output;
		try {
			output = new FileWriter(idxFilePath);
			String keys = "";
			if (!base.keySet().isEmpty()) {
				for (String key : base.keySet()) {
					keys += key + "|";
					logger.info("Push task:Save key-- " + key + " to queue");
					this.save(key);
					logger.info("Push finish");
				}
			}
			keys = keys.substring(0, keys.length() - 1);
			output.write(keys, 0, keys.length());
			output.close();
		} catch (Throwable e) {
			throw new SimBaseException(e);
		}
	}

	public void save(String key) {
		if (!base.containsKey(key)) {
			base.put(key, new SimEngine());
		}
		try {
			base.get(key).save(key);
		} catch (Throwable e) {
			throw new SimBaseException(e);
		}
	}

	public void delete(String key, int docid) {
		try {
			base.get(key).delete(docid);
		} catch (Throwable e) {
			throw new SimBaseException(e);// 如果没有键值直接抛错
		}
	}

	public void add(String key, int docid, float[] distr) {
		if (!base.containsKey(key)) {
			base.put(key, new SimEngine());
		}
		base.get(key).add(docid, distr);
	}

	public void update(String key, int docid, float[] distr) {
		if (!base.containsKey(key)) {
			base.put(key, new SimEngine());
		}
		base.get(key).update(docid, distr);
	}

	public void cron() {
		// 创建一个cron任务
		Timer cron = new Timer();
		TimerTask task = new TimerTask() {
			public void run() {
				save();
			}

		};
		cron.schedule(task, timeInterval, timeInterval);
	}

	public SortedSet<Map.Entry<Integer, Float>> retrieve(String key, int docid) {
		SortedSet<Map.Entry<Integer, Float>> result = null;
		if (base.containsKey(key)) {
			result = base.get(key).retrieve(docid);
		} else {
			result = SimTable
					.entriesSortedByValues(new TreeMap<Integer, Float>());
		}
		return result;
	}

	public static void main(String[] args) throws IOException {
		try {
			Map<String, Object> context = new HashMap<String, Object>();
			context.put("debug", true);

			SimBase db = new SimBase();
			context.put("simbase", db);

			ActionRegistry registry = ActionRegistry.getInstance();
			registry.register(PingAction.class);
			registry.register(AddAction.class);
			registry.register(PutAction.class);
			registry.register(GetAction.class);
			registry.register(SaveAction.class);
			registry.register(ExitAction.class);
			registry.register(ShutdownAction.class);
			registry.register(DelAction.class);

			Server server = new Server(context, registry);

			server.run(7654);
		} catch (Throwable e) {
			logger.error("Server Error!", e);
			System.exit(-1);
		}

	}
}
