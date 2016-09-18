package com.learnnetty.netty_linebased.client;

import java.util.logging.Logger;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

public class TimeClientHandler extends ChannelHandlerAdapter {
	private static final Logger logger = Logger.getLogger(TimeClientHandler.class.getName());
	private byte[] req;
	private int counter;

	public TimeClientHandler() {
		// 初始化，将訪問服務端的命令存入ByteBuf
		req = ("QUERY TIME ORDER" + System.getProperty("line.separator")).getBytes();

	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		// 释放资源
		logger.warning("Unexpected exception from downstream: " + cause.getMessage());
		ctx.close();
	}

	@Override
	/**
	 * 回调方法，当连接成功建立以后，执行channelActive的方法。
	 */
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		// 寫入命令到服務端
		ByteBuf message = null;
		for (int i = 0; i < 100; i++) {
			message = Unpooled.buffer(req.length);
			message.writeBytes(req);
			ctx.writeAndFlush(message);
		}
	}

	@Override
	/**
	 * 获取服务端应答消息
	 */
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		/*
		 * ByteBuf buf = (ByteBuf) msg; byte[] req = new
		 * byte[buf.readableBytes()]; buf.readBytes(req); String body = new
		 * String(req, "UTF-8");
		 */
		String body = (String) msg;// 直接强制类型转换，转为String。
		System.out.println("Now is: " + body + "; the counter is: " + ++counter);
	}

}
