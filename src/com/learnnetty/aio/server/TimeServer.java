package com.learnnetty.aio.server;

/**
 * 与其他IO不同的是，这里的异步机制是建立在AsynchronousSocketChannel之上，这个类十分强大，可以不断的回调读取或者写入。
 * 
 * @author xp020154
 *
 */
public class TimeServer {
	public static void main(String[] args) {
		int port = 8881;
		if (args != null && args.length > 0) {
			try {
				port = Integer.valueOf(args[0]);
			} catch (Exception e) {
			}
		}
		// 异步时间服务器处理类
		AsyncTimeServerHandler timeServer = new AsyncTimeServerHandler(port);
		new Thread(timeServer, "AIO-AsynTimeServerHandler-001").start();
	}
}
