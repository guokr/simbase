package com.guokr.simbase;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeMap;

import org.wahlque.net.action.ActionRegistry;
import org.wahlque.net.server.Server;

import com.guokr.simbase.action.AddAction;
import com.guokr.simbase.action.ExitAction;
import com.guokr.simbase.action.GetAction;
import com.guokr.simbase.action.PingAction;
import com.guokr.simbase.action.PutAction;
import com.guokr.simbase.action.SaveAction;
import com.guokr.simbase.action.ShutdownAction;


public class SimBase {

	private Map<String, SimEngine> base = new HashMap<String, SimEngine>();
	private String dir = System.getProperty("user.dir")
			+ System.getProperty("file.separator");
	
	public SimBase() throws IOException {
		this.load();//新建时加载磁盘数据
	}
	public void load() {//只有全局读取的时候读取文件里的map
		String path = dir + "hashmap.dmp" ;
		try {
			BufferedReader input = new BufferedReader(new FileReader(path));
			String [] keys = input.readLine().split("\\|");
			for (String key : keys){
				System.out.println("loading key:"+key);
				this.load(key);
			}
			input.close();
		}
		catch (FileNotFoundException e){
			
		}
		catch (IOException e){
			e.printStackTrace();
		}
	}	
	public void load(String key) {
		if (!base.containsKey(key)) {
			base.put(key, new SimEngine());
		}
		try {
			base.get(key).load(key);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public void save() {//只有全局保存的时候把map写到文件里
		
		String path = dir + "hashmap.dmp" ;
		FileWriter output;
		try {
			output = new FileWriter(path);
			String keys = "";
			for (String key : base.keySet()){
				keys += key + "|";
				System.out.println("saving key:"+key);
				this.save(key);
				System.out.println("save finish");
			}
			keys = keys.substring(0, keys.length()-1);
			output.write(keys,0,keys.length());
			output.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}	

	public void save(String key) {
		if (!base.containsKey(key)) {
			base.put(key, new SimEngine());
		}
		try {
			base.get(key).save(key);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
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

		Server server = new Server(context, registry);
		server.run(7654);
	}
}
