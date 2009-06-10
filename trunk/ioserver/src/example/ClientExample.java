package example;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.yz.net.Configure;
import com.yz.net.IoFuture;
import com.yz.net.IoHandler;
import com.yz.net.IoSession;
import com.yz.net.NetMessage;
import com.yz.net.ProtocolHandler;
import com.yz.net.expand.IoConnector;


public class ClientExample {
	
	static Random rand = new Random();
	
	static PrintRspTime printRspTime = new PrintRspTime();
	
	public static void main(String[] args) {
		try {
			Configure config = new Configure();
			config.setAddress(new InetSocketAddress("127.0.0.1",8899));
			config.setProtocolHandler(new Protocol());
			config.setIoHandler(new DataHandler());
			
			IoConnector connector = new IoConnector();
			
			config.start(connector);
			
			Thread t = new Thread(printRspTime);
			t.start();
			
			
			for(int i=0; i<10; i++) {
				IoSession session = IoConnector.newSession(connector);
				IoFuture future = session.connect();
				future.await();
				int num = rand.nextInt(5000);
				
				session.addAttribute("START", System.currentTimeMillis());
				session.write(new ExampleMessage(num));
			}
			
			/*IoSession session = IoConnector.newSession(connector);
			IoFuture future = session.connect();
			
			future.await();
			*/
			
			
			//int count = 0;
			while(true) {
				
				/*int num = rand.nextInt(5000);
				startTime = System.currentTimeMillis();
				session.write(new ExampleMessage(num));*/
				Thread.sleep(1000);
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
			Long starttime = (Long) session.getAttribute("START");
			long endtime = System.currentTimeMillis();
			
			printRspTime.queue.offer(new long[]{starttime, endtime});
			
			ExampleMessage message = (ExampleMessage) msg;
			//System.out.println("Num = " + message.getNumber());
			
			/*try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
			
			session.addAttribute("START", System.currentTimeMillis());
			session.write(new ExampleMessage(rand.nextInt(5000)));
		}

		
		@Override
		public void messageReceived(IoSession session, byte[] msgdata) {
			//TODO:当没有协议解析类时，请按具体项目要求完成此方	
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
	
	
	public static class PrintRspTime implements Runnable {
		ConcurrentLinkedQueue<long[]> queue = new ConcurrentLinkedQueue();

		@Override
		public void run() {
			while(true) {
				long[] times = queue.poll();
				if(times != null) {
					System.out.println("RSPTIME = " + (times[1] - times[0]));
				}
				
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}
		
	}
}
