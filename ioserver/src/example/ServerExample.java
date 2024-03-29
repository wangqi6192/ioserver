﻿package example;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import com.yz.net.Configure;
import com.yz.net.IoHandlerAdapter;
import com.yz.net.IoSession;
import com.yz.net.NetMessage;
import com.yz.net.ProtocolHandler;
import com.yz.net.IoService;
import com.yz.net.impl.IoServerImpl;

public class ServerExample {

	public static void main(String[] args) {
		try {
			Configure config = new Configure();
			config.setAddress(new java.net.InetSocketAddress("127.0.0.1", 8899));
			config.setProtocolHandler(new Protocol());
			config.setIoHandler(new DataHandler());
			
			IoService server = new IoServerImpl();
			config.start(server);
			
		
			
			while(true) {
				Thread.sleep(1000);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	
	public static class DataHandler extends IoHandlerAdapter {
		@Override
		public void messageReceived(IoSession session, NetMessage msg) {
			//TODO:当存在协议解析类时，请按具体项目要求完成此方法
			ExampleMessage message = (ExampleMessage) msg;
			int returnvalue = message.getNumber() + 1;
			
			ExampleMessage sendMsg = new ExampleMessage(returnvalue);
			session.write(sendMsg);
		}
	
	}
	
	public static class Protocol implements ProtocolHandler {

		@Override
		public List<NetMessage> onData(ByteBuffer data, IoSession session) {
			//TODO:这里负责茶房地数据进行解析，并形成一个具体的消息类
			
			ArrayList<NetMessage> list = new ArrayList<NetMessage>();
			while(data.remaining() >= 4) {
				int number = data.getInt();
				list.add(new ExampleMessage(number));
			}
			
			return list;
		}
		
	}
	
	public static class ExampleMessage implements NetMessage {
		
		private int number;
		
		public ExampleMessage(int number) {
			this.number = number;
		}
		
		public int getNumber() {
			return number;
		}

		@Override
		public byte[] getContent() {
			byte[] content = new byte[4];
			 
			content[0] = (byte) ((number >>> 24) & 0xFF);
			content[1] = (byte) ((number >>> 16) & 0xFF);
			content[2] = (byte) ((number >>>  8) & 0xFF);
			content[3] = (byte) ((number >>>  0) & 0xFF);
			
			return content;
		}
		
	}
}
