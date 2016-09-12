package com.learnnetty.netty.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

public class TimeServerHandler extends ChannelHandlerAdapter {

	private int counter;

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		ByteBuf buf = (ByteBuf) msg;
		// buf.readableBytes()相当于buffer.remaining(),获取缓冲区可读字节数长度
		byte[] req = new byte[buf.readableBytes()];
		// 将buf中的字节复制到req中，相当于buffer.get(req);
		buf.readBytes(req);
		String body = new String(req, "UTF-8").substring(0, req.length - System.getProperty("line.separator").length());
		System.out.println("The time server receive order: " + body + "; the counter is : " + ++counter);
		String currentTime = "QUERY TIME ORDER".equalsIgnoreCase(body)
				? new java.util.Date(System.currentTimeMillis()).toString() : "BAD ORDER";
		currentTime = currentTime + System.getProperty("line.separator");
		ByteBuf resp = Unpooled.copiedBuffer(currentTime.getBytes());
		// 原来的doWrite，异步发送应答消息给客户端。这里已经封装好，直接调用write方法即可。
		ctx.writeAndFlush(resp);
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		/**
		 * netty 的write並不直接將消息寫入SocketChannel發送，而是放到一個發送緩衝數組中，
		 * 通過下面的flush方法，將緩衝區內的消息全部寫入SocketChannel發送。
		 */
		ctx.flush();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		ctx.close();
	}

}
