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
 * �����������ṩ�����ĵķ�����
 * </p>
 * <br>
 * @author ����@ritsky
 *
 */
public abstract class AbstractIoServer implements IoService, DispatcherEventlListener{
	/**�Ƿ�����*/
	protected volatile boolean isStart;

	/**������ַ*/
	private SocketAddress address;
	
	/**IO�����̹߳�����*/
	private IoReadWriteMachineManager dispatcherManager;
	
	/**io������*/
	private IoHandler iohandler;
	
	/**��ʱ�����ߣ��ṩĬ�ϵĳ�ʱ������*/
	private OverTimeHandler overtimeHandler = new DefaultOverTimeHandler();
	
	/**Э�鴦����*/
	private ProtocolHandler protocolHandler;
	
	/**��ʱ��*/
	private Timer timer;
	
	//TODO:��ʼ��ʱ��һ����Ԥ�⵽�Ĳ���ֵ���Ա����map�ķ�������
	private ConcurrentHashMap<Long, IoSession> ioSessionMap = new ConcurrentHashMap<Long, IoSession>();
	
	/**�ṩֻ��Map*/
	private Map<Long, IoSession> readOnlyIoSessionMap = java.util.Collections.unmodifiableMap(ioSessionMap);
	
	/**ID�ṩ��*/
	private AtomicLong nextId = new AtomicLong(0);
	
	protected Object stopLock = new Object();
	
	
	/**
	 * ������ʱ��
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
	 * ����IO�����߳�
	 */
	protected void startIoDispatchers() {
		dispatcherManager.start();
	}
	
	/**
	 * ֹͣIO�����߳�
	 * @throws IOException
	 */
	protected void stopIoDispatchers(){
		dispatcherManager.stop();
	}
	
	
	/**
	 * �ر����лỰ������Ϊ����
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
	
	
	/**�Ƿ�����*/
	public boolean isStart() {
		return isStart;
	}
	
	/**
	 * ��ö�ʱ�����������û�����������ص�����null
	 * @return
	 */
	Timer getTimer() {
		return timer;
	}

	
	protected long getNextSessionId() {
		return nextId.incrementAndGet();
	}
	
	public AbstractIoServer() throws Exception{
		dispatcherManager = new IoReadWriteMachineManager(this);
		dispatcherManager.init();
		
	}

	
	public AbstractIoServer(int port) throws Exception {
		this();
		bind(port);
	}
	
	@Override
	public SocketAddress getBindAddress() {
		return this.address;
	}
	
	@Override
	public void bind(SocketAddress address) throws IOException {
		if(address == null) {
			throw new IllegalArgumentException("��ַ����Ϊ��");
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
		
		session.setOwnerDispatcher(dispatcher);   //���ûỰ�Ĺ���������
		
		session.allocatInBuffer();                //��������Buffer
		
		dispatcher.scheduleRegister(session);     //�ų�ע��
	}

	
	@Override
	public IoSession getIoSession(long ioSessionId) {
		return ioSessionMap.get(ioSessionId);
	}




	@Override  //IoSession�ڷ�������ע��󴥷�
	public void onRegisterSession(IoSessionImpl session) {
		if(session == null) {
			return;
		}
		
		this.ioSessionMap.putIfAbsent(session.getId(), session);
		
		//TODO:����IoHandler�¼�
	}





	@Override  //IoSession�����ڷ������йرղ��Ƴ�ʱ����
	public void onRemoveSession(IoSessionImpl session) {
		if(session == null) {
			return;
		}
		
		this.ioSessionMap.remove(session.getId());
		
		//TODO:������Ӧ�¼�
	}


	/**
	 * �ṩ�ϵĳ�ʱ������
	 * @author ����@ritsky
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
			session.close();     //��ʱ��رջỰ
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
