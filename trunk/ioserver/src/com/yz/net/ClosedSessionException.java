package com.yz.net;

import java.io.IOException;

/**
 * <p>
 * 会话已经被关闭了，再在会话上进行IO操作异常
 * </p>
 * <br>
 * @author 胡玮@ritsky
 *
 */
public class ClosedSessionException extends IOException {
	public ClosedSessionException(String msg) {
		super(msg);
	}
}
