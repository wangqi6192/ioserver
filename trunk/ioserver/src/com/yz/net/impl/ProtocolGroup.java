package com.yz.net.impl;


import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import com.yz.net.IoSession;
import com.yz.net.NetMessage;
import com.yz.net.ProtocolHandler;

/**
 * <p>
 * 协议组定义了，一组和协议解析工作相关的ProtocolHandler对像，此类对像会根据收到数据具体分析<br>
 * 而选择组里具体的一个ProtocolHandler进行工作
 * 
 * </p>
 * @author 胡玮@ritsky
 *
 */
class ProtocolGroup implements ProtocolHandler {

	
	/**需要作分析工作的前缀字节数*/
	private int prefixByteNum;
	
	private ArrayList<ProtocolHandler> handlerList = new ArrayList<ProtocolHandler>();

	

	/**
	 * <p>
	 * 构建协议组，构建时会告诉系统，每次收到的数据的前多少位字节是用来分析接下来具体选择组中协议<br>
	 * 的判断标准
	 * </p>
	 * <br>
	 * @param prefixByteNum 
	 */
	ProtocolGroup(int prefixByteNum) {
		this.prefixByteNum = prefixByteNum;
	}
	

	
	
	/**
	 * <p>
	 * 添加协议处理者
	 * </p>
	 * <br>
	 */
	public void addHandler(ProtocolHandler handler) {
		if(handler == null) {
			return;
		}
		
		this.handlerList.add(handler);
	}
	

	@Override
	public List<NetMessage> onData(ByteBuffer data, IoSession session) {
		//ByteBuffer readdata = data.asReadOnlyBuffer();
		
		if(data.remaining() < prefixByteNum) {
			return null;
		}
		
	
		int size = handlerList.size();
		for(int i=0; i<size; i++) {
			ProtocolHandler handler = handlerList.get(i);
			if(handler == null) {
				continue;
			}
			
			List<NetMessage> list = handler.onData(data, session);
			if(list != null && list.size() > 0) {
				return list;
			}
		}
		
		ArrayList<NetMessage> list = new ArrayList<NetMessage>();
		list.add(NetMessage.ERROR_MSG);
		
		return list;
		
	}
}
