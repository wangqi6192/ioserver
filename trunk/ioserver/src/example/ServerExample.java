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

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			//��һ�������˿�
			IoService acceptor = new IoServerImpl(8899);
			
			//����Э�鴦���ߣ����Բ�����
			acceptor.setProtocolHandler(new Protocol());
			
			//������Ϣ�����ߣ�һ��Ҫ����
			acceptor.setIoHandler(new DataHandler());
			
			//����
			acceptor.start();
			
			
			while(true) {
				Thread.sleep(1000);
			}
			
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
			ExampleMessage message = (ExampleMessage) msg;
			
			int returnvalue = message.getNumber() + 1;
			
			
			ExampleMessage sendMsg = new ExampleMessage(returnvalue);
			
			session.write(sendMsg);
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
