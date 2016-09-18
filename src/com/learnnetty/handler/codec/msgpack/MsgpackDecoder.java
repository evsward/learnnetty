package com.learnnetty.handler.codec.msgpack;

import java.util.List;

import org.msgpack.MessagePack;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

public class MsgpackDecoder extends MessageToMessageDecoder<ByteBuf> {

	/**
	 * 从msg中读取需要解码的byte数组，然后调用MessagePack的read方法将其反序列化为Object对象，将解码后的对象加入到解码列表out。
	 */
	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
		final byte[] array;
		final int length = msg.readableBytes();
		array = new byte[length];
		msg.getBytes(msg.readerIndex(), array, 0, length);
		MessagePack msgpack = new MessagePack();
		out.add(msgpack.read(array));
	}

}
