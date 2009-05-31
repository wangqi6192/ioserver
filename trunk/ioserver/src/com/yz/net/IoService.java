package com.yz.net;

import java.io.IOException;
import java.net.SocketAddress;

/**
 * <p>
 * IO接收器，通过接收器，可以绑定本地IP地址和端口，来对网络进行监听，同时还可以设置一些网络处理相关的<br>
 * 处理者，比如ProtocolHandler(协议处理者)，比如OverTimeHandler(超时处理者)，IoHandler(消息处理者)<br>
 * 此类也是整个框架的启动者
 * 
 * 注意：<br>
 * 1.IoAcceptor目前版本只能监听一个端口，之后的版本考虑加入可以同时监听不同的地址<br>
 * 2.ProtocolHandler与IoHandler一定要进行设置，否则不起启动框架 <br>
 * 3.如果OverTimeHandler未被设置，框架会默认提供一个，默认的处理者以5分钟作为超时的判断条件，默认处理<br>
 *   者只对读写均无操作发生的超时作出了处理<br>
 * 4.默认的OverTimeHandler只是当发生超时时关闭IoSession<br>
 * </p>
 * <br>
 * @author 胡玮@ritsky
 *
 */
public interface IoService {
	/**
	 * <p>
	 * 绑定IP地址
	 * </p>
	 * <br>
	 * @param address
	 * @throws IOException
	 */
	public void bind(SocketAddress address) throws IOException;
	
	
	/**
	 * <p>
	 * 绑定在给定端口上绑定本机地址
	 * </p>
	 * <br>
	 * @param port
	 * @throws IOException
	 */
	public void bind(int port) throws IOException;
	
	
	/**
	 * <p>
	 * 获取绑地址
	 * </p>
	 * <br>
	 * @return
	 */
	public SocketAddress getBindAddress();
	
	
	/**
	 * <p>
	 * 获取一个协议处理者
	 * </p>
	 * <br>
	 * @return
	 */
	public ProtocolHandler getProtocolHandler();
	
	
	/**
	 * <p>
	 * 设置一个协义处理者
	 * </p>
	 * <br>
	 * @param handler
	 * @return
	 */
	public void setProtocolHandler(ProtocolHandler handler);
	
	
	/**
	 * <p>
	 * 获取一个io处理者
	 * </p>
	 * <br>
	 * @return
	 */
	public IoHandler getIoHandler();
	
	
	/**
	 * <p>
	 * 设置一个io处理者
	 * </p>
	 * <br>
	 * @param handler
	 */
	public void setIoHandler(IoHandler handler);
	
	
	
	
	/**
	 * <p>
	 * 获得超时处理者
	 * </p>
	 * <br>
	 * @return
	 */
	public OverTimeHandler getOverTimeHandler();
	
	/**
	 * <p>
	 * 设置超时处理者
	 * </p>
	 * <br>
	 * @param handler
	 */
	public void setOverTimeHandler(OverTimeHandler handler);
	

	
	
	/**
	 * <p>
	 * 启动框架
	 * </p>
	 * <br>
	 * @throws Exception
	 */
	public void start() throws Exception;
	
	
	/**
	 * <p>
	 * 停止框架
	 * </p>
	 * <br>
	 * @throws Exception
	 */
	public void stop() throws Exception;
	
	
	/**
	 * <p>
	 * 获取一个IO会话
	 * </p>
	 * <br>
	 * @param ioSessionId
	 * @return
	 */
	public IoSession getIoSession(long ioSessionId);
}
