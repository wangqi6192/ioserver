package com.yz.net.impl;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.List;
import java.util.Queue;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import com.yz.net.ClosedSessionException;
import com.yz.net.CloseingSessionException;
import com.yz.net.IoFuture;
import com.yz.net.IoSession;
import com.yz.net.NetMessage;

/**
 * <p>
 * IO�Ựʵ����
 * </p>
 * <br>
 * @author ����@ritsky
 *
 */
public class IoSessionImpl implements IoSession{
	
	/**sessionId*/
	private long id;
	
	/**����ӳ���*/
	private HashMap<String, Object> attributeMap;
	
	/**�Ƿ���ʽ���ر�*/
	private volatile boolean isClose;
	
	/**�Ƿ����ڹر���*/
	AtomicBoolean isCloseing = new AtomicBoolean(false);
	
	/**�Ƿ����ڸı����*/
	AtomicBoolean isChangeingControl = new AtomicBoolean(false);
	
	
	/**�Ƿ����ڳ�ʱ�����г�ʱ������,false:��δ��ʼ��ʱ����,true:��ʱ����ʼ��*/
	AtomicBoolean isOverTimeHandleing = new AtomicBoolean(false);
	
	/**ͨ��*/
	private SocketChannel channel;
	
	/**ѡ���*/
	private SelectionKey selectionKey;
	
	/**д��Ϣʱ���Ƿ�Ϊ����д*//*
	private boolean isblockWrite;
	
	*//**����Ϣʱ���Ƿ�������*//*
	private boolean isblockRead;
	
	*//**�رջỰʱ���Ƿ������ر�*//*
	private boolean isblockClose;*/
	
	/**�����ʱ��*/
	long lastReadTime = System.currentTimeMillis();
	
	/**���дʱ��*/
	long lastWriteTime = System.currentTimeMillis();
	
	/**�������ʱ��*/
	long lastAccessTime = System.currentTimeMillis();
	
	/**д����*/
	private ConcurrentLinkedQueue<IoFuture> writeQueue = new ConcurrentLinkedQueue();
	
	/**IoSession���ĸ��ط�ע���*/
	private IoReadWriteMachine ownerDispatcher;
	
	/**IoSession�Ĺ���������*/
	private AbstractIoServer ownerAcceptor;
	
	/**����buffer*/
	private ByteBuffer inBuffer;
	
	/**��鳬ʱ*/
	private OverTimeCheckTask checkOverTime;
	
	/**�رջỰʱ��Future*/
	private CloseFuture closeFuture = new CloseFuture(this);
	
	//TODO:���Ǽ��뵽ĿǰΪֹ���ܹ������˶������ݣ����ֽڼ���
	
	IoSessionImpl() {
		checkOverTime = new OverTimeCheckTask();
	}
	
	protected IoSessionImpl(SocketChannel channel, IoReadWriteMachine dispatcher) {
		this.channel = channel;
		this.ownerDispatcher = dispatcher;
		checkOverTime = new OverTimeCheckTask();
		//this.inBuffer = this.ownerDispatcher.getMemoryManager().allocat(10240);        //����10������cache 
	}
	
	
	protected IoSessionImpl(long id, SocketChannel channel, AbstractIoServer acceptor) {
		this.id = id;
		this.channel = channel;
		this.ownerAcceptor = acceptor;
		checkOverTime = new OverTimeCheckTask();
	}
	
	CloseFuture getCloseFuture() {
		return closeFuture;
	}
	
	
	/**��ó�ʱ�������*/
	OverTimeCheckTask getCheckOverTime() {
		return checkOverTime;
	}
	
	/**����ͨ��*/
	protected SocketChannel getChannel() {
		return channel;
	}
	
	SelectionKey getSelectionKey(){
		return selectionKey;
	}
	
	void setSelectionKey(SelectionKey key) {
		this.selectionKey = key;
	}
	
	/**
	 * ���ù���������
	 * @param dispatcher
	 */
	void setOwnerDispatcher(IoReadWriteMachine dispatcher) {
		this.ownerDispatcher = dispatcher;
	}
	
	/**
	 * ��������buffer
	 */
	void allocatInBuffer() {
		//TODO:�����ڴ���������buffer���ڴ�����л��
		//ByteBuffer buffer = this.ownerDispatcher.getMemoryManager().allocat(10240);
		//this.inBuffer = this.ownerDispatcher.getMemoryManager().allocat(10240);
		this.inBuffer = ByteBuffer.allocateDirect(1024 * 5);
	}
	
	
	protected AbstractIoServer getOwnerAcceptor() {
		return ownerAcceptor;
	}
	
	
	@Override
	public void addAttribute(String key, Object obj) {
		synchronized (attributeMap) {
			attributeMap.put(key, obj);
		}
	}

