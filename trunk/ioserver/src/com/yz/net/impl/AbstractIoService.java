package com.yz.net.impl;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import com.yz.net.IoFuture;
import com.yz.net.IoService;
import com.yz.net.IoHandler;
import com.yz.net.IoSession;
import com.yz.net.OverTimeHandler;
import com.yz.net.ProtocolHandler;

/**
 * <p>
 * 服务器抽象，提供基本的的服务功能
 * </p>
 * <br>
 * @author 胡玮@ritsky
 *
 */
public abstract class AbstractIoService implements IoService, DispatcherEventlListener{
	/**是否启动*/
	protected volatile boolean isStart;

	/**监听地址*/
	private SocketAddress address;
	
	/**IO处理线程管理器*/
	private IoReadWriteMachineManager dispatcherManager;
	
	/**io处理者*/
	private IoHandler iohandler;
	
	/**超时处理者，提供默认的超时处理者*/
	private OverTimeHandler overtimeHandler = new DefaultOverTimeHandler();
	
	/**协议处理者*/
	private ProtocolHandler protocolHandler;
	
	/**定时器*/
	private Timer timer;
	
	//TODO:初始化时给一个能预测到的参数值，以便提高map的访问性能 //TODO:
	private ConcurrentHashMap<Long, IoSession> ioSessionMap = new ConcurrentHashMap<Long, IoSession>();
	
	/**提供只读Map*/
	private Map<Long, IoSession> readOnlyIoSessionMap = java.util.Collections.unmodifiableMap(ioSessionMap);
	
	/**ID提供器*/
	private AtomicLong nextId = new AtomicLong(0);
	
	protected Object stopLock = new Object();
	
	
	/**
	 * 启动定时器
	 */
	protected void startTimer() {
		if(timer == null) {
			timer = new Timer(true);
		}
	}
	
	protected void stopTimer() {
		if(timer != null) {
			timer.cancel();
		}
	}
	
	/**
	 * 启动IO处理线程
	 */
	protected void startIoReadWriteMachines() {
		dispatcherManager.start();
	}
	
	/**
	 * 停止IO处理线程
	 * @throws IOException
	 */
	protected void stopIoReadWriteMachines(){
		dispatcherManager.stop();
	}
	
	
	/**
	 * 初始经读写机
	 * @param readwriteThreadNum
	 * @throws Exception
	 */
	protected void initIoReadWriteMachines(int readwriteThreadNum) throws Exception{
		this.dispatcherManager.init(readwriteThreadNum);
	}
	
	
	
	/**
	 * 关闭所有会话，方法为阻塞
	 */
	protected void closeAllSession()  {
		Iterator<Long> iter = ioSessionMap.keySet().iterator();
		while(iter.hasNext()) {
			long sessionId = iter.next();
			IoSession session = ioSessionMap.get(sessionId);
			if(session != null) {
				IoFuture future = session.close();
				future.await(2000);
			}
		}
	}
	
	
	/**是否启动*/
	public boolean isStart() {
		return isStart;
	}
	
	/**
	 * 获得定时器，如果对像没有启动，返回的则是null
	 * @return
	 */
	Timer getTimer() {
		return timer;
	}

	
	protected long getNextSessionId() {
		return nextId.incrementAndGet();
	}
	
	public AbstractIoService() {
		dispatcherManager = new IoReadWriteMachineManager(this);
		//dispatcherManager.init(readwriteThreadNum);
		
	}

	
	/*public AbstractIoService(int readwriteThreadNum, int port) throws Exception {		
		this(readwriteThreadNum);
		bind(port);
	}*/
	
	@Override
	public SocketAddress getBindAddress() {
		return this.address;
	}
	
	@Override
	public void bind(SocketAddress address) throws IOException {
		if(address == null) {
			throw new IllegalArgumentException("地址不能为空");
		}
		this.address = address;
	}

	
	@Override
	public void bind(int port) throws IOException {
		SocketAddress _address = new InetSocketAddress(port);
		bind(_address);
	}

	
	protected IoReadWriteMachineManager getIoDispatcherManager() {
		return this.dispatcherManager;
	}

	@Override
	public IoHandler getIoHandler() {
		return this.iohandler;
	}

	
	@Override
	public OverTimeHandler getOverTimeHandler() {
		return this.overtimeHandler;
	}

	@Override
	public ProtocolHandler getProtocolHandler() {
		return this.protocolHandler;
	}

	
	@Override
	public void setIoHandler(IoHandler handler) {
		this.iohandler = handler;
	}

	@Override
	public void setOverTimeHandler(OverTimeHandler handler) {
		this.overtimeHandler = handler;
	}

	@Override
	public void setProtocolHandler(ProtocolHandler handler) {
		this.protocolHandler = handler;
	}

	
	
	public void scheduleToDispatcher(IoSessionImpl session) {
		if(session == null) {
			return;
		}
		
		IoReadWriteMachine dispatcher = getIoDispatcherManager().getNextDispatcher();
		
		session.setOwnerDispatcher(dispatcher);   //设置会话的归属发报机
		
		session.allocatInBuffer();                //分配输入Buffer
		
		dispatcher.scheduleRegister(session);     //排程注册
	}

	
	@Override
	public IoSession getIoSession(long ioSessionId) {
		return ioSessionMap.get(ioSessionId);
	}




	@Override  //IoSession在发报机中注册后触发
	public void onRegisterSession(IoSessionImpl session) {
		if(session == null) {
			return;
		}
		
		this.ioSessionMap.putIfAbsent(session.getId(), session);
		
		//TODO:触发IoHandler事件
	}





	@Override  //IoSession真正在发报机中关闭并移除时触发
	public void onRemoveSession(IoSessionImpl session) {
		if(session == null) {
			return;
		}
		
		this.ioSessionMap.remove(session.getId());
		
		//TODO:触发相应事件
	}


	/**
	 * 提供认的超时处理者
	 * @author 胡玮@ritsky
	 *
	 */
	class DefaultOverTimeHandler implements OverTimeHandler {

		@Override
		public long bothOverTime() {
			return 5 * 60 * 1000;
		}

		@Override
		public long interval() {
			return 5 * 1000;
		}

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

		@Override
		public long readOverTime() {
			return -1;
		}

		@Override
		public long writerOverTime() {
			return -1;
		}
		
	}
}
