package com.yz.net;

import java.net.SocketAddress;

/**
 * <p>
 * IoServer配置，一份配置可以同时启动N份服务。
 * </p>
 * <br>
 * @author 胡玮@ritsky
 *
 */
public class Configure {
	/**超时间隔检查时间*/
	private long overTimeIntervalCheckTime = 5 * 1000;
	
	
	/**读超时时间*/
	private long readOverTime = Long.MAX_VALUE;
	
	/**写超时时间*/
	private long writeOverTime = Long.MAX_VALUE;
	
	/**读写超时时间*/
	private long bothOverTime = Long.MAX_VALUE;

	/**绑定地址*/
	private SocketAddress address;
	
	/**IO处理者*/
	private IoHandler ioHandler;
	
	/**协议处理者*/
	private ProtocolHandler protocolHandler;
	
	/**超时处理者*/
	private OverTimeHandler overTimeHandler;
	
	
	public Configure() {
		overTimeIntervalCheckTime = 5 * 1000;
		bothOverTime =  2 * 60 * 1000;
		 
		overTimeHandler = new DefaultOverTimeHandler();
	}
	
	public long getOverTimeIntervalCheckTime() {
		return overTimeIntervalCheckTime;
	}




	public void setOverTimeIntervalCheckTime(long overTimeIntervalCheckTime) {
		this.overTimeIntervalCheckTime = overTimeIntervalCheckTime;
	}




	public long getReadOverTime() {
		return readOverTime;
	}




	public void setReadOverTime(long readOverTime) {
		this.readOverTime = readOverTime;
	}




	public long getWriteOverTime() {
		return writeOverTime;
	}




	public void setWriteOverTime(long writeOverTime) {
		this.writeOverTime = writeOverTime;
	}




	public long getBothOverTime() {
		return bothOverTime;
	}




	public void setBothOverTime(long bothOverTime) {
		this.bothOverTime = bothOverTime;
	}




	public SocketAddress getAddress() {
		return address;
	}




	public void setAddress(SocketAddress address) {
		this.address = address;
	}




	public IoHandler getIoHandler() {
		return ioHandler;
	}




	public void setIoHandler(IoHandler ioHandler) {
		this.ioHandler = ioHandler;
	}




	public ProtocolHandler getProtocolHandler() {
		return protocolHandler;
	}




	public void setProtocolHandler(ProtocolHandler protocolHandler) {
		this.protocolHandler = protocolHandler;
	}




	public OverTimeHandler getOverTimeHandler() {
		return overTimeHandler;
	}




	public void setOverTimeHandler(OverTimeHandler overTimeHandler) {
		this.overTimeHandler = overTimeHandler;
	}

	
	public void start(IoService service) throws Exception {
		service.setConfigure(this);
		service.start();
	}
	
	
	/**
	 * 提供认的超时处理者
	 * @author 胡玮@ritsky
	 *
	 */
	class DefaultOverTimeHandler implements OverTimeHandler {

		@Override
		public void onBothOverTime(IoSession session) {
			session.close();     //超时后关闭会话
		}

		@Override
		public void onReadOverTime(IoSession session) {
			
		}

		@Override
		public void onWriterOverTime(IoSession session) {
			
		}
	}
}
