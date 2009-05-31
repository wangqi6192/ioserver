package com.yz.net;

import java.nio.ByteBuffer;

/**
 * <p>
 * 网络消息
 * </p>
 * <br>
 * @author 胡玮@ritsky
 *
 */
public interface NetMessage {
	
	/**
	 * <p>
	 * 获得消息内容
	 * </p>
	 * <br>
	 * @return
	 */
	public byte[] getContent();
}
