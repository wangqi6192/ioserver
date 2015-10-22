# 使用介绍 #
客户端可以用下面的Client项来连，也可以用Example2列程的Client项

# Server #
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

# Client #
```
package example;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Random;

public class Client extends Thread {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			Client client = new Client("127.0.0.1", 8899);
			client.start();
			
			
			while(true) {
				Thread.sleep(10100);
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	
	Socket socket;
	
	DataInputStream dis;
	
	DataOutputStream dos;
	
	boolean isRuning = false;
	
	int count = 0;
	
	public Client(String host, int port) throws IOException {
		//socket = new Socket(host, port);
		
		//dis = new DataInputStream(socket.getInputStream());
		//dos = new DataOutputStream(socket.getOutputStream());
	}
	
	
	public void longConnection() {
		Random rand = new Random();
		try {
			socket = new Socket("127.0.0.1", 8899);
			dis = new DataInputStream(socket.getInputStream());
			dos = new DataOutputStream(socket.getOutputStream());
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		while(isRuning) {
			
			int temp = rand.nextInt(5000);
			
			try {
				dos.writeInt(temp);
				
				int checkInt = dis.readInt();
				
				if(checkInt != (temp + 1)) {
					System.out.println("存在问题，存在问题");
				}
				else {
					System.out.println(checkInt);
				}
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	/**
	 * 短连接测试
	 */
	public void shortConnection() {
		Random rand = new Random();
		while(isRuning) {
			try {
				socket = new Socket("127.0.0.1", 8899);
				dis = new DataInputStream(socket.getInputStream());
				dos = new DataOutputStream(socket.getOutputStream());
			} catch (UnknownHostException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			
			
			
			int temp = rand.nextInt(5000);
			
			try {
				dos.writeInt(temp);
				
				int checkInt = dis.readInt();
				
				if(checkInt != (temp + 1)) {
					System.out.println("存在问题，存在问题");
				}
				else {
					System.out.println(checkInt);
				}
				
				socket.close();
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	public void run() {
		isRuning = true;
		//shortConnection();
		longConnection();
	}
}
```