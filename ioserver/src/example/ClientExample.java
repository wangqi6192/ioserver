package example;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.yz.net.IoFuture;
import com.yz.net.IoHandler;
import com.yz.net.IoSession;
import com.yz.net.NetMessage;
import com.yz.net.ProtocolHandler;
import com.yz.net.expand.ClientIoSession;
import com.yz.net.expand.IoConnector;


public class ClientExample {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			IoConnector connector = new IoConnector();
			connector.bind(8899);
			connector.setProtocolHandler(new Protocol());
			connector.setIoHandler(new DataHandler());
			
			connector.start();
			
			ClientIoSession session = IoConnector.newSession(connector);
			IoFuture future = session.connect();
			
			future.await();
			
			Random rand = new Random();
			//int count = 0;
			while(true) {
				int num = rand.nextInt(5000);
				session.write(new ExampleMessage(num));
				Thread.sleep(1000);
				/*count ++;
				if(count > 10) {
					session.close();
					break;
				}*/
			}
			
			//connector.stop();
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
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
			
			System.out.println("Num = " + message.getNumber());
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
