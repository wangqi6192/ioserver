package com.yz.net.impl;

import com.yz.net.IoSession;

class CloseFuture extends AbstractIoFuture {

	public CloseFuture(IoSession session) {
		super(session);
	}

	@Override
	public void completeRun() {

	}

}