	@Override
	public int available() {
		throw new java.lang.UnsupportedOperationException("��û��ʵ��");
	}

	
	@Override //�رջỰ���رջỰ���첽�رգ������Ҫͬ��������IoFuture�ϵ�wait
	public IoFuture close() {
		if(this.isCloseing.compareAndSet(false, true)) {
			if(this.closeFuture == null) {
				closeFuture = new CloseFuture(this);
			}
			
			this.writeQueue.offer(closeFuture);
			
			//�Ѿ�����رջỰ�����̣���ʱ�ǲ���������κ���������������ˣ�����Ҫ�رն��ļ���
			this.setReadControl(false);
			
			//IO�Ự�����ų̿���
			ownerDispatcher.scheduleControl(this);
		
		}
		return this.closeFuture; 
	}

	
	/**�ڷ������е��õ������رգ����û���ر��Ƿ��ڽ�����*/
	void closeNow() throws IOException {
		if(this.isCloseing.get()) {
			closeNow0();
			this.isClose = true;		
		}
	}
	
	
	@Override
	public Object getAttribute(String key) {
		synchronized (attributeMap) {
			return attributeMap.get(key);
		}
	}

	@Override
	public long getId() {
		return id;
	}

	@Override /**�Ƿ���ʽ�ر�*/
	public boolean isClose() {
		return isClose;
	}
	
	
	/**�Ƿ����ڰ����*/
	public boolean isCloseing() {
		return isCloseing.get();
	}
	

	/**���ϴ������ȡ��Ϣ*/
	void readNow() throws IOException {
		int readlen = this.channel.read(this.inBuffer);
		
		if(readlen == -1) {  //������ĩ
			throw new IOException("End_Stream");
		}
		
		if(readlen > 0) {
			
			//��û������Э�鴦��ʱ
			if(ownerAcceptor.getProtocolHandler() == null) {   
				inBuffer.flip();
				byte[] msgdata = new byte[inBuffer.remaining()];
				inBuffer.get(msgdata);
				inBuffer.clear();
				ownerAcceptor.getIoHandler().messageReceived(this, msgdata);
			}
			else {  //��������Э�鴦����ʱ
				ByteBuffer readBuf = inBuffer.asReadOnlyBuffer();
				readBuf.flip();

				List<NetMessage> list = ownerAcceptor.getProtocolHandler().onData(readBuf, this);

				int size = list.size();
				if(list != null && size > 0) {
					inBuffer.position(readBuf.position());
					inBuffer.limit(readBuf.limit());

					inBuffer.compact();                  //ѹ������ʣ����ֽڷŵ�inBuffer����ǰ��
					inBuffer.limit(inBuffer.capacity());

					//������Ϣ
					for(int i=0; i<size; i++) {
						this.ownerAcceptor.getIoHandler().messageReceived(this, list.get(i));
					}
				}
			}
		}
	}
	
	
	
	void closeNow0() throws IOException {
		if(this.selectionKey != null) {
			this.selectionKey.cancel();
		}
		
		if(channel != null) {
			channel.close();
			channel.socket().close();
		}
		
		try {
			if(this.inBuffer != null) {
				this.ownerDispatcher.getMemoryManager().free(this.inBuffer);
			}
		}
		catch(Exception e) {}
		
		//this.isClose = true;		
	}
	
	
	@Override
	public NetMessage read() {
		throw new java.lang.UnsupportedOperationException("��û��ʵ��");
	}

	@Override
	public void read(NetMessage[] msgs) {
		throw new java.lang.UnsupportedOperationException("��û��ʵ��");
		
	}

	@Override
	public Object removeAttribute(String key) {
		synchronized (attributeMap) {
			return attributeMap.remove(key);
		}
	}

	@Override/**д��Ϣ*/
	public IoFuture write(NetMessage msg) {
		if(msg == null) {
			throw new IllegalArgumentException("msg is null");
		}
		
		WriteFuture future = new WriteFuture(this, ByteBuffer.wrap(msg.getContent()));
		
		//�Ự�Ѿ����ر���
		if(isClose) {
			future.setComplete(new ClosedSessionException("�Ự�Ѿ����ر���...."));
			return future;
		}
		
		if(!this.isCloseing.get()) {
			this.writeQueue.offer(future);
			
			//�ж�Э�鴦���ߵ�Ҫ���Ƿ�Ϊд���ر�
			if(this.ownerAcceptor.getProtocolHandler().isClose()) {
				this.close();
			}
		
			//IO�Ự�����ų̿���
			ownerDispatcher.scheduleControl(this);
		
			//ownerDispatcher.wakeup();
		}
		else {
			future.setComplete(new CloseingSessionException("�Ự���������ڹر�״̬��...."));
		}
		
		return future;
	}

	
	
	
	/**���д����*/
	protected Queue<IoFuture> getWriteQueue() {
		return this.writeQueue;
	}
	
