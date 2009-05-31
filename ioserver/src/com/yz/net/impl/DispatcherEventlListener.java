package com.yz.net.impl;





/**
 * <p>
 * 发报机事件监听者
 * </p>
 * <br>
 * @author 胡玮@ritsky
 *
 */
interface DispatcherEventlListener {
	/**
	 * <p>
	 * 选择键被Cancel时触发
	 * </p>
	 * <br>
	 * @param key
	 */
	public void onRemoveSession(IoSessionImpl session);
	
	
	/**
	 * <p>
	 * 注册IO会话时
	 * </p>
	 * <br>
	 * @param session
	 */
	public void onRegisterSession(IoSessionImpl session);
}
