package example;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import com.yz.net.IoFuture;
import com.yz.net.IoHandlerAdapter;
import com.yz.net.IoSession;
import com.yz.net.NetMessage;
import com.yz.net.ProtocolHandler;
import com.yz.net.expand.ClientIoSession;
import com.yz.net.expand.IoConnector;


/**
 * <p>
 * Echo客户端例程，为了方便写在一个文件中，把一些消息和Handler都定在一个文件中，其实Handler和NetMessage只需定义一份
 * 客户端与服务器端都可以共用
 * </p>
 * <br>
 * @author 胡玮@ritsky
 *
 */
public class EchoClientExample {

	public static IoConnector connector = null;
	
	public static void main(String[] args) {
		try {
			//新建一个连接器
			connector = new IoConnector();
			SocketAddress address = new InetSocketAddress("127.0.0.1", 8899);
			
			//绑定一个需要连接的服务器地址
			connector.bind(address);
			
			//设置协议处理器
			connector.setProtocolHandler(new EchoProtocol());
			
			//设置数据处理器
			connector.setIoHandler(new EchoHandler());
			
			//开始连接器
			connector.start();
			
			//生成一个客户端会话
			ClientIoSession session = IoConnector.newSession(connector);
			
			//发出连接请求
			IoFuture future = session.connect();
			
			//等待连接完成
			future.await();
			
			//连接时发生错误后的处理
			if(future.isError()) {
				System.out.println(future.getThrowable().toString());
				return;
			}
			
			
			while(true) {
				
				InputStreamReader stream = new InputStreamReader(System.in);
				BufferedReader reader = new BufferedReader(stream);
				
				String str = reader.readLine();
				session.write(new EchoMessage(str));   //发送echo
			}
			
		}
		catch(Exception e) {
			e.printStackTrace();
		}

	}


	public static class EchoHandler extends IoHandlerAdapter {

		@Override
		public void ioSessionClosed(IoFuture future) {
			connector.stop();
			System.exit(0);
		}

		@Override
		public void messageReceived(IoSession session, NetMessage msg) {
			EchoMessage echoMsg = (EchoMessage) msg;

			String echostr = echoMsg.getEchoStr();

			System.out.println(echostr);
		}

	}


	/**定义协议解析器*/
	public static class EchoProtocol implements ProtocolHandler {
		@Override
		public List<NetMessage> onData(ByteBuffer data, IoSession session) {

			ArrayList<NetMessage> list = null;
			while(true) {
				if(data.remaining() < 2) {
					break;
				}
				int len = data.getShort();

				if(data.remaining() < len) {
					break;
				}



				byte[] echodata = new byte[len];
				data.get(echodata);

				NetMessage message = null;
				try {
					message = new EchoMessage(new String(echodata, "UTF-8"));
					if(list == null) {
						list = new ArrayList<NetMessage>();
					}
					list.add(message);
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}


			return list;
		}

	}

	/**定义消息*/
	public static class EchoMessage implements NetMessage {

		private String echostr;

		public EchoMessage(String echostr) {
			this.echostr = echostr;
		}

		public String getEchoStr() {
			return echostr;
		}

		@Override
		public byte[] getContent() {
			byte[] content = null;
			try {
				byte[] echodata = echostr.getBytes("UTF-8");
				content = new byte[2 + echodata.length];


				int len = echodata.length;
				content[0] = (byte) ((len >>> 8) & 0xFF);
				content[1] = (byte) ((len >>> 0) & 0xFF);

				System.arraycopy(echodata, 0, content, 2, len);

			} catch (UnsupportedEncodingException e) {}

			return content;
		}

	}

}
