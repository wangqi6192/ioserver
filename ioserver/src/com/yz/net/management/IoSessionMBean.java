package com.yz.net.management;

import com.yz.net.IoSession;
import com.yz.net.impl.IoServerImpl;

/**
 * {@link IoSession} 【连接】管理接口 <br>
 * 多用于 server[ {@link IoServerImpl} ] 端
 *
 * @author William Wang
 * @version $Rev$, Jul 8, 2009 4:45:44 PM
 */
public interface IoSessionMBean {

    /**
     * 关闭 {@link IoSession}
     */
    public void close();

    /**
     * 返回 {@link IoSession} 存活状态<br>
     * Result: ture if connection is active ,vice versa false
     */
    public boolean isSessionClosed();
}
