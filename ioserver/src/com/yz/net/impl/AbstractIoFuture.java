package com.yz.net.impl;

import com.yz.net.IoFuture;
import com.yz.net.IoSession;

/**
 * <p>
 * �ṩ�����IoFuture����
 * </p>
 * <br>
 * @author ����@ritsky
 *
 */
public abstract class AbstractIoFuture implements IoFuture {
	
	private IoSession session;
	
	/**�Ƿ����*/
	private boolean isComplete;
	
	/**�Ƿ�ȡ��*/
	private boolean isCancel;
	
	/**�쳣����*/
	private Throwable throwable;
	
	
	public AbstractIoFuture(IoSession session) {
		this.session = session;
	}
	
	@Override
	public void await()  {
		while(true) {
			if(isComplete()) {
				break;
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {}
		}
	}

	@Override
	public void await(long timeout) {
		try {
			Thread.sleep(timeout);
		} catch (InterruptedException e) {}
	}

	
	@Override
	public void cancel() {
		synchronized (this) {
			isCancel = true;
		}
	}


	@Override
	public IoSession getSession() {
		return session;
	}

	@Override
	public Throwable getThrowable() {
		return this.throwable;
	}

	
	@Override
	public boolean isComplete() {
		synchronized (this) {
			return this.isComplete;
		}
	}

	@Override
	public boolean isError() {
		synchronized (this) {
			if(getThrowable() == null) {
				return false;
			}
			else {
				return true;
			}
		}
	}

	@Override
	public boolean isCannel() {
		synchronized (this) {
			if(this.isComplete) {
				return false;
			}
			
			return this.isCancel;
		}
	}

	
	/**�������*/
	public void setComplete(Throwable throwable) {
		synchronized (this) {
			if(isCancel) {  //�������û�����о�ȡ����
				return;
			}
			
			this.isComplete = true;              //�������
			
			if(throwable != null) {
				this.throwable = throwable;
			}
			
			this.completeRun();
		}
	}	

	
	protected abstract void completeRun();
}
