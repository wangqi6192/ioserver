package com.yz.net;

import java.io.IOException;
import java.net.SocketAddress;

/**
 * <p>
 * IO��������ͨ�������������԰󶨱���IP��ַ�Ͷ˿ڣ�����������м�����ͬʱ����������һЩ���紦����ص�<br>
 * �����ߣ�����ProtocolHandler(Э�鴦����)������OverTimeHandler(��ʱ������)��IoHandler(��Ϣ������)<br>
 * ����Ҳ��������ܵ�������
 * 
 * ע�⣺<br>
 * 1.IoAcceptorĿǰ�汾ֻ�ܼ���һ���˿ڣ�֮��İ汾���Ǽ������ͬʱ������ͬ�ĵ�ַ<br>
 * 2.ProtocolHandler��IoHandlerһ��Ҫ�������ã�������������� <br>
 * 3.���OverTimeHandlerδ�����ã���ܻ�Ĭ���ṩһ����Ĭ�ϵĴ�������5������Ϊ��ʱ���ж�������Ĭ�ϴ���<br>
 *   ��ֻ�Զ�д���޲��������ĳ�ʱ�����˴���<br>
 * 4.Ĭ�ϵ�OverTimeHandlerֻ�ǵ�������ʱʱ�ر�IoSession<br>
 * </p>
 * <br>
 * @author ����@ritsky
 *
 */
public interface IoService {
	/**
	 * <p>
	 * ��IP��ַ
	 * </p>
	 * <br>
	 * @param address
	 * @throws IOException
	 */
	public void bind(SocketAddress address) throws IOException;
	
	
	/**
	 * <p>
	 * ���ڸ����˿��ϰ󶨱�����ַ
	 * </p>
	 * <br>
	 * @param port
	 * @throws IOException
	 */
	public void bind(int port) throws IOException;
	
	
	/**
	 * <p>
	 * ��ȡ���ַ
	 * </p>
	 * <br>
	 * @return
	 */
	public SocketAddress getBindAddress();
	
	
	/**
	 * <p>
	 * ��ȡһ��Э�鴦����
	 * </p>
	 * <br>
	 * @return
	 */
	public ProtocolHandler getProtocolHandler();
	
	
	/**
	 * <p>
	 * ����һ��Э�崦����
	 * </p>
	 * <br>
	 * @param handler
	 * @return
	 */
	public void setProtocolHandler(ProtocolHandler handler);
	
	
	/**
	 * <p>
	 * ��ȡһ��io������
	 * </p>
	 * <br>
	 * @return
	 */
	public IoHandler getIoHandler();
	
	
	/**
	 * <p>
	 * ����һ��io������
	 * </p>
	 * <br>
	 * @param handler
	 */
	public void setIoHandler(IoHandler handler);
	
	
	
	
	/**
	 * <p>
	 * ��ó�ʱ������
	 * </p>
	 * <br>
	 * @return
	 */
	public OverTimeHandler getOverTimeHandler();
	
	/**
	 * <p>
	 * ���ó�ʱ������
	 * </p>
	 * <br>
	 * @param handler
	 */
	public void setOverTimeHandler(OverTimeHandler handler);
	

	
	
	/**
	 * <p>
	 * �������
	 * </p>
	 * <br>
	 * @throws Exception
	 */
	public void start() throws Exception;
	
	
	/**
	 * <p>
	 * ֹͣ���
	 * </p>
	 * <br>
	 * @throws Exception
	 */
	public void stop() throws Exception;
	
	
	/**
	 * <p>
	 * ��ȡһ��IO�Ự
	 * </p>
	 * <br>
	 * @param ioSessionId
	 * @return
	 */
	public IoSession getIoSession(long ioSessionId);
}
