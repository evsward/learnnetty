package com.learnnetty.nio.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * 多路复用类
 * 
 * @author xp020154
 *
 */
public class MultiplexerTimeServer implements Runnable {
	// 多路复用器
	private Selector selector;
	private ServerSocketChannel servChannel;
	private volatile boolean stop;

	// 利用构造函数，初始化设置
	public MultiplexerTimeServer(int port) {
		try {
			selector = Selector.open();
			servChannel = ServerSocketChannel.open();
			servChannel.configureBlocking(false);// 设置为异步非阻塞模式
			// backlog 设为1024
			servChannel.socket().bind(new InetSocketAddress(port), 1024);
			// 注册selector到servChannel，监听SelectionKey.OP_ACCEPT操作位
			// 将channel给Selector管理
			servChannel.register(selector, SelectionKey.OP_ACCEPT);
			System.out.println("nio: The time server is started in port: " + port);
		} catch (IOException e) {
			e.printStackTrace();
			// 出错则退出。参数不为0意味着非正常中断。
			System.exit(1);
		}
	}

	public void stop() {
		this.stop = true;
	}

	@Override
	public void run() {
		while (!stop) {
			try {
				selector.select(1000);// 休眠时间1s，每隔1s唤醒一次。
				Set<SelectionKey> selectedKeys = selector.selectedKeys();
				Iterator<SelectionKey> it = selectedKeys.iterator();
				SelectionKey key = null;
				// 遍历Selector，对就绪状态的Channel集合进行迭代
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
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}
		// selector.close()以后，所有注册在上面的Channel,pipe等资源会自动关闭，不用手动释放。
		if (selector != null) {
			try {
				selector.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 根据key的操作位来判断网络事件类型。
	 * 
	 * @param key
	 *            SelectionKey: A token representing the registration of a
	 *            SelectableChannel with a Selector.
	 * @throws IOException
	 */
	private void handleInput(SelectionKey key) throws IOException {
		if (key.isValid()) {
			// 处理新键入的请求消息
			if (key.isAcceptable()) {
				ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
				// accept()相当于TCP的三次握手，方法执行后，连接正式建立。
				SocketChannel sc = ssc.accept();// 接受客户端连接请求并创建SocketChannel实例
				sc.configureBlocking(false);
				sc.register(selector, SelectionKey.OP_READ);
			}
			// 读取客户端的请求消息
			if (key.isReadable()) {
				// 此时连接已建立，不用重复建立，直接获取channel即可
				SocketChannel sc = (SocketChannel) key.channel();
				// 不知道请求消息有多大，先分配1MB的ByteBuffer(字节缓冲区)
				ByteBuffer readBuffer = ByteBuffer.allocate(1024);
				// 开始从Channel中将消息数据读入ByteBuffer，因为连接建立时已设定异步非阻塞，所以现在读的操作是异步非阻塞的。
				int readBytes = sc.read(readBuffer);
				// 根据返回值判断
				if (readBytes > 0) {// 读到了字节
					readBuffer.flip();// limit->position->0,用于后续对ByteBuffer读取操作。
					// 根据readBuffer中可读字节数readBuffer.remaining(),创建byte[]
					byte[] bytes = new byte[readBuffer.remaining()];
					readBuffer.get(bytes);// 将readBuffer中的字节数组复制到新建的字节数组变量中。
					String body = new String(bytes, "UTF-8");// 转成String类型消息体
					System.out.println("The time server receive order: " + body);
					String currentTime = "QUERY TIME ORDER".equalsIgnoreCase(body)
							? new java.util.Date(System.currentTimeMillis()).toString() : "BAD ORDER";
					doWrite(sc, currentTime);
				} else if (readBytes < 0) {// 链路已经关闭，需要在此区域内关闭SocketChannel，释放资源。
					// 对端链路关闭。
					key.cancel();
					sc.close();
				} else { // 没有读到字节，属于正常现象，忽略。
					;// 读到0字节，忽略
				}
			}
		}
	}

	/**
	 * 将response异步返回客户端
	 * 
	 * @param channel:
	 *            A token representing the registration of a SelectableChannel
	 *            with a Selector.
	 * @param response
	 * @throws IOException
	 * 
	 ****             bug：写半包问题，因为SocketChannle是异步非阻塞的，不能保证一次把response都发送出去。
	 */
	private void doWrite(SocketChannel channel, String response) throws IOException {
		if (response != null && response.trim().length() > 0) {
			byte[] bytes = response.getBytes();// 先将String编码为字节数组byte[]
			ByteBuffer writeBuffer = ByteBuffer.allocate(bytes.length);// 根据byte[]容量创建ByteBuffer
			writeBuffer.put(bytes);// 将byte[]复制到ByteBuffer
			writeBuffer.flip();// limit->position->0,用于后续对ByteBuffer读取操作。
			channel.write(writeBuffer);// 写入channel发送客户端
		}
	}
}
