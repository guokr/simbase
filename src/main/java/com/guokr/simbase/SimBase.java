package com.guokr.simbase;

import java.io.IOException;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.wahlque.net.action.ActionRegistry;

import com.guokr.simbase.action.AddAction;
import com.guokr.simbase.action.AppendAction;
import com.guokr.simbase.action.DelAction;
import com.guokr.simbase.action.ExitAction;
import com.guokr.simbase.action.GetAction;
import com.guokr.simbase.action.PingAction;
import com.guokr.simbase.action.PutAction;
import com.guokr.simbase.action.RecommendAction;
import com.guokr.simbase.action.RetrieveAction;
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

	private Map<String, Object> context;

	public SimBase(Map<String, Object> context) {
		this.context = context;
		this.load();// 新建时加载磁盘数据
		this.startCron();// 设置定时任务
	}

	private void clear() {
	}

	private void startCron() {
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

	public void cfg(String key, String property) {
	}

	public void load() {
	}

	public void load(String bkey) {
	}

	public void save() {
	}

	public void save(String bkey) {
	}

	public void del(String key) {
	}

	public int xincr(String key) {
		return 1;
	}

	public String xget(int vecid) {
		return null;
	}

	public String[] blist() {
		return null;
	}

	public void bmk(String bkey, String[] base) {
	}

	public void brev(String bkey, String[] base) {
	}

	public String[] bget(String bkey) {
		return null;
	}

	public String[] vlist(String bkey) {
		return null;
	}

	public void vmk(String vkey, String bkey) {
	}

	public void vrem(String vkey, int vecid) {
	}

	public void vadd(String vkey, int vecid, float[] distr) {
	}

	public void vacc(String vkey, int vecid, float[] distr) {
	}

	public float[] vget(String vkey, int vecid) {
		return null;
	}

	public void jadd(String vkey, int vecid, String jsonlike) {
	}

	public void jacc(String vkey, int vecid, String jsonlike) {
	}

	public String jget(String vkey, int vecid) {
		return null;
	}

	//For client-side sparsification
	public void _vadd(String vkey, int vecid, int[] pairs) {
	}

	//For client-side sparsification
	public void _vacc(String vkey, int vecid, int[] pairs) {
	}

	//For client-side sparsification
	public int[] _vget(String vkey, int vecid) {
		return null;
	}

	public String[] rlist(String vkey) {
		return null;
	}

	public void rmk(String vkeySource, String vkeyTarget) {
	}

	public String[] rget(String vkeySource, String vkeyTarget) {
		return null;
	}

	public int[] rrec(String vkeySource, String vkeyTarget) {
		return null;
	}

	public static void main(String[] args) throws IOException {
	}
}
