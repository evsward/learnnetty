package com.learnnetty.netty_linebased.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

/**
 * LineBasedFrameDecoder: 根据回车换行符来判断分割字符进行解码。
 * 
 * @author xp020154
 *
 */
public class TimeClient {
	public void connect(int port, String host) throws Exception {
		// 配置客户端NIO线程组(处理客户端IO读写的NioEventLoopGroup)
		EventLoopGroup group = new NioEventLoopGroup();
		try {
			Bootstrap b = new Bootstrap();// 客户端辅助启动类Bootstrap
			b.group(group).channel(NioSocketChannel.class).option(ChannelOption.TCP_NODELAY, true)
					.handler(new ChannelInitializer<SocketChannel>() {

						@Override
						protected void initChannel(SocketChannel ch) throws Exception {
							// 解决半包，粘包的关键工具：LineBasedFrameDecoder,StringDecoder.
							ch.pipeline().addLast(new LineBasedFrameDecoder(1024));
							ch.pipeline().addLast(new StringDecoder());
							ch.pipeline().addLast(new TimeClientHandler());
						}
					});
			// 发起异步连接操作,同步等待连接成功。
			ChannelFuture f = b.connect(host, port).sync();
			// 等待客户端链路关闭
			f.channel().closeFuture().sync();
		} finally {
			// 优雅退出，释放NIO线程组
			group.shutdownGracefully();
		}
	}

	public static void main(String[] args) throws Exception {
		int port = 8881;
		if (args != null && args.length > 0) {
			try {
				port = Integer.valueOf(args[0]);
			} catch (NumberFormatException e) {

			}
		}
		new TimeClient().connect(port, "127.0.0.1");
	}
}
