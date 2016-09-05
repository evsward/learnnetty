package com.learnnetty.nio.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class TimeClientHandler implements Runnable {

	private String host;
	private int port;
	private Selector selector;
	private SocketChannel socketChannel;
	private volatile boolean stop;

	public TimeClientHandler(String host, int port) {
		this.host = host == null ? "127.0.0.1" : host;
		this.port = port;
		try {
			selector = Selector.open();
			socketChannel = SocketChannel.open();
			socketChannel.configureBlocking(false);
			// 设置TCP参数，socketChannel.socket()取得socket
			socketChannel.socket().setReuseAddress(true);
			socketChannel.socket().setReceiveBufferSize(1024);
			socketChannel.socket().setSendBufferSize(1024);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	@Override
	public void run() {
		try {
			doConnect();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		while (!stop) {
			try {
				selector.select(1000);
				Set<SelectionKey> selectedKeys = selector.selectedKeys();
				Iterator<SelectionKey> it = selectedKeys.iterator();
				SelectionKey key = null;
				// 在循环体中轮询Selector，当有就绪的Channel时，执行handleInput方法。
				while (it.hasNext()) {
					key = it.next();
					it.remove();
					try {
						handleInput(key);
					} catch (Exception e) {
						if (key != null) {
							key.cancel();
							if (key.channel() != null) {
								key.channel().close();
							}
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
		// 优雅退出，Selector关闭以后，JDK自动释放所有关联的资源
		if (selector != null) {
			try {
				selector.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void doConnect() throws IOException {
		// 判断是否连接成功
		if (socketChannel.connect(new InetSocketAddress(host, port))) {
			// 连接成功，注册读位，然后写入数据。
			socketChannel.register(selector, SelectionKey.OP_READ);
			doWrite(socketChannel);
		} else {
			// 服务端没有返回TCP握手消息，注册OP_CONNECT，当服务端返回TCP
			// syn-ack消息以后，Selector就会轮询到该SocketChannel已处于连接就绪状态。
			socketChannel.register(selector, SelectionKey.OP_CONNECT);
		}
	}

	private void doWrite(SocketChannel sc) throws IOException {
		byte[] req = "QUERY TIME ORDER".getBytes();
		ByteBuffer writeBuffer = ByteBuffer.allocate(req.length);
		writeBuffer.put(req);
		writeBuffer.flip();
		sc.write(writeBuffer);
		if (!writeBuffer.hasRemaining()) {// 是否含有未处理完数据
			System.out.println("Send order 2 server succeed.");
		}
	}

	private void handleInput(SelectionKey key) throws IOException {
		if (key.isValid()) {
			SocketChannel sc = (SocketChannel) key.channel();
			// 如果是连接状态，说明服务端已返回ACK应答消息
			if (key.isConnectable()) {
				if (sc.finishConnect()) {// 客户端连接成功
					sc.register(selector, SelectionKey.OP_READ);// 监听网路读操作（用来接受resp）
					doWrite(sc);// 发送请求消息给服务端
				} else {// 连接失败
					System.exit(1);
				}
			}
			// 如果是可读的，说明客户端接收到了服务端的应答消息
			if (key.isReadable()) {
				ByteBuffer readBuffer = ByteBuffer.allocate(1024);// 不知应答码流大小，预分配1MB缓冲区接收
				int readBytes = sc.read(readBuffer);// 异步读取
				if (readBytes > 0) {
					readBuffer.flip();
					byte[] bytes = new byte[readBuffer.remaining()];
					readBuffer.get(bytes);
					String body = new String(bytes, "UTF-8");
					System.out.println("nio: Now is : " + body);
					this.stop = true;
				} else if (readBytes < 0) {
					key.cancel();
					sc.close();
				} else {
					;
				}
			}
		}
	}
}
