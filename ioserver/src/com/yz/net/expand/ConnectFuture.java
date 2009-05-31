package com.yz.net.expand;

import com.yz.net.IoSession;
import com.yz.net.impl.AbstractIoFuture;

/**
 * <p>
 * 连接的异步计算结果
 * </p>
 * <br>
 * @author 胡玮@ritsky
 *
 */
public class ConnectFuture extends AbstractIoFuture {

	public ConnectFuture(IoSession session) {
		super(session);
	}

	@Override
	protected void completeRun() {
		// TODO Auto-generated method stub
		
	}

}
