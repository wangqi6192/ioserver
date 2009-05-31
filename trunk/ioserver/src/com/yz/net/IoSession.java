package com.yz.net;

import java.io.IOException;



/**
 * <p>
 * ����Ự����ʾһ��ͨѶ����
 * </p>
 * <br>
 * @author ����@ritsky 
 *
 */
public interface IoSession {
	
	/**
	 * <p>
	 * ��ȡSessionId
	 * </p>
	 * <br>
	 * @return
	 */
	public long getId();
	
	
	/**
	 * <p>
	 * �������
	 * </p>
	 * <br>
	 * @param key
	 * @param obj
	 */
	public void addAttribute(String key, Object obj);
	
	/**
	 * <p>
	 * ��ȡ����
	 * </p>
	 * <br>
	 * @param key
	 * @return
	 */
	public Object getAttribute(String key);
	
	/**
	 * <p>
	 * �Ƴ�����
	 * </p>
	 * <br>
	 * @param key
	 * @return
	 */
	public Object removeAttribute(String key);
	
	
	/**
	 * <p>
	 * д��Ϣ
	 * </p>
	 * <br>
	 * @param msg
	 * @return
	 */
	public IoFuture write(NetMessage msg);
	

	
	/**
	 * <p>
	 * ��ȡ������ȡ������Ϣ����
	 * </p>
	 * <br>
	 * @return
	 */
	public int available();
	
	
	/**
	 * <p>
	 * ��ȡ��Ϣ���˷������������������û����Ϣ�ɶ�ʱ��������null
	 * </p>
	 * <br>
	 * @return
	 */
	public NetMessage read();
	
	
	/**
	 * <p>
	 * ��ȡ��Ϣ���˷��������������
	 * </p>
	 * <br>
	 * @param msgs
	 */
	public void read(NetMessage[] msgs);
	
	
	/**
	 * <p>
	 * �Ƿ�ر�
	 * </p>
	 * <br>
	 * @return
	 */
	public boolean isClose();
	
	
	/**
	 * <p>
	 * �ر�IO�Ự������Ϊ�첽�ģ����Ҫ��ͬ�������Ե��÷��ؽ����IoFuture.await()
	 * </p>
	 * <br>
	 * @return
	 */
	public IoFuture close();
}
