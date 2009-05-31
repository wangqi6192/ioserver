package com.yz.net.impl;


import java.nio.ByteBuffer;

/**
 * 内存管理接口
 * @author Administrator
 *
 */
interface MemoryManagerInface {

	/**
	 * 获取指定字节大小的一块内存区域
	 * @return
	 */
	public ByteBuffer allocat(int size);
	
	/**
	 * 获得一块内存
	 * @return
	 */
	public ByteBuffer allocat();
	
	/**
	 * 释放一个指定对象的内存
	 * @param buf
	 */
	public void free(ByteBuffer buf) throws Exception ;
	
	/**
	 * 整理内存碎片
	 * @return
	 */
	public boolean neaten();
}
