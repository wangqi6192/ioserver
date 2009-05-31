package com.yz.net.expand;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.SocketChannel;
import java.util.concurrent.atomic.AtomicBoolean;

import com.yz.net.IoFuture;
import com.yz.net.impl.AbstractIoServer;
import com.yz.net.impl.IoServerImpl;
import com.yz.net.impl.IoSessionImpl;

/**
 * <p>
 * ����IoSession������IOServer��ܵ�һ����չ���䣬�ںܶ�����£�������Ҫ�ڲ��޸�ԭ�������������<br>
 * ����������һ����ʽ��ͨѶЭ��򷽹�����ô��һ�ֲ������ṩ�������������ԭ�еķ���������ͨѶ����<br>
 * ���˾��ڴ��������ͨѶ���������ÿһ��PoxyIoSession������һ���ϲ��������IoSession
 * </p>
 * <br>
 * @author ����@ritsky
 *
 */
public class PoxyIoSession extends IoSessionImpl {
	
	/**���������󶨵ĵ�ַ*/
	private SocketAddress bindAddress;
	
	private ConnectFuture connectFuture = new ConnectFuture(this);
	
	private AtomicBoolean isConnecting = new AtomicBoolean(false);        //�Ƿ���������
	
	
	PoxyIoSession(long id, SocketChannel channel, AbstractIoServer acceptor, SocketAddress address) {
		super(id, channel, acceptor);
		this.bindAddress = address;
	}
	
	/**
	 * <p>
	 * �����ϲ�������������첽������������������г���IoFuture.isError()�ɼ�����
	 * </p>
	 * <br>
	 * @return IoFuture
	 */
	public IoFuture connect(){
		try {
			if(isConnecting.compareAndSet(false, true)) {
				getChannel().configureBlocking(false);
				getOwnerAcceptor().scheduleToDispatcher(this);
			}
		}
		catch(IOException e) {
			connectFuture.setComplete(e);     //�������
		}
		return connectFuture;
	}
	
	
	/**
	 * <p>
	 * ������ӵ��첽��������ֻ�д���������ʱ�����õ��������������null
	 * </p>
	 * <br>
	 * @return
	 */
	public IoFuture getConnectFuture() {
		if(isConnecting.get()) {   //ֻ�д������������в����õ��첽������
			return connectFuture;
		}
		return null;
	}
}
