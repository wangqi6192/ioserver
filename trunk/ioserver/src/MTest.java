import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.Timer;
import java.util.TimerTask;

import com.yz.net.IoService;
import com.yz.net.expand.IoConnector;
import com.yz.net.impl.IoServerImpl;

/**
 * 无任何意义，完成用于测试
 * @author huwei
 *
 */
public class MTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			
	
			
			
			/*SelectorProvider provider = SelectorProvider.provider();
			
			SocketChannel channel = provider.openSocketChannel();
			
			channel.configureBlocking(false);
			
			InetSocketAddress address = new InetSocketAddress("www.126.com", 80);
			
			System.out.println(channel.connect(address));
			
			Thread.sleep(500);
			
			System.out.println(channel.finishConnect());
			
			
			Socket socket = channel.socket();
			
			System.out.println(socket.isClosed());
			System.out.println(socket.isConnected());
			
			
			channel.configureBlocking(false);
			
			//channel.connect(remote)
			SocketChannel channel2 = provider.openSocketChannel();
			
			System.out.println(channel.equals(channel2));*/
			
			/*IoAcceptor acceptor = new IoAcceptorImpl(8899);
			acceptor.setIoHandler(new LogicHandler());
			
			acceptor.start();*/
			
			IoService acceptor = new IoConnector(8899);
			acceptor.setIoHandler(new LogicHandler());
			acceptor.start();
			
			System.out.println();
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 

	}

}
