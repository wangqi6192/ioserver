package com.yz.net.impl;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.yz.net.IoFuture;
import com.yz.net.expand.ConnectFuture;
import com.yz.net.expand.PoxyIoSession;

/**
 * <p>
 * һ��IO�Ķ�д�����ӵĴ����
 * </p>
 * <br>
 * @author ����@ritsky
 *
 */
class IoReadWriteMachine implements Runnable {
	
	/**���������*/
	private static final int MAX_OUT_SIZE = 1024;
	
	private boolean isRunning;
	
	/**�������б�*/
	private HashSet<DispatcherEventlListener> listenerSet;
	
	/**�ڴ������*/
	private MemoryManagerInface memManager;
	
	/**�ȴ�ע�����*/
	private ConcurrentLinkedQueue<IoSessionImpl> waitRegQueue;
	
	/**�ȴ�ɾ������*/
	//private ConcurrentLinkedQueue<CloseFuture> waitRemoveQueue;
	
	/**�ȴ����ƶ���*/
	private ConcurrentLinkedQueue<IoSessionImpl> waitControlQueue;
	
	/**�ȴ���ʱ����*/
	private ConcurrentLinkedQueue<IoSessionImpl> waitOverTimeQueue;
	
	/**�ȴ����Ӷ���*/
	private ConcurrentLinkedQueue<IoFuture> waitConnectQueue;
	
	private Selector selector;
	
	/**����ܳа���IoSession����*/
	private int maxIoSessionNum;
	
	/**����IoSession�ļ�����*/
	private int concurrentIoSessionCount;
	
	
	void addListener(DispatcherEventlListener listener) {
		listenerSet.add(listener);
	}
	
	
	/**
	 * <p>
	 * ��ʼ��IO������
	 * </p>
	 * <br>
	 * @param maxIoSessionNum
	 * @throws Exception
	 */
	public void init(int maxIoSessionNum) throws Exception {
		
		listenerSet = new HashSet<DispatcherEventlListener>();
		memManager = new MemoryManagerLinked(1024 * 10, 0, true);
		
		waitRegQueue = new ConcurrentLinkedQueue<IoSessionImpl>();
		
		//waitRemoveQueue = new ConcurrentLinkedQueue<CloseFuture>();
		
		waitControlQueue = new ConcurrentLinkedQueue<IoSessionImpl>();
		waitOverTimeQueue = new ConcurrentLinkedQueue<IoSessionImpl>();
		
		waitConnectQueue = new ConcurrentLinkedQueue<IoFuture>();
		
		
		selector = Selector.open();
		
		this.maxIoSessionNum = maxIoSessionNum;
		
	}
	
	
	/**
	 * <p>
	 * �ų̹رղ��Ƴ�IO�Ự
	 * </p>
	 * <br>
	 * @param closeFuture
	 *//*
	public void scheduleRemove(CloseFuture closeFuture) {
		
		if(closeFuture == null) {
			return;
		}
		
		waitRemoveQueue.offer(closeFuture);
		this.wakeup();
	}*/
	
	
	public void scheduleOverTime(IoSessionImpl session) {
		if(session == null) {
			return;
		}
		waitOverTimeQueue.offer(session);
		this.wakeup();
	}
	
	
	public void wakeup() {
		selector.wakeup();
	}
	
	
	/**
	 * <p>
	 * �ر�IO������
	 * </p>
	 * <br>
	 * @throws IOException
	 */
	public void close() {
		this.isRunning = false;
		wakeup();
		
		try {
			this.selector.close();
		} catch (IOException e) {}
	}

	
	/**
	 * <p>
	 * ��ȡ�ڴ������
	 * </p>
	 * <br>
	 * @return
	 */
	public MemoryManagerInface getMemoryManager() {
		return memManager;
	}

	
	/**
	 * <p>
	 * �ж��Ƿ��
	 * </p>
	 * <br>
	 * @return
	 */
	public boolean isOpen() {
		// TODO Auto-generated method stub
		return false;
	}

	
	//TODO:�����Ƿ�����쳣�׳�
	private void registerIoSessionNow(IoSessionImpl session) throws IOException {
		SocketChannel channel = session.getChannel();
		if(channel == null) {
			throw new Error("ͨ������Ϊ��");
		}

		/*if(channel.isRegistered()) {
			throw new RuntimeException("��ͨ���Ѿ������");
		}*/

		
		channel.configureBlocking(false);                //����Ϊ������
		
		if(session.getClass() == IoSessionImpl.class) {
			//ע��
			SelectionKey selectKey = channel.register(selector, SelectionKey.OP_READ, session);  //��ѡ����ע��ͨ��

			session.setSelectionKey(selectKey);   //���ûỰ��ѡ���

			//session.setOwnerDispatcher(this);    //���ù���IO������

			this.onRegisterSession(session);
		}
		else if(session.getClass() == PoxyIoSession.class) {  //ע�����Ӳ���
			PoxyIoSession poxyIoSession = (PoxyIoSession) session;
			SelectionKey selectKey = channel.register(selector, SelectionKey.OP_CONNECT, session);
			channel.connect(poxyIoSession.getOwnerAcceptor().getBindAddress());
			session.setSelectionKey(selectKey);
		}
		
		//������ʱ���
		session.lastAccessTime = System.currentTimeMillis();
		session.lastReadTime = System.currentTimeMillis();
		session.lastWriteTime = System.currentTimeMillis();

		session.getOwnerAcceptor().getTimer().schedule(session.getCheckOverTime(), 
				session.getOwnerAcceptor().getOverTimeHandler().interval(), 
				session.getOwnerAcceptor().getOverTimeHandler().interval());

	}
	
	
	/**ע������е����лỰ��ѡ����*/
	private void registerIoSessionsNow() {
		for(;;) {
			IoSessionImpl session = this.waitRegQueue.poll();
			if(session == null) {
				break;
			}
			
			try {
				registerIoSessionNow(session);
			} catch (IOException e) {
				//TODO:��¼���쳣
				e.printStackTrace();
				try {
					//ע��ʧ��ʱֱ�ӵ��ùرգ������ų̹رգ���Ϊ��ʱ�Ự������û����ѡ������
					session.closeNow0();    
				} catch (IOException e1) {}
			}
		}
	}
	
	
//////////////////////////////////////���������Ҫ�Ƴ��ĻỰ����ʽ�رղ���/////////////////////
	
