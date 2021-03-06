package com.learnnetty.bio.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TimeServer {
	public static void main(String[] args) throws IOException {
		int port = 8881;
		if (args != null && args.length > 0) {
			try {
				port = Integer.valueOf(args[0]);
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		}
		ServerSocket server = null;
		try {
			server = new ServerSocket(port);
			System.out.println("The time server is started in port:" + port);
			Socket socket = null;
			// 通过一个无限循环来监听客户端的连接。
			while (true) {
				// 如果没有客户端接入，则主线程阻塞在server.accept();这行上。
				socket = server.accept();
				/**
				 * 对当前socket连接进行服务端逻辑编程。
				 * 每当有一个客户端接入，就要new一个线程来处理客户端的请求。
				 * 一连接一线程
				 */
				new Thread(new TimeServerHandler(socket)).start();
			}
		} finally {
			if (server != null) {
				System.out.println("The time server close");
				server.close();
				server = null;
			}
		}
	}
}
