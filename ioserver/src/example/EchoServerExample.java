package example;

import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import com.yz.net.Configure;
import com.yz.net.IoHandlerAdapter;
import com.yz.net.IoService;
import com.yz.net.IoSession;
import com.yz.net.NetMessage;
import com.yz.net.ProtocolHandler;
import com.yz.net.impl.IoServerImpl;

/**
 * <p>
 * Echo服务器例程，为了方便写在一个文件中，把一些消息和Handler都定在一个文件中，其实Handler和NetMessage只需定义一份
 * 客户端与服务器端都可以共用
 * </p>
 * <br>
 * @author 胡玮@ritsky
 *
 */
public class EchoServerExample {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			//构建一个服务器端的服务
			Configure config = new Configure();
			config.setAddress(new InetSocketAddress("127.0.0.1", 8899));
			config.setProtocolHandler(new EchoProtocol());
			config.setIoHandler(new EchoHandler());
			
			IoService service = new IoServerImpl();
			
			config.start(service);
			
			while(true) {
				Thread.sleep(1000);
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}

	}

	
	/**
	 * 数据处理器
	 * @author
	 *
	 */
	public static class EchoHandler extends IoHandlerAdapter {
		
		@Override
		public void messageReceived(IoSession session, NetMessage msg) {
			EchoMessage echoMsg = (EchoMessage) msg;
			
			String echostr = echoMsg.getEchoStr();
			
			EchoMessage sendmsg = null;
			if(echostr.toLowerCase().equals("quit") ||
					echostr.toLowerCase().equals("exit")) {
				sendmsg = new EchoMessage("Byte~!!!");
				session.write(sendmsg);
				session.close();
			}
			else {
				System.out.println("=== " + echoMsg.getEchoStr() + " ===");
				sendmsg = new EchoMessage("Echo:" + echoMsg.getEchoStr());
				session.write(sendmsg);
			}
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
