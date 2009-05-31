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
 * 代理IoSession，这是IOServer框架的一个扩展补充，在很多情况下，我们需要在不修改原服务器的情况下<br>
 * 而加入另外一种形式的通讯协议或方工，那么有一种策略是提供代理服务器来与原有的服务器进行通讯，客<br>
 * 户端就于代理服务器通讯，此扩充的每一个PoxyIoSession都代理一个上层服务器的IoSession
 * </p>
 * <br>
 * @author 胡玮@ritsky
 *
 */
public class PoxyIoSession extends IoSessionImpl {
	
	/**被接受器绑定的地址*/
	private SocketAddress bindAddress;
	
	private ConnectFuture connectFuture = new ConnectFuture(this);
	
	private AtomicBoolean isConnecting = new AtomicBoolean(false);        //是否正在连接
	
	
	PoxyIoSession(long id, SocketChannel channel, AbstractIoServer acceptor, SocketAddress address) {
		super(id, channel, acceptor);
		this.bindAddress = address;
	}
	
	/**
	 * <p>
	 * 连接上层服务器，返回异步运算结果。如果在连接中出错，IoFuture.isError()可检查出来
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
			connectFuture.setComplete(e);     //设置完成
		}
		return connectFuture;
	}
	
	
	/**
	 * <p>
	 * 获得连接的异步运算结果，只有处于连接中时才能拿到，其它情况返回null
	 * </p>
	 * <br>
	 * @return
	 */
	public IoFuture getConnectFuture() {
		if(isConnecting.get()) {   //只有处在正在连接中才能拿到异步运算结果
			return connectFuture;
		}
		return null;
	}
}
