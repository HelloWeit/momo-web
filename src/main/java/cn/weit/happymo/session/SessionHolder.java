package cn.weit.happymo.session;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.concurrent.TimeUnit;

/**
 * @author weitong
 */
public class SessionHolder {

	private Cache<String, Object> cache;

	private long time;

	private int workerNum;

	public SessionHolder withExpireTime(long time) {
		this.time = time;
		return this;
	}

	public SessionHolder withWorkerNum(int num) {
		this.workerNum = num;
		return this;
	}

	public SessionHolder builder() {
		cache = CacheBuilder.newBuilder()
				.concurrencyLevel(workerNum)
				.expireAfterAccess(time, TimeUnit.HOURS)
				.recordStats()
				.build();
		return this;
	}

	public Object get(String key) {
		return cache.getIfPresent(key);
	}

	public void set(String key, Object obj) {
		cache.put(key, obj);
	}
}
