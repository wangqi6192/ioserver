package com.yz.net;

import java.nio.ByteBuffer;
import java.util.List;

/**
 * <p>
 * Э�鴦������ʹ����ͨ��ʵ�ִ˽ӿ��������������յ��ֽ����ݣ���������ת�����ڴ��е�<br>
 * ��Ϣģ��
 * </p>
 * <br>
 * @author ����@ritsky
 *
 */
public interface ProtocolHandler {
	/**
	 * <p>
	 * ��������ͨ��ʱ��ͨ���˷���������ת����NetMessage <br>
	 * ע�⣺
	 * ���� onData�е�ByteBuffer�����������ܵ������У���data������
	 * ��ʣ���ֽ�������Ӱ����
	 * </p>
	 * <br>
	 * @param data
	 * @param session
	 * @return
	 */
	List<NetMessage> onData(ByteBuffer data, IoSession session);
	
	/**
	 * <p>
	 * ÿ�ξ���ʱ�Ƿ�ر�ͨ��������true�رգ����򱣳�
	 * </p>
	 * <br>
	 * @return
	 */
	boolean isClose();
}
