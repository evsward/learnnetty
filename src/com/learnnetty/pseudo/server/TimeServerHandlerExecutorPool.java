package com.learnnetty.pseudo.server;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 线程池的具体实现
 * 
 * @author xp020154
 *
 */
public class TimeServerHandlerExecutorPool {
	private ExecutorService executor;

	/**
	 * 通过构造函数创建线程池
	 * 
	 * @param maxPoolSize
	 *            线程池大小
	 * @param queueSize
	 *            队列大小
	 */
	public TimeServerHandlerExecutorPool(int maxPoolSize, int queueSize) {
		executor = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(), maxPoolSize, 120L,
				TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(queueSize));
	}

	public void execute(Runnable task) {
		executor.execute(task);
	}
}