	/**�Ƴ����ر��������Ƴ������еĻỰ*//*
	private void removeIoSessionsNow() {
		for(;;) {
			CloseFuture future = this.waitRemoveQueue.poll();
			if(future == null) {
				break;
			}
			
			removeIoSessionNow(future);
		}
	}*/
	
	
	/**�����رղ���ѡ�������Ƴ��Ự*/
	private void removeIoSessionNow(CloseFuture future) {
		IoSessionImpl session = (IoSessionImpl) future.getSession();
		
		
		//�����;ȡ����ɾ���������°��ſ���
		if(future.isCannel()) {
			if(session.isClose()) {
				try {
					session.closeNow0();
				} catch (IOException e) {}
			}
			else {
			//��;ȡ���رջỰʱ��Ӧ�ðѻỰ��λ
				if(session.isCloseing.compareAndSet(true, false)) {
					//������ȡ���˹رգ��ỰҪ�ٴ����´򿪶��ļ���
					session.setReadControl(true);
					
					//���¼���һ������
					this.scheduleControl(session);
				}
			}
		}
		else {

			try {
				session.closeNow();
				future.setComplete(null);

			} catch (IOException e) {
				e.printStackTrace();
				future.setComplete(e);
			}
			finally {
				this.onRemoveSession((IoSessionImpl) future.getSession());
				session.getOwnerAcceptor().getIoHandler().ioSessionClosed(future);
			}
		}
	}
	
	
	
	/**�ı��ų��еĻỰ���ƣ����ų̵ĻỰ�õ�д���¼�*/
	private void changeControls() {
		while(true) {
			IoSessionImpl session = this.waitControlQueue.poll();
			if(session == null) {
				break;
			}
			
			if(!session.isClose()) {  //�����û����ʽ�رվ�ִ��
				session.isChangeingControl.set(true);
			
			
				session.setWriteControl(true);
			
				session.isChangeingControl.set(false);
			}
		}
	}
	
	

	@Override
	public void run() {

		this.isRunning = true;
		while(isRunning) {
			try {
				selector.select(1000);
			} catch (IOException e) {
				//TODO:��¼�쳣
				e.printStackTrace();
			}

			for(Iterator<SelectionKey> i = selector.selectedKeys().iterator(); i.hasNext();) {
				SelectionKey sk = i.next();
				i.remove();

				
				IoSessionImpl session = (IoSessionImpl) sk.attachment();
				
				if(sk.isValid() == false) {
					try {
						session.closeNow0();
					} catch (IOException e) {}
					continue;
				}

				
				if(sk.isValid() && sk.isConnectable()) {
					//TODO:
					try {
						handleConnect(session);
					} catch (IOException e) {
						//TODO:��¼���쳣
						e.printStackTrace();
						ConnectFuture future =  (ConnectFuture) ((PoxyIoSession) session).getConnectFuture();
						future.setComplete(e);          //�������
						session.close();
					}
				}
				
				if(sk.isValid() && sk.isReadable()) {
					handleRead(session);
				}

				if(sk.isValid() && sk.isWritable()) {
					handleWrite(session);
				}
				
				
			}
			
			
			this.handleOverTimeSessions();   //����ʱIoSession
			
			//this.removeIoSessionsNow();     //���Ƴ��������Ƴ����رջỰ
			this.registerIoSessionsNow();   //�ӵȴ�ע��Ķ�����ע��Ự��ѡ����
			
			this.changeControls();          //�ı�Ự���ƣ�Ϊ��Ҫд���ݵĻỰ����OP_WRITE�¼�
		}
	}
	
