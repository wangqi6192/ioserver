package com.yz.net;


/**
 * <p>
 * IO�����ߣ�����Э�鴦���ľ�����Ϣ���д������Կ���������������߼����һ���ط����߼������Ϣ�Ĵ���<br>
 * ���Լ̳д˽ӿڽ�����Ϣ�Ĵ���IOServer�Ŀ��Ϊ�������ݣ�û��ΪIoHandler���̳߳ط���Ĵ��������<br>
 * �߼������б�Ҫ�õ��̳߳�֮��Ĵ�������������ʵ�ֵ�IoHandler�����ⷽ���ʵ��.
 * </p>
 * <br>
 * @author ����@ritsky
 *
 */
public interface IoHandler {
	/**
	 * <p>
	 * ������Ϣ����ʱ������������Ϣ�Ǿ���Э�鴦���߽��������Ϣ���ͣ�ע�⣬��û��������Ϣ������ʱ�˷���<br>
	 * �ǲ��ᱻ������
	 * </p>
	 * <br>
	 * @param session
	 */
	public void messageReceived(IoSession session, NetMessage msg);
	
	
	/**
	 * <p>
	 * ������Ϣ����ʱ���ⷢ������Ϣ��û�о���Э�崦���ߵ�ByteBuffer���ͣ������ݵĽ����Ҫʵ��������ʵ��<br>
	 * ע�⣬��������Э�鴦�����󣬴˷����ǲ��ᱻ�����ġ�
	 * </p>
	 * <br>
	 * @param session
	 * @param buffer
	 */
	public void messageReceived(IoSession session, byte[] msgdata);
	
	
	//public void onPutMessage(IoSession session);
	
	
	
	//public void onClosedIoSession(IoFuture future);
	
	
	/**
	 * <p>
	 * IoSession����ʽ�ر�ʱ����ִ��(���ڹر����쳣�ģ�Ҳ���ǲ���IoSession�ǳɹ��ر�<br>
	 * �����ڹر�ʱ�׳����쳣���ᱻ����
	 * </p>
	 * <br>
	 * @param session
	 */
	public void ioSessionClosed(IoFuture future);
	
	
	
	//public void onError(Exception e);
}
