package com.yz.net;


/**
 * <p>
 * ��ʾIO���첽���������ṩ�������Ƿ���ɵķ������͵ȴ�������ɵķ�����
 * ע�⣺
 * һ��IO��������ɣ�ֻ��˵�����������п��ܼ������У������Ƿ�ɹ��������Ƿ����֤��
 * ���ṩ�ˣ�����쳣�ķ����������������ȷ���ǰ�����쳣����ͨ������쳣���Ի�ò�
 * ���������쳣
 * </p>
 * <br>
 * @author ����@ritsky
 *
 */
public interface IoFuture {
	
	/**
	 * <p>
	 * ��ȡIO�Ự
	 * </p>
	 * <br>
	 * @return IoSession ��ʾһ��IO�Ự<br>
	 */
	public IoSession getSession();
	
	
	/**
	 * <p>
	 * ��鷢����IO�����Ƿ���ɣ�����true:���  ����false:δ���
	 * </p>
	 * <br>
	 * @return boolean �Ƿ����<br>
	 */
	public boolean isComplete();
	
	
	/**
	 * <p>
	 * ���IO�����Ƿ��������ˣ�����true:����������  ����false:δ��������
	 * </p>
	 * <br>
	 * @return
	 */
	public boolean isError();
	
	
	/**
	 * <p>
	 * ���IO�����Ƿ�ȡ���ˣ�����true:������Ϣ  ����false:����δ��ȡ��
	 * </p>
	 * <br>
	 * @return boolean �Ƿ�ȡ��<br>
	 */
	public boolean isCannel();
	
	
	
	/**
	 * <p>
	 * ��ȡIO���������쳣����쳣�������IO�����޴�������������null
	 * </p>
	 * <br>
	 * @return Throwable �쳣<br>
	 */
	public Throwable getThrowable();

	
	/**
	 * <p>
	 * �ȴ���ɣ�һֱ�ȴ���ֱ��IO�������Ϊֹ����ʱ��ȴ���һ����Ե�˵��������Ϊ��һЩ����Ԥ<br>
	 * ���������趨��һ����ȴ�������ڳ�����ȴ���δ�õ�IO������ɵ��źţ����������س�<br>
	 * �����Ա�����������ѭ����
	 * </p>
	 * <br>
	 */
	public void await();
	
	
	/**
	 * <p>
	 * �ȴ���ɣ��ȴ�ָ����ʱ��(����)��ֱ��IO�������Ϊֹ�����ָ��ʱ����IO����δ��ɣ�����������<br>
	 * </p>
	 * <br>
	 * @param timeout �ȴ�ʱ��<br>
	 */
	public void await(long timeout);

	/**
	 * <p>
	 * ȡ����ǰIO����
	 * </p>
	 * <br>
	 */
	public void cancel();
}
