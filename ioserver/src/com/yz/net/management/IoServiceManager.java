package com.yz.net.management;

import javax.management.MBeanServer;

import com.yz.net.IoService;

/**
 * {@link IoService} manage actor <br>
 * 具体 {@link IoService} 管理 实现类
 *
 * @author William Wang
 * @version $Rev$, Jul 8, 2009 5:18:13 PM
 */
public class IoServiceManager extends BaseManagerMBean implements IoServiceMBean {

    private IoService ioService;

    /**
     * MBean 初始化入口
     *
     * @param beanServer
     *            JMX {@link MBeanServer}
     * @param ioService
     *            {@link IoService}
     */
    public IoServiceManager(MBeanServer beanServer, IoService ioService) {
        super(beanServer, ioService);
        this.ioService = ioService;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.yz.net.management.IoServiceMBean#restart()
     */
    @Override
    public void restart() {
        if (ioService != null) {
            try {
                ioService.stop();
                ioService.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.yz.net.management.IoServiceMBean#start()
     */
    @Override
    public void start() {
        if (ioService != null) {
            try {
                ioService.stop();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.yz.net.management.IoServiceMBean#stop()
     */
    @Override
    public void stop() {
        // TODO First 创建 IoServices 对象，then start it's instance
    }

}
