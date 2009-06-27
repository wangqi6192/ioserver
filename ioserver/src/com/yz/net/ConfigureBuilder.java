package com.yz.net;

/**
 * <p>
 * 配置构建者，本打算实现xml文件配置，属性文件配置，不过暂时只放好了类，并没有任何实现
 * </p>
 * <br>
 * @author 胡玮@ritsky
 *
 */
public interface ConfigureBuilder {
	public Configure buildConfigure(String filename);
}
