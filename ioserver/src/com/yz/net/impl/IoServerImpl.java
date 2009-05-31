package com.yz.net.impl;


import java.io.IOException;
import java.net.ServerSocket;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;



/**
 * <p>
 * ���ڹ����������ľ���ʵ����
 * </p>
 * <br>
 * @author ����@ritsky
 *
 */
public class IoServerImpl extends AbstractIoServer implements Runnable{
	
	/**����������Socket*/
	private ServerSocketChannel ssChannel;
	
	/**ѡ����*/
	private Selector selector;
	
	private boolean isRunning;
	
	
	
	public IoServerImpl() throws Exception{
		super();
	}

	
	public IoServerImpl(int port) throws Exception {
		this();
		bind(port);
	}
	
	
	@Override
	public void start() throws IOException {
		startTimer();              //������ʱ��
		
		if(getBindAddress() == null) {
			throw new IOException("û�а󶨵�ַ");
		}
		
	
		selector = Selector.open();         //��ѡ����
		
		ssChannel = ServerSocketChannel.open();
		ssChannel.configureBlocking(false);       //�趨Ϊ������
		ServerSocket ss = ssChannel.socket();
		ss.bind(getBindAddress());
		
				
		ssChannel.register(selector, SelectionKey.OP_ACCEPT);
		
		this.startIoDispatchers();
		
		Thread t = new Thread(this, "IoAcceptor");
		t.start();
		
		this.isStart = true;
	}
	
	
	
	
	public void stop() {
		synchronized (stopLock) {
			if(this.isRunning) {
				this.isStart = false;
				this.isRunning = false;
				this.selector.wakeup();
				getTimer().cancel();
				
				this.closeAllSession(); //һ�����;Ͳ���������ֻ�����лỰ�ر�Ϊֹ
				stopIoDispatchers();
				
				try {
					this.selector.close();
					this.ssChannel.close();
				} catch (IOException e) {}
			}
		}
	}
	
	
	
	

	@Override
	public void run() {
		
		isRunning = true;
    	while(isRunning){
    		
    		try {
				selector.select();
			} catch (IOException e) {
				//TODO:��¼���쳣
				e.printStackTrace();
			}
    		
    		for(Iterator<SelectionKey> i = selector.selectedKeys().iterator(); i.hasNext();) {
    			SelectionKey sk = i.next();
    			i.remove();
    			
    			if(sk.isValid() == false) {
    				//TODO:�����Ƿ��׳��쳣
    			}
    			
    			if(sk.isAcceptable()) {
    				
    				SocketChannel sc = null;
    				try {
						sc = this.ssChannel.accept();
						sc.configureBlocking(false);
					} catch (IOException e) {
						//TODO:��¼�´��쳣
						e.printStackTrace();
					}
					
					if(sc == null) {
						continue;
					}
					
					
					IoSessionImpl session = newIoSession(sc);
					scheduleToDispatcher(session);   //��һ�����������ų�ע��
    			}
    		}
    	}	
	}

	
	/**
	 * ����һ���µ�IoSession
	 * @param sc
	 * @return
	 */
	private IoSessionImpl newIoSession(SocketChannel sc) {
		long id = getNextSessionId();
		
		IoSessionImpl session = new IoSessionImpl(id, sc, this);
		return session;
	}
	
	

}