	/**
	 * <p>
	 * ����д����
	 * </p>
	 * <br>
	 * @param isopen �Ƿ��д
	 */
	void setWriteControl(boolean isopen) {
		if(!selectionKey.isValid()) {
			return;
		}
		
		int op = this.selectionKey.interestOps();
		if(isopen) {
			op = op | SelectionKey.OP_WRITE;
		}
		else {
			op = op ^ SelectionKey.OP_WRITE;
		}
		this.selectionKey.interestOps(op);
	}
	
	
	/**
	 * <p>
	 * ���ö�����
	 * </p>
	 * <br>
	 * @param isopen �Ƿ�򿪶�
	 */
	void setReadControl(boolean isopen) {
		if(!selectionKey.isValid()) {
			return;
		}
		
		int op = this.selectionKey.interestOps();
		if(isopen) {
			op = op | SelectionKey.OP_READ;
		}
		else {
			op = op ^ SelectionKey.OP_READ;
		}
		this.selectionKey.interestOps(op);
	}

	
	void setConnectControl(boolean isopen) {
		if(!selectionKey.isValid()) {
			return;
		}
		
		int op = this.selectionKey.interestOps();
		if(isopen) {
			op = op | SelectionKey.OP_CONNECT;
		}
		else {
			op = op ^ SelectionKey.OP_CONNECT;
		}
		this.selectionKey.interestOps(op);
	}
	
	
	/**֪ͨ��ʱ��*/
	void notifyOverTime() {
		long currTime = System.currentTimeMillis();
		
		long bathOverTime = ownerAcceptor.getOverTimeHandler().bothOverTime();
		
		if(bathOverTime > 0) {
			if((currTime - lastAccessTime) > bathOverTime) {
				if(isOverTimeHandleing.get()) {
					//����ʱ��
					this.ownerAcceptor.getOverTimeHandler().onBothOverTime(this);
				}
				isOverTimeHandleing.set(false);
				return;
			}
		}
		
		long readOverTime = ownerAcceptor.getOverTimeHandler().readOverTime();
		
		if(readOverTime > 0) {
			if((currTime - lastAccessTime) > readOverTime) {
				if(isOverTimeHandleing.get()) {
				//����ʱ��
					this.ownerAcceptor.getOverTimeHandler().onReadOverTime(this);
				}
			}
		}
		
		long writeOverTime = ownerAcceptor.getOverTimeHandler().writerOverTime();
		
		if(writeOverTime > 0) {
			if((currTime - lastAccessTime) > writeOverTime) {
				if(isOverTimeHandleing.get()) {
					//����ʱ��
					this.ownerAcceptor.getOverTimeHandler().onWriterOverTime(this);

				}
			}
		}
		isOverTimeHandleing.set(false);
	}
	
	
	
	/**��ʱ����Ƿ�ʱ*/
	private class OverTimeCheckTask extends TimerTask {

		@Override
		public void run() {
			
			long currTime = System.currentTimeMillis();
			long bothOverTime = ownerAcceptor.getOverTimeHandler().bothOverTime();
			long readOverTime = ownerAcceptor.getOverTimeHandler().readOverTime();
			long writeOverTime = ownerAcceptor.getOverTimeHandler().writerOverTime();
			
			if(bothOverTime <=0 || readOverTime <= 0 || writeOverTime <=0 ) {
				//TODO:�����ڳ�ʱ����
				return;
			}
			
			
			//����Ƿ��г�ʱ�������еĻ�������IO�����߳���ȥ
			if((currTime - lastAccessTime) > ownerAcceptor.getOverTimeHandler().bothOverTime() ||
					(currTime - lastReadTime) > ownerAcceptor.getOverTimeHandler().readOverTime() ||
					(currTime - lastWriteTime) > ownerAcceptor.getOverTimeHandler().writerOverTime()) {
				if(!IoSessionImpl.this.isCloseing() || !IoSessionImpl.this.isClose()) {
					if(isOverTimeHandleing.compareAndSet(false, true)) {
						ownerDispatcher.scheduleOverTime(IoSessionImpl.this); //�ų�	
					}
				}
			}
		}
		
	}
}
