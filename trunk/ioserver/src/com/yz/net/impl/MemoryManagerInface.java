package com.yz.net.impl;


import java.nio.ByteBuffer;

/**
 * �ڴ����ӿ�
 * @author Administrator
 *
 */
interface MemoryManagerInface {

	/**
	 * ��ȡָ���ֽڴ�С��һ���ڴ�����
	 * @return
	 */
	public ByteBuffer allocat(int size);
	
	/**
	 * ���һ���ڴ�
	 * @return
	 */
	public ByteBuffer allocat();
	
	/**
	 * �ͷ�һ��ָ��������ڴ�
	 * @param buf
	 */
	public void free(ByteBuffer buf) throws Exception ;
	
	/**
	 * �����ڴ���Ƭ
	 * @return
	 */
	public boolean neaten();
}
