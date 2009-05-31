package com.yz.net;

/**
 * <p>
 * IO��ʱ�����߽ӿڣ�IoSession��ʱ�󶼻ᴥ���˽ӿ������������ﶨ���˶���ʱ(IoSession�ڹ涨��һ��ʱ��<br>
 * ��û�з����κζ�����Ϊ���ӳ�ʱ)��д��ʱ(IoSession�ڹ涨��һ��ʱ����û�з���д����Ϊ����Ϊ��ʱ)��˫<br>
 * ����ʱ(�ڹ涨ʱ��û�з�������д����Ϊ����Ϊ��ʱ)������Ҫע����ǣ�������˫����ʱʱ�����ܶ���ʱ��д��<br>
 * ʱ�Ƿ����������ᱻ����ִ�С�<br>
 * 
 * �ӿڲ��������˳�ʱ��ִ�з�������������һЩ��ʱ�ж�ʱ��������ʱ�����������Ժ���Ϊ��λ���㣬����interval()<br>
 * �㶨���˿�ܻ�������ʱ����һ��IoSession�Ƿ�ʱ��readOverTime()�㶨���˶��ĳ�ʱ��λ�������Դ�����<br>
 * </p>
 * <br>
 * @author ����@ritsky
 *
 */
public interface OverTimeHandler {
	/**
	 * <p>
	 * һ��ʱ����û�н��ж�����ʱ������
	 * </p>
	 * <br>
	 * @param session
	 */
	public void onReadOverTime(IoSession session);
	
	
	/**
	 * <p>
	 * һ��ʱ����û�н���д����ʱ������
	 * </p>
	 * <br>
	 * @param session
	 */
	public void onWriterOverTime(IoSession session);
	
	
	/**
	 * <p>
	 * һ��ʱ����û�н��ж�д����ʱ������
	 * </p>
	 * <br>
	 * @param session
	 */
	public void onBothOverTime(IoSession session);
	
	
	/**
	 * <p>
	 * ���ʱ�䣬ÿ�μ�鳬ʱ��ļ���೤�ټ��һ�Σ����ص�ʱ���Ժ���Ϊ���㵥λ
	 * </p>
	 * <br>
	 * @return
	 */
	public long interval();
	
	/**
	 * <p>
	 * �������ĳ�ʱʱ��ֵ
	 * </p>
	 * <br>
	 * @return
	 */
	public long readOverTime();
	
	
	/**
	 * <p>
	 * д�����ĳ�ʱʱ��ֵ
	 * </p>
	 * <br>
	 * @return
	 */
	public long writerOverTime();
	
	
	/**
	 * <p>
	 * ��д�����ĳ�ʱʱ��ֵ
	 * </p>
	 * <br>
	 * @return
	 */
	public long bothOverTime();
}
