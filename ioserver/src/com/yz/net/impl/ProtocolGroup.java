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
	//private int prefixByteNum;
	
	/**记录协议处理者的标签视图*/
	//private ArrayList<FlagRecord> flagList = new ArrayList<FlagRecord>();
	
	private ArrayList<ProtocolHandler> handlerList = new ArrayList<ProtocolHandler>();

	

	/**
	 * <p>
	 * 构建协议组，构建时会告诉系统，每次收到的数据的前多少位字节是用来分析接下来具体选择组中协议<br>
	 * 的判断标准
	 * </p>
	 * <br>
	 * @param prefixByteNum 
	 *//*
	ProtocolGroup(int prefixByteNum) {
		this.prefixByteNum = prefixByteNum;
	}
	*/
	
	ProtocolGroup() {
		
	}
	
	
	/**
	 * <p>
	 * 添加协议处理者
	 * </p>
	 * <br>
	 * @param flagstr 处理者标签字符串(用于进行分析判的依据)<br>
	 * @param handler 协议处理者对像<br>
	 *//*
	public void addHandler(String flagstr, ProtocolHandler handler) {
		if(flagstr == null || handler == null || handler.getClass() == ProtocolGroup.class) {
			return;
		}
		
		//标签统一转换成大写
		flagstr = flagstr.toUpperCase();
		
		byte[] flagData = null;
		try {
			flagData = flagstr.getBytes("ISO-8859-1");
			
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		FlagRecord flag = new FlagRecord();
		flag.flagData = flagData;
		flag.handler = handler;
		
		flagList.add(flag);
	}*/
	
	
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
		
		return null;
		
		/*int size = flagList.size();
		for(int i=0; i<size; i++) {
			FlagRecord record = flagList.get(i);
			if(record == null) {
				continue;
			}
			
			List<NetMessage> list = record.handler.onData(data, session);
			if(list != null && list.size() > 0) {
				return list;
			}
		}
		
		return null;*/
		
	}


	/**
	 * <p>
	 * 根据指定参数的数据，分析到得某个具的协议处理者，如果没有找到，即返回null，方法只找到第一个<br>
	 * 符合条件的协议处理者
	 * </p>
	 * <br>
	 * @param data 接收到的字节数据<br>
	 * @return ProtocolHandler 协议处理者<br>
	 *//*
	ProtocolHandler getHandler(ByteBuffer data) {
		//重新得到一个只读buffer，目的是为了不影响之后的具体协议解析
		ByteBuffer readdata = data.asReadOnlyBuffer();
		
		byte[] prefix = new byte[prefixByteNum];
		readdata.get(prefix);
		
		int size = flagList.size();
		for(int i=0; i<size; i++) {
			FlagRecord record = flagList.get(i);
			if(record == null) {
				continue;
			}
			
			if(record.contrast(prefix)){
				return record.handler;
			}
		}
		
		return null;
	}*/
	 
	/*
	ProtocolHandler getHandler0(ByteBuffer data) {
		ByteBuffer readdata = data.asReadOnlyBuffer();
		
		int size = flagList.size();
		for(int i=0; i<size)
		
		return null;
	}
	*/
	

	/**
	 * <p>
	 * 标签记录，标识并记录ProtocolHandler的包装类，提供与前缀字节的对比操作
	 * </p>
	 * <br>
	 * @author 胡玮@ritsky
	 *
	 *//*
	private class FlagRecord {
		byte[] flagData;
		
		ProtocolHandler handler;
		
		public boolean contrast(byte[] prefix) {
			int len = flagData.length;
			
			if(prefix.length < len) {
				return false;
			}
			
			//TODO:
			String prefixstr = new String(prefix, "ISO-8859-1"); 
			
			for(int i=0; i<len; i++) {
				if(flagData[i] != prefix[i]){
					return false;
				}
			}
			return true;
		}
	}*/
}
