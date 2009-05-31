package com.yz.net;

import java.io.IOException;

/**
 * <p>
 * 会话正在关闭中，在会话上进行IO操作
 * </p>
 * <br>
 * @author 胡玮@ritsky
 *
 */
public class CloseingSessionException extends IOException {
	public CloseingSessionException(String msg) {
		super(msg);
	}
}
