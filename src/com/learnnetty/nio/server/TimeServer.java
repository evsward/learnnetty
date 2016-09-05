package com.learnnetty.nio.server;

import java.io.IOException;

/**
 * NIO，说白了就是**遍历标识位的方式Selector**来代替生硬的用线程来阻塞等待
 * 
 * @author xp020154
 *
 */
public class TimeServer {
	public static void main(String[] args) throws IOException {
		int port = 8881;
		if (args != null && args.length > 0) {
			port = Integer.valueOf(args[0]);
		}
		MultiplexerTimeServer timeServer = new MultiplexerTimeServer(port);
		new Thread(timeServer, "NIO-MultiplexerTimeServer-001").start();
	}
}
