﻿package com.yz.net.impl;

import java.io.IOException;


/**
 * <p>
 * IoReadWriteMachine的管理器，提供算法用于选择合适的IoReadWriteMachine
 * </p>
 * <br>
 * @author 胡玮@ritsky
 *
 */
class IoReadWriteMachineManager {

	private AbstractIoService ioAcceptor;

	/**发报机群，会根据CPU各数来决定*/
	private IoReadWriteMachine[] ioDispatchers;

	private int nextIndex = 0;



	public IoReadWriteMachineManager(AbstractIoService ioAcceptor) {

		this.ioAcceptor = ioAcceptor;
	}


	public void init(int readwriteThreadNum) throws Exception {
		
		//如果先前存在，则先停止
		if(ioDispatchers != null) {
			for(int i=0; i<ioDispatchers.length; i++) {
				ioDispatchers[i].close();
			}
		}

		ioDispatchers = new IoReadWriteMachine[readwriteThreadNum];
		for(int i=0; i<readwriteThreadNum; i++) {
			ioDispatchers[i] = new IoReadWriteMachine();
			ioDispatchers[i].init(2000);
			ioDispatchers[i].addListener(ioAcceptor);
		}
	}


	/**
	 * <p>
	 * 启动所有的IO处理器
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
	 * 关闭所有的IO处理器
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
