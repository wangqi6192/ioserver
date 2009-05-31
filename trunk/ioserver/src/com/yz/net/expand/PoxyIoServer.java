package com.yz.net.expand;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.SocketChannel;
import com.yz.net.impl.AbstractIoServer;



/**
 * <p>
 * 代理接收器，很多时候，我们希望加入新的通讯方式，但又不希望更改原有服务器的代码来加入，这时可以利用<br>
 * 代理接收器来构建一个新的服务器，代理接收器可以绑定需要连接的上层服务器地址，并设定对原数据进行处理<br>
 * 的处理者对像，如下
 * 
 * IoAcceptor acceptor = new PoxyIoAcceptor(8899);
 * acceptor.setIoHandler(new DataHandler());
 * acceptor.start();
 * 
 * </p>
 * <br>
 * @author 胡玮@ritsky
 *
 */
public class PoxyIoServer extends AbstractIoServer {

	/**
	 * <p>
	 * 在指定的代理接收器中生成新的代理会话，如果参数为空，返回null，如果连接器没有启动也返回null
	 * </p>
	 * <br>
	 * @param acceptor 代理接收器<br>
	 * @return PoxyIoSession 代理会话<br>
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
		this.startTimer();                //启动定时器
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
