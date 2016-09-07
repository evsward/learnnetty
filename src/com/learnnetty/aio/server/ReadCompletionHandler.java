package com.learnnetty.aio.server;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

public class ReadCompletionHandler implements CompletionHandler<Integer, ByteBuffer> {

	private AsynchronousSocketChannel channel;

	public ReadCompletionHandler(AsynchronousSocketChannel channel) {
		if (this.channel == null) {
			this.channel = channel;
		}
	}

	@Override
	public void completed(Integer result, ByteBuffer buffer) {
		buffer.flip();
		// buffer.remaining() 缓冲区的可读字节数长度length
		byte[] body = new byte[buffer.remaining()];
		buffer.get(body);// 将buffer中可读字节复制到body中去。
		try {
			String req = new String(body, "UTF-8");
			System.out.println("The time server receive order: " + req);
			String currentTime = "QUERY TIME ORDER".equalsIgnoreCase(req)
					? new java.util.Date(System.currentTimeMillis()).toString() : "BAD ORDER";
			doWrite(currentTime);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	private void doWrite(String currentTime) {
		if (currentTime != null && currentTime.trim().length() > 0) {
			byte[] bytes = currentTime.getBytes();
			ByteBuffer writeBuffer = ByteBuffer.allocate(bytes.length);// 为buffer分配大小
			writeBuffer.put(bytes);// 复制到bytes
			writeBuffer.flip();// 准备写
			// 调用AsynchronousSocketChannel的异步write方法
			channel.write(writeBuffer, writeBuffer, new CompletionHandler<Integer, ByteBuffer>() {

				@Override
				public void completed(Integer result, ByteBuffer buffer) {
					// 如果没有发送完成，则继续发送。
					if (buffer.hasRemaining()) {
						channel.write(buffer, buffer, this);
					}
				}

				@Override
				public void failed(Throwable exc, ByteBuffer attachment) {
					try {
						channel.close();
					} catch (IOException e) {

					}
				}

			});
		} else {
			System.out.println("currentTime illegal.");
		}
	}

	@Override
	public void failed(Throwable exc, ByteBuffer attachment) {
		try {
			this.channel.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
