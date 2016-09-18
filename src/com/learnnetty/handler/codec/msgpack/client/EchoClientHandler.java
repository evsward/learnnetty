package com.learnnetty.handler.codec.msgpack.client;

import com.learnnetty.handler.codec.msgpack.vo.UserInfo;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

public class EchoClientHandler extends ChannelHandlerAdapter {
	private int sendNumber;

	public EchoClientHandler(int sendNumber) {
		this.sendNumber = sendNumber;
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		UserInfo[] infos = UserInfo();
		for (UserInfo infoE : infos) {
			// ctx.writeAndFlush(Unpooled.copiedBuffer(ECHO_REQ.getBytes()));
			ctx.write(infoE);// 这里直接传输对象。
		}
		ctx.flush();
	}

	private UserInfo[] UserInfo() {
		UserInfo[] infos = new UserInfo[sendNumber];
		UserInfo userInfo = null;
		for (int i = 0; i < sendNumber; i++) {
			userInfo = new UserInfo();
			userInfo.setAge(i);
			userInfo.setName("ABCDRFGH ---> " + i);
			infos[i] = userInfo;
		}
		return infos;
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		System.out.println("Receive client: " + msg);
		ctx.write(msg);
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.flush();
	}
}
