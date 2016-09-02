package com.learnnetty.pseudo.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import com.learnnetty.bio.server.TimeServerHandler;

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
			System.out.println("The time server is started in port: " + port);
			Socket socket = null;
			TimeServerHandlerExecutorPool singleExecutor = new TimeServerHandlerExecutorPool(50, 1000);
			while (true) {
				socket = server.accept();
				/**
				 * 仍旧使用TimeServerHandler的服务端处理逻辑。
				 * 但是用线程池代替了new Thread(***).start();
				 * 解决了线程连接一对一的资源耗尽问题，
				 * 但底层仍旧采用的是BIO模型。
				 */
				singleExecutor.execute(new TimeServerHandler(socket));
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
