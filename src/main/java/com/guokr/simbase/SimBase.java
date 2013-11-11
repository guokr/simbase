package com.guokr.simbase;

import gnu.trove.list.TFloatList;
import gnu.trove.list.array.TFloatArrayList;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wahlque.net.action.ActionRegistry;
import org.wahlque.net.server.Server;
import org.yaml.snakeyaml.Yaml;

import com.guokr.simbase.action.AddAction;
import com.guokr.simbase.action.AppendAction;
import com.guokr.simbase.action.DelAction;
import com.guokr.simbase.action.ExitAction;
import com.guokr.simbase.action.GetAction;
import com.guokr.simbase.action.PingAction;
import com.guokr.simbase.action.PutAction;
import com.guokr.simbase.action.RetrieveAction;
import com.guokr.simbase.action.RecommendAction;
import com.guokr.simbase.action.ReviseAction;
import com.guokr.simbase.action.SaveAction;
import com.guokr.simbase.action.SchemaAction;
import com.guokr.simbase.action.ShutdownAction;
import com.guokr.simbase.action.UpdateAction;

public class SimBase {

    static {
        ActionRegistry registry = ActionRegistry.getInstance();
        registry.register(PingAction.class);
        registry.register(ReviseAction.class);
        registry.register(SchemaAction.class);
        registry.register(AddAction.class);
        registry.register(AppendAction.class);
        registry.register(PutAction.class);
        registry.register(UpdateAction.class);
        registry.register(GetAction.class);
        registry.register(RetrieveAction.class);
        registry.register(RecommendAction.class);
        registry.register(SaveAction.class);
        registry.register(ExitAction.class);
        registry.register(ShutdownAction.class);
        registry.register(DelAction.class);
    }

    private static final String dir = System.getProperty("user.dir")
            + System.getProperty("file.separator");
    private static final String idxFilePath = dir + "keys.idx";
    private static final Logger logger = LoggerFactory.getLogger(SimBase.class);

    private Map<String, Object> context;

    private Map<String, SimEngine> base = new HashMap<String, SimEngine>();

    public SimBase(Map<String, Object> context) {
        Pattern pattern = Pattern.compile((String) context.get("debugPattern"));
        context.put("debugPattern", pattern);
        this.context = context;
        this.load();// 新建时加载磁盘数据
        this.cron();// 设置定时任务
    }

    public void cron() {
        final int cronInterval = (Integer) this.context.get("cronInterval");

        // 创建一个cron任务
        Timer cron = new Timer();

        TimerTask cleartask = new TimerTask() {
            public void run() {
                clear();
            }
        };
        cron.schedule(cleartask, cronInterval / 2, cronInterval);

        TimerTask savetask = new TimerTask() {
            public void run() {
                save();
            }
        };
        cron.schedule(savetask, cronInterval, cronInterval);
    }

    public void clear() {
        List<String> list = new ArrayList<String>(base.keySet());
        if (list.size() != 0) {
            Collections.shuffle(list);
            base.get(list.get(0)).clear();
        } else {
            logger.warn("Empty set do not need clear");
        }
    }

    public void load() {// 只有全局读取的时候读取文件里的map
        try {
            BufferedReader input = new BufferedReader(new FileReader(
                    idxFilePath));
            String[] keys = input.readLine().split("\\|");
            for (String key : keys) {
                logger.info("Loading key: " + key);// 只有存储才有多进程的情况
                this.load(key);
            }
            input.close();
        } catch (FileNotFoundException e) {
            logger.warn("Backup .idx file not found.Please examine your backup file");
            return;
        } catch (NullPointerException e) {
            logger.warn("Backup .idx file is empty.Please examine your backup file");
            return;
        } catch (Throwable e) {
            throw new SimBaseException(e);
        }
    }

    public void load(String key) {
        if (!base.containsKey(key)) {
            base.put(key, new SimEngine(key, context));
        }
        try {
            base.get(key).load(key);
        } catch (FileNotFoundException e) {
            logger.warn("Backup .dmp file not found,Please examine your backup file");
            return;
        }
    }

