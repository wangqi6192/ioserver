高性能，易扩展的网络框架，相对于Apache的MINA更加轻量级，源码更容易读懂，源码中有大量详细的中文注解，是一个非常不错的学习框架，框架主要至力于解决中国市场上手机网游的服务器端程序的编写。

# IoServer构建简单服务器例程(更详细的例程可下载例程包，或进入Wiki查看) #

服务器接收一个来自客户端的整型，并加一返回给客户端

```
package example;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import com.yz.net.IoFuture;
import com.yz.net.IoHandler;
import com.yz.net.IoSession;
import com.yz.net.NetMessage;
import com.yz.net.ProtocolHandler;
import com.yz.net.IoService;
import com.yz.net.impl.IoServerImpl;

public class ServerExample {
	public static void main(String[] args) {
		try {
			//绑定一个本机端口
			IoService acceptor = new IoServerImpl(8899);
			
			//设置协议处理者，可以不设置
			acceptor.setProtocolHandler(new Protocol());
			
			//设置消息处理者，一定要设置
			acceptor.setIoHandler(new DataHandler());
			
			//启动
			acceptor.start();
			
			while(true) {
				Thread.sleep(1000);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	public static class DataHandler implements IoHandler {

		@Override
		public void ioSessionClosed(IoFuture future) {
			//TODO:当一个会话关闭后被触发的方法
			
		}

		@Override
		public void messageReceived(IoSession session, NetMessage msg) {
			//TODO:当存在协议解析类时，请按具体项目要求完成此方法
			ExampleMessage message = (ExampleMessage) msg;
			
			int returnvalue = message.getNumber() + 1;
			
			
			ExampleMessage sendMsg = new ExampleMessage(returnvalue);
			
			session.write(sendMsg);
		}

		
		@Override
		public void messageReceived(IoSession session, byte[] msgdata) {
			//TODO:当没有协议解析类时，请按具体项目要求完成此方法
			
		}	
	}
	
	public static class Protocol implements ProtocolHandler {

		@Override
		public boolean isClose() {
			//TODO:如果需要长连接，请反回false，如果需要短连接请返回true
			return false;
		}

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

```