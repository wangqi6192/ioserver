package com.yz.net.impl;





/**
 * <p>
 * �������¼�������
 * </p>
 * <br>
 * @author ����@ritsky
 *
 */
interface DispatcherEventlListener {
	/**
	 * <p>
	 * ѡ�����Cancelʱ����
	 * </p>
	 * <br>
	 * @param key
	 */
	public void onRemoveSession(IoSessionImpl session);
	
	
	/**
	 * <p>
	 * ע��IO�Ựʱ
	 * </p>
	 * <br>
	 * @param session
	 */
	public void onRegisterSession(IoSessionImpl session);
}
