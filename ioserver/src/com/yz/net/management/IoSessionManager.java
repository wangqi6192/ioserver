package com.yz.net.management;

import javax.management.MBeanServer;

import com.yz.net.IoSession;

/**
 * {@link IoSession} mananger worker(actor)
 *
 * @author William Wang
 * @version $Rev$, Jul 8, 2009 4:49:13 PM
 */
public class IoSessionManager extends BaseManagerMBean implements IoSessionMBean {

    private IoSession session;

    /**
     * init {@link IoSession} object to manange
     *
     * @param beanServer
     *            jmx server linster 监听者 {@link MBeanServer}
     * @param manager
     */
    public IoSessionManager(MBeanServer beanServer, IoSession session) {
        super(beanServer, session);
        this.session = session;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.yz.net.management.IoSessionMBean#close()
     */
    @Override
    public void close() {
        if (session != null && !session.isClose()) {
            session.close();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.yz.net.management.IoSessionMBean#isSessionClosed()
     */
    @Override
    public boolean isSessionClosed() {
        if (session != null)
            return session.isClose();
        return Boolean.FALSE;
    }

}