    public void save() {// 只有全局保存的时候把map写到文件里

        String keys = "";

        if (!base.keySet().isEmpty()) {
            FileWriter output = null;
            try {
                output = new FileWriter(idxFilePath);
                for (String key : base.keySet()) {
                    keys += key + "|";
                    logger.info("Push task:Save key-- " + key + " to queue");
                    this.save(key);
                    logger.info("Push finish");
                }
                keys = keys.substring(0, keys.length() - 1);
                output.write(keys, 0, keys.length());

            } catch (Throwable e) {
                throw new SimBaseException(e);
            } finally {
                if (output != null) {
                    try {
                        output.close();
                    } catch (IOException e) {
                        throw new SimBaseException(e);
                    }
                }
            }
        } else {
            logger.warn("Empty set don't need save");
        }
    }

    public void save(String key) {
        if (base.containsKey(key)) {
            try {
                base.get(key).save(key);
            } catch (Throwable e) {
                throw new SimBaseException(e);
            }
        }
    }

    public void delete(String key, int docid) {
        try {
            base.get(key).delete(docid);
        } catch (Throwable e) {
            throw new SimBaseException(e);// 如果没有键值直接抛错
        }
    }

    public void revise(String key, String[] schema) {
        if (!base.containsKey(key)) {
            base.put(key, new SimEngine(key, this.context));
        }
        base.get(key).revise(schema);
    }

    public void add(String key, int docid, float[] distr) {
        if (!base.containsKey(key)) {
            base.put(key, new SimEngine(key, this.context));
        }
        base.get(key).add(docid, distr);
    }

    public void append(String key, int docid, Object[] pairs) {
        if (!base.containsKey(key)) {
            base.put(key, new SimEngine(key, this.context));
        }
        base.get(key).append(docid, pairs);
    }

    public void put(String key, int docid, float[] distr) {
        if (!base.containsKey(key)) {
            base.put(key, new SimEngine(key, context));
        }
        base.get(key).put(docid, distr);
    }

    public void update(String key, int docid, Object[] pairs) {
        if (!base.containsKey(key)) {
            base.put(key, new SimEngine(key, context));
        }
        base.get(key).update(docid, pairs);
    }

    public String[] schema(String key) {
        String[] result = null;
        if (base.containsKey(key)) {
            result = base.get(key).schema();
        } else {
            result = new String[0];
        }
        return result;
    }

    public TFloatList get(String key, int docid) {
        TFloatList result = null;
        if (base.containsKey(key)) {
            result = base.get(key).get(docid);
        } else {
            result = new TFloatArrayList();
        }
        return result;
    }

    public String[] retrieve(String key, int docid) {
        String[] result = null;
        if (base.containsKey(key)) {
            result = base.get(key).retrieve(docid);
        } else {
            result = new String[0];
        }
        return result;
    }

    public int[] recommend(String key, int docid) {
        int[] result = null;
        if (base.containsKey(key)) {
            result = base.get(key).recommend(docid);
        } else {
            result = new int[0];
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    public static void main(String[] args) throws IOException {

        Map<String, Object> config = new HashMap<String, Object>();

        try {
            Yaml yaml = new Yaml();
            config = (Map<String, Object>) yaml.load(new FileReader(dir
                    + "/config/server.yaml"));
        } catch (IOException e) {
            logger.warn("YAML not found, loading default config");
            config.put("cronInterval", 120000);
            config.put("port", 7654);
        }

        try {
            Map<String, Object> context = new HashMap<String, Object>(config);
            SimBase db = new SimBase(context);
            context.put("simbase", db);

            Server server = new Server(context, ActionRegistry.getInstance());
            server.run();
        } catch (Throwable e) {
            logger.error("Server Error!", e);
            System.exit(-1);
        }

    }
}
