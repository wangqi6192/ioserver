package com.yz.net;

import java.io.IOException;

/**
 * <p>
 * �Ự�Ѿ����ر��ˣ����ڻỰ�Ͻ���IO�����쳣
 * </p>
 * <br>
 * @author ����@ritsky
 *
 */
public class ClosedSessionException extends IOException {
	public ClosedSessionException(String msg) {
		super(msg);
	}
}
