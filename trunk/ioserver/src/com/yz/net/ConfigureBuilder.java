package com.yz.net;

/**
 * <p>
 * 配置文件构建者，可以实现以xml文件为配置的构建者，或者以属性文件为配置的构建者
 * </p>
 * <br>
 * @author 胡玮@ritsky
 *
 */
public interface ConfigureBuilder {
	public Configure buildConfigure(String filename);
}