	/**
	 * ����ʱIoSession
	 */
	public void handleOverTimeSessions() {
		for(;;) {
			IoSessionImpl session = this.waitOverTimeQueue.poll();
			if(session == null) {
				break;
			}
			
			session.notifyOverTime();
		}
	}
	
	
	/**����Ự����*/
	public void handleConnect(IoSessionImpl session) throws IOException{
		if(session.getChannel().finishConnect()) {
			session.setReadControl(true);         //�򿪶�
			session.setWriteControl(true);        //����
			session.setConnectControl(false);     //�ر�
			this.onRegisterSession(session);      //session��ӵ���������

			ConnectFuture future =  (ConnectFuture) ((PoxyIoSession) session).getConnectFuture();
			future.setComplete(null);                 //�������
		}
	}
	
	
	/**
	 * ����Ự��
	 * @param session
	 */
	public void handleRead(IoSessionImpl session) {
		try {
			session.readNow();
			
			session.lastReadTime = System.currentTimeMillis();
			session.lastAccessTime = session.lastReadTime;
			
			
		} catch (IOException e) {
			e.printStackTrace();
			session.close();
		}
	}

	
	
	/**
	 * <p>
	 * д���������//TODO:������ʱ����ÿ�ν���һ����Ϣ��д�����������Ϣ�����ݴ���1k�ͷŵ��´�
	 * ѭ������//TODO:���ڿ����Ƿ�д���ֽ�������Ϊ�ж�����һ��ѭ����д��С�ڶ��ٵ����ݽ���
	 * �´�ѭ�������ǰ���Ϣ���������ж�
	 * </p>
	 * <br>
	 * @param session
	 */
	public void handleWrite(IoSessionImpl session) {
		Queue<IoFuture> writeQueue = session.getWriteQueue();
		
		IoFuture ioFuture =  writeQueue.peek();
		
		
		if(ioFuture == null) {
			session.setWriteControl(false);
			return;
		}
		
		if(ioFuture.getClass() == CloseFuture.class) {
			if(ioFuture.equals(session.getCloseFuture())) {
				//TODO:�����ر��¼�
				this.removeIoSessionNow(session.getCloseFuture());
				writeQueue.poll();
				return;
			}
		}
			
		
		WriteFuture future = (WriteFuture) ioFuture;
		
		ByteBuffer outBuffer = future.getBuffer();
		
		int writelen = 0;            //д���ֽڵĳ���
		int count = 0;               //Ϊ�˷�ֹ������ѭ���Ĳ���
		
		while(true) {
			count ++;
			try {
				writelen += session.getChannel().write(outBuffer);
			}
			catch(IOException e) {
				//TODO:��¼�쳣
				e.printStackTrace();
				if(future.equals(writeQueue.peek())) {
					writeQueue.poll();
				}
				future.setComplete(e);
				break;
			}
			
			//����Ƿ��в���������
			if(outBuffer.hasRemaining()) {
				if(writelen > MAX_OUT_SIZE) {
					//���ܻ��в������ݣ������Ѿ�д�볬��MAX_OUT_SIZE�����ݣ���Ϊ�Ѳ����������´�ѭ��
					break;
				}
			}
			else {
				//�Ѿ�û�в�����������
				if(future.equals(writeQueue.poll())) {
					future.setComplete(null);
					break;
				}
				else {
					//�����鵽������Ķ���ͷ�������Ǵ���Ķ���ͷ������˵�����ǵĳ�����ĳЩ�ط��ǲ������
					//��ʱӦ���׳����󣬲����ó������ִ����ȥ��.
					throw new Error("�������ش���");
					
					//TODO:��������һ�ִ��������������׳�����
				}
			}
			
			if(count >= 10) {
				break;
			}
		}
		
		//session.setWriteControl(false);          //�ر�д
		
		//����Ự���Ѿ�û��Ҫд�������ˣ��͹رջỰ�е�д
		if(session.getWriteQueue().peek() == null) {
			session.setWriteControl(false);
			//this.scheduleControl(session);
		}
		
		session.lastWriteTime = System.currentTimeMillis();
		session.lastAccessTime = session.lastWriteTime;
	}
	
	
	/**�ų̿���,����Ự�м���д���¼�*/
	public void scheduleControl(IoSessionImpl session) {
		if(!session.isChangeingControl.get()) {
			this.waitControlQueue.offer(session);
		}
		this.wakeup();
	}
	
	
	/**�ų�ע�ᣬ����Ựע��*/
	public void scheduleRegister(IoSessionImpl session) {
		if(session == null) {
			return;
		}
		
		this.waitRegQueue.offer(session);
		this.wakeup();
	}

	
	
	////////////////////////////////////Listener//////////////////////////////
	void onRegisterSession(IoSessionImpl session) {
		Iterator<DispatcherEventlListener> iter = this.listenerSet.iterator();
		while(iter.hasNext()) {
			iter.next().onRegisterSession(session);
		}
	}
	
	void onRemoveSession(IoSessionImpl session) {
		Iterator<DispatcherEventlListener> iter = this.listenerSet.iterator();
		while(iter.hasNext()) {
			iter.next().onRemoveSession(session);
		}
	}
}
