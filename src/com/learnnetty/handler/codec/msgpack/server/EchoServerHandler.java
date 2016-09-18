package com.learnnetty.handler.codec.msgpack.server;

import com.learnnetty.handler.codec.msgpack.vo.UserInfo;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

public class EchoServerHandler extends ChannelHandlerAdapter {

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		ctx.close();
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		// 因为前面已经将对象转为字符串。这里直接强制类型转换不会出现问题。
		UserInfo info = (UserInfo) msg;
		System.out.println("Receive client: [name: " + info.getName() + " age: " + info.getAge() + "]");
		ctx.writeAndFlush(msg);
	}

}
