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
 * 用于构建服务器的具体实现类
 * </p>
 * <br>
 * @author 胡玮@ritsky
 *
 */
public class IoServerImpl extends AbstractIoServer implements Runnable{
	
	/**服务器监听Socket*/
	private ServerSocketChannel ssChannel;
	
	/**选择器*/
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
		startTimer();              //启动定时器
		
		if(getBindAddress() == null) {
			throw new IOException("没有绑定地址");
		}
		
	
		selector = Selector.open();         //打开选择器
		
		ssChannel = ServerSocketChannel.open();
		ssChannel.configureBlocking(false);       //设定为非阻塞
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
				
				this.closeAllSession(); //一但调和就产生阻塞，只到所有会话关闭为止
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
				//TODO:记录下异常
				e.printStackTrace();
			}
    		
    		for(Iterator<SelectionKey> i = selector.selectedKeys().iterator(); i.hasNext();) {
    			SelectionKey sk = i.next();
    			i.remove();
    			
    			if(sk.isValid() == false) {
    				//TODO:考虑是否抛出异常
    			}
    			
    			if(sk.isAcceptable()) {
    				
    				SocketChannel sc = null;
    				try {
						sc = this.ssChannel.accept();
						sc.configureBlocking(false);
					} catch (IOException e) {
						//TODO:记录下此异常
						e.printStackTrace();
					}
					
					if(sc == null) {
						continue;
					}
					
					
					IoSessionImpl session = newIoSession(sc);
					scheduleToDispatcher(session);   //在一个发报机中排程注册
    			}
    		}
    	}	
	}

	
	/**
	 * 产生一个新的IoSession
	 * @param sc
	 * @return
	 */
	private IoSessionImpl newIoSession(SocketChannel sc) {
		long id = getNextSessionId();
		
		IoSessionImpl session = new IoSessionImpl(id, sc, this);
		return session;
	}
	
	

}
