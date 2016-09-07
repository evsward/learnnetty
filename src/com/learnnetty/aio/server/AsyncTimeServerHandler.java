package com.learnnetty.aio.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.util.concurrent.CountDownLatch;

public class AsyncTimeServerHandler implements Runnable {

	CountDownLatch latch;
	// 异步服务器套接字通道
	AsynchronousServerSocketChannel asynchronousServerSocketChannel;

	public AsyncTimeServerHandler(int port) {
		try {
			asynchronousServerSocketChannel = AsynchronousServerSocketChannel.open();
			asynchronousServerSocketChannel.bind(new InetSocketAddress(port));
			System.out.println("The time server is started in port：" + port);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		/**
		 * 在完成一组正在执行的操作之前，允许当前线程一直阻塞， 
		 * 这里是为了防止服务端执行完毕退出。
		 */
		latch = new CountDownLatch(1);
		doAccept();
		try {
			latch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void doAccept() {
		//如果有新的客户端接入，系统会回调AcceptCompletionHandler的completed方法，表示新客户端已接入成功。
		asynchronousServerSocketChannel.accept(this, new AcceptCompletionHandler());
	}

}
