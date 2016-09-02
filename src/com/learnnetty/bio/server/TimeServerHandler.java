package com.learnnetty.bio.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class TimeServerHandler implements Runnable {
	private Socket socket;

	public TimeServerHandler(Socket socket) {
		this.socket = socket;
	}

	@Override
	public void run() {
		BufferedReader in = null;
		PrintWriter out = null;
		try {
			/**
			 * InputStream:当对Socket的输入流进行读取操作时，它会一直阻塞下去，直到发生以下三种事件：
			 * 1、有数据可读
			 * 2、可用数据已经读取完毕
			 * 3、发生空指针或IO异常
			 */
			in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
			/**
			 * OutputStream:直到所有要发送字节全部写入完毕，或者异常，否则一直阻塞。
			 */
			out = new PrintWriter(this.socket.getOutputStream(), true);
			String currentTime = null;
			String body = null;
			while (true) {
				body = in.readLine();
				if (body == null)
					break;
				System.out.println("The tiem server receive order: " + body);
				currentTime = "QUERY TIME ORDER".equalsIgnoreCase(body)
						? new java.util.Date(System.currentTimeMillis()).toString() : "BAD ORDER";
				out.println(currentTime);
			}
		} catch (Exception e) {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			if (out != null) {
				out.close();
				out = null;
			}
			if (this.socket != null) {
				try {
					this.socket.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				this.socket = null;
			}
		}
	}

}
