package com.zld.utils;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import net.rubyeye.xmemcached.CASOperation;
import net.rubyeye.xmemcached.Counter;
import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.exception.MemcachedException;

/**
 * XMemcached������
 */
public class CacheXMemcache<T> {
	
	//private final Logger logger = Logger.getLogger(CacheXMemcache.class);
	
	private MemcachedClient memcachedClient;

	/**
	 * ���캯��
	 */
	public CacheXMemcache() {
	}

	/**
	 * setע��
	 */
	public void setMemcachedClient(MemcachedClient memcachedClient) {
		memcachedClient.setConnectTimeout(1000);
		this.memcachedClient = memcachedClient;
	}
	
	/**
	 * ִ��һ����������ڻ������ж�Ӧ��ֵ����ôֱ�ӷ��أ�����ִ�����񲢰���������뻺��
	 * @param task ����
	 * @return ���񷵻�ֵ
	 */
	public T doCachedTask(CachedTask<T> task) {
		String key = task.getKey();
		String flag = task.getFlag();
		T value = null;
		try {
			value = getCached(key);
			if (value == null) {//��һ�δ���
				value = task.run();
				setCached(key,value);
			} else if (flag != null) {//����
				value = task.run();
				putCachedXM(key, value);
			}
		} catch (Exception e) {
			value = task.run();
			//e.printStackTrace();
		}
		return value;
	}

	/**
	 * CAS���� ʵ��ԭ�Ӹ���
	 */
	public void putCachedXM(String key, final Object object) throws TimeoutException,
			InterruptedException, MemcachedException {
		memcachedClient.cas(key, new CASOperation<Object>() {
			public int getMaxTries() {
				return Integer.MAX_VALUE;
			}

			public Object getNewValue(long currentCAS, Object currentValue) {
				return object;
			}
		});
	}

	/**
	 * �õ�����
	 * @throws MemcachedException 
	 * @throws InterruptedException 
	 * @throws TimeoutException 
	 */
	public T getCached(String key){
		T t = null;
		try {
			t = memcachedClient.get(key);
		} catch (TimeoutException e) {
			//logger.error(e);
		} catch (InterruptedException e) {
			//logger.error(e);
		} catch (MemcachedException e) {
			//logger.error(e);
		}
		return t;
	}
	
	/**
	 * ���»���
	 * @throws MemcachedException 
	 * @throws InterruptedException 
	 * @throws TimeoutException 
	 */
	public boolean setCached(String key,Object object){
		boolean isSuccuess = false;
		try {
			 isSuccuess = memcachedClient.set(key,0,object);
		} catch (TimeoutException e) {
			//logger.error(e);
		} catch (InterruptedException e) {
			//logger.error(e);
		} catch (MemcachedException e) {
			//logger.error(e);
		}
		return isSuccuess;
	}
	
	/**
	 * add�������ɹ�����true���������Ѿ�����ʱ������false
	 * @param key
	 * @param object
	 * @param exp ����ʱ�䣨�룩
	 * @return
	 */
	public boolean addCached(String key, Object object, int exp){
		try {
			return memcachedClient.add(key, exp, object);
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MemcachedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * set��������Լ����ӣ����set��key�Ѿ����ڣ���������Ը��¸�key����Ӧ��ԭ�������ݣ�Ҳ����ʵ�ָ��µ����á�
	 * @param key
	 * @param object
	 * @param exp
	 * @return
	 */
	public boolean setCached(String key, Object object, int exp){
		try {
			return memcachedClient.set(key, exp, object);
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MemcachedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	public boolean delCached(String key){
		try {
			return memcachedClient.delete(key);
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MemcachedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	

	/**
	 * ������
	 * @throws MemcachedException 
	 * @throws InterruptedException 
	 * @throws TimeoutException 
	 */
	public Counter getCounter(String key){
		return memcachedClient.getCounter(key,0);
	}
	
	/**
	 * �ر�����
	 */
	public void shutdown() {
		try {
			memcachedClient.shutdown();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}