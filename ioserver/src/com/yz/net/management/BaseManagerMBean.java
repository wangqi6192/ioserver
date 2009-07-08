package com.yz.net.management;

import javax.management.MBeanServer;
import javax.management.ObjectName;

/**
 * Jmx Management Base service class
 *
 * @author William Wang
 * @version $Rev$, Jun 9, 2009 3:48:23 PM
 */
public abstract class BaseManagerMBean {

    /**
     * {@link MBeanServer} JMX 服务listener
     */
    private MBeanServer mBeanServer;

    /**
     * {@link ObjectName} 即将 注册到 JMX Class 名称
     */
    private ObjectName objectName;

    /**
     * jmx bean server
     *
     * @param mBeanServer
     *            {@link MBeanServer}
     * @param manager
     *            {@link Manager}
     */
    public BaseManagerMBean(MBeanServer mBeanServer, Object manager) {
        this.mBeanServer = mBeanServer;
        this.objectName = createObjectName(manager);
    }

    /**
     *
     * @return {@link MBeanServer}
     */
    public MBeanServer getMBeanServer() {
        return mBeanServer;
    }

    /**
     * 注册 {@link BaseManagerMBean#objectName} ☞ JMX Server
     */
    public void register() {
        try {
            getMBeanServer().registerMBean(this, getObjectName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 撤销 ☞ Jmx Server 的注册
     */
    public void unregister() {
        try {
            getMBeanServer().unregisterMBean(getObjectName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @return the objectName
     */
    public ObjectName getObjectName() {
        return objectName;
    }

    /**
     * 创建 {@link ObjectName} 对象
     *
     * @param object
     *            {@link Object}
     * @return {@link ObjectName} instance
     */
    public ObjectName createObjectName(Object object) {
        ObjectName objectName = null;
        try {
            objectName = new ObjectName(object.getClass().toString() + ":type=Manager,name="
                    + object.getClass().getName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return objectName;
    }
}
