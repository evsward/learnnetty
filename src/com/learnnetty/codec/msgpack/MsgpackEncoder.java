package com.learnnetty.codec.msgpack;

import org.msgpack.MessagePack;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * 负责将Object类型的POJO对象编码为byte数组，然后写入ByteBuf中。
 * 
 * @author xp020154
 *
 */
public class MsgpackEncoder extends MessageToByteEncoder<Object> {

	@Override
	protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
		MessagePack msgpack = new MessagePack();
		byte[] raw = msgpack.write(msg);
		out.writeBytes(raw);
	}

}
