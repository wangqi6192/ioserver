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
			//׼������ķ�������ַ
			SocketAddress address = new InetSocketAddress("127.0.0.1", 80);
			
			IoService acceptor = new IoConnector();
			acceptor.bind(address);
			
			//����Э�鴦���ߣ����Բ�����
			acceptor.setProtocolHandler(new Protocol());
			
			//�������ݴ�����,һ��Ҫ����
			acceptor.setIoHandler(new DataHandler());
			
			//��������
			acceptor.start();
			
			
			//�����µ����ӵ����������ʱ����Ҫ����һ�����ϲ�������ĻỰ
			ClientIoSession session = IoConnector.newSession((IoConnector) acceptor);
			
			//�����ϲ������������Ϊ�첽����
			IoFuture future = session.connect();
						
			//�����Ҫͬ���ȴ�������ɿ��Ե������·���
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
			//TODO:��һ���Ự�رպ󱻴����ķ���
			
		}

		@Override
		public void messageReceived(IoSession session, NetMessage msg) {
			//TODO:������Э�������ʱ���밴������ĿҪ����ɴ˷���
			
		}

		@Override
		public void messageReceived(IoSession session, byte[] msgdata) {
			//TODO:��û��Э�������ʱ���밴������ĿҪ����ɴ˷���
			
		}	
	}
	
	public static class Protocol implements ProtocolHandler {

		@Override
		public boolean isClose() {
			//TODO:�����Ҫ�����ӣ��뷴��false�������Ҫ�������뷵��true
			return false;
		}

		@Override
		public List<NetMessage> onData(ByteBuffer data, IoSession session) {
			//TODO:���︺��跿�����ݽ��н��������γ�һ���������Ϣ��
			return null;
		}
		
	}
}
