package com.learnnetty.aio.server;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

public class AcceptCompletionHandler implements CompletionHandler<AsynchronousSocketChannel, AsyncTimeServerHandler> {

	@Override
	public void completed(AsynchronousSocketChannel result, AsyncTimeServerHandler attachment) {
		attachment.asynchronousServerSocketChannel.accept(attachment, this);
		ByteBuffer buffer = ByteBuffer.allocate(1024);
		/**
		 * AsynchronousSocketChannel 的read方法参数介绍：
		 * ByteBuffer dst: 接收缓冲区，用于从异步Channel中读取数据包；
		 * A attachment: 异步Channel携带的附件，通知回调的时候作为入参使用；
		 * CompletionHandler<Integer,? super A>: 接收通知回调的业务Handler，这里是ReadCompletionHandler。
		 */
		result.read(buffer, buffer, new ReadCompletionHandler(result));
	}

	@Override
	public void failed(Throwable exc, AsyncTimeServerHandler attachment) {
		exc.printStackTrace();
		attachment.latch.countDown();
	}

}
