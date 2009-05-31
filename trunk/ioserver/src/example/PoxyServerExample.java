package example;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.util.List;

import com.yz.net.IoService;
import com.yz.net.IoFuture;
import com.yz.net.IoHandler;
import com.yz.net.IoSession;
import com.yz.net.NetMessage;
import com.yz.net.ProtocolHandler;
import com.yz.net.expand.IoConnector;
import com.yz.net.expand.ClientIoSession;

public class PoxyServerExample {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			//准备代理的服务器地址
			SocketAddress address = new InetSocketAddress("127.0.0.1", 80);
			
			IoService acceptor = new IoConnector();
			acceptor.bind(address);
			
			//设置协议处理者，可以不设置
			acceptor.setProtocolHandler(new Protocol());
			
			//设置数据处理者,一定要设置
			acceptor.setIoHandler(new DataHandler());
			
			//启动服务
			acceptor.start();
			
			
			//当有新的连接到代理服务器时，需要建立一个与上层服务器的会话
			ClientIoSession session = IoConnector.newSession((IoConnector) acceptor);
			
			//连接上层服务器，操作为异步操作
			IoFuture future = session.connect();
						
			//如果需要同步等待连接完成可以调用以下方法
			future.await();
			
			System.out.println(future.isError());
			
			System.out.println();
			
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
			return null;
		}
		
	}
}
