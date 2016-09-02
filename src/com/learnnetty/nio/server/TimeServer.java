package com.learnnetty.nio.server;

import java.io.IOException;

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
