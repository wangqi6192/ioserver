package com.yz.net.management;

import com.yz.net.IoService;
import com.yz.net.impl.IoServerImpl;

/**
 * {@link IoService} mananger interface<br>
 * {@link IoService} 基本管理，比如启动,停止,重启 {@link IoService}
 *
 * @author William Wang
 * @version $Rev$, Jul 8, 2009 5:13:49 PM
 */
public interface IoServiceMBean {

    /**
     * 启动 {@link IoServerImpl}<br>
     * condition of {@link IoServerImpl} is stop
     */
    public void start();

    /**
     * 停止 {@link IoServerImpl}<br>
     * condition of {@link IoServerImpl} is start
     */
    public void stop();

    /**
     * 重启 {@link IoServerImpl}<br>
     * condition of {@link IoServerImpl} is start
     */
    public void restart();
}
