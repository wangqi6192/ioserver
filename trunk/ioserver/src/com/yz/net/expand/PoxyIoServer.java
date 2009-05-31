package com.yz.net.expand;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.SocketChannel;
import com.yz.net.impl.AbstractIoServer;



/**
 * <p>
 * ������������ܶ�ʱ������ϣ�������µ�ͨѶ��ʽ�����ֲ�ϣ������ԭ�з������Ĵ��������룬��ʱ��������<br>
 * ���������������һ���µķ�������������������԰���Ҫ���ӵ��ϲ��������ַ�����趨��ԭ���ݽ��д���<br>
 * �Ĵ����߶�������
 * 
 * IoAcceptor acceptor = new PoxyIoAcceptor(8899);
 * acceptor.setIoHandler(new DataHandler());
 * acceptor.start();
 * 
 * </p>
 * <br>
 * @author ����@ritsky
 *
 */
public class PoxyIoServer extends AbstractIoServer {

	/**
	 * <p>
	 * ��ָ���Ĵ���������������µĴ���Ự���������Ϊ�գ�����null�����������û������Ҳ����null
	 * </p>
	 * <br>
	 * @param acceptor ���������<br>
	 * @return PoxyIoSession ����Ự<br>
	 * @throws IOException
	 */
	public static PoxyIoSession newSession(PoxyIoServer acceptor) throws IOException{
		
		if(acceptor == null) {
			return null;
		}
		
		if(!acceptor.isStart()) {
			return null;
		}

		long sessionId = acceptor.getNextSessionId();

		SocketChannel sc = SocketChannel.open();
		sc.configureBlocking(false);

		SocketAddress bindAddress = acceptor.getBindAddress();

		PoxyIoSession session = new PoxyIoSession(sessionId, sc, acceptor, bindAddress);

		return session;
	}

	
	public PoxyIoServer() throws Exception {
		super();
	}
	
	public PoxyIoServer(int port) throws Exception {
		super(port);
	}

	@Override
	public void start() throws Exception {
		this.startTimer();                //������ʱ��
		this.startIoDispatchers();
		this.isStart = true;
	}

	
	@Override
	public void stop(){
		synchronized (stopLock) {
			this.isStart = false;
			this.stopTimer();
			
			this.closeAllSession();
			
			this.stopIoDispatchers();
		}
		
	}
}
