package com.yz.net.impl;

import java.io.IOException;


/**
 * <p>
 * IoReadWriteMachine�Ĺ��������ṩ�㷨����ѡ����ʵ�IoReadWriteMachine
 * </p>
 * <br>
 * @author ����@ritsky
 *
 */
class IoReadWriteMachineManager {

	private AbstractIoServer ioAcceptor;

	/**������Ⱥ�������CPU����������*/
	private IoReadWriteMachine[] ioDispatchers;

	private int nextIndex = 0;



	public IoReadWriteMachineManager(AbstractIoServer ioAcceptor) {

		this.ioAcceptor = ioAcceptor;
	}


	public void init() throws Exception {
		int cupNum = Runtime.getRuntime().availableProcessors(); //���CPU����

		//�����ǰ���ڣ�����ֹͣ
		if(ioDispatchers != null) {
			for(int i=0; i<ioDispatchers.length; i++) {
				ioDispatchers[i].close();
			}
		}

		ioDispatchers = new IoReadWriteMachine[cupNum];
		for(int i=0; i<cupNum; i++) {
			ioDispatchers[i] = new IoReadWriteMachine();
			ioDispatchers[i].init(2000);
			ioDispatchers[i].addListener(ioAcceptor);
		}
	}


	/**
	 * <p>
	 * �������е�IO������
	 * </p>
	 * <br>
	 */
	public void start() {

		for(int i=0; i<ioDispatchers.length; i++) {
			Thread t = new Thread(ioDispatchers[i], "IoProcess-"+i);
			t.start();
		}
	}


	/**
	 * <p>
	 * �ر����е�IO������
	 * </p>
	 * <br>
	 * @throws IOException
	 */
	public void stop() {
		for(int i=0; i<ioDispatchers.length; i++) {
			ioDispatchers[i].close();
		}

		nextIndex = 0;
	}



	public int getDispatcherNum() {
		return ioDispatchers.length;
	}


	public IoReadWriteMachine getNextDispatcher() {
		IoReadWriteMachine dispatcher = ioDispatchers[nextIndex++];
		if(nextIndex >= ioDispatchers.length) {
			nextIndex = 0;
		}

		return dispatcher;
	}
}
