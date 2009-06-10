package com.yz.net.impl;

import com.yz.net.Configure;
import com.yz.net.ConfigureBuilder;

/**
 * <p>
 * </p>
 * <br>
 * @author huwei
 *
 */
public class XmlConfigureBuilder implements ConfigureBuilder {

	private static ConfigureBuilder instance;
	
	public static ConfigureBuilder getInstance() {
		if(instance == null) {
			instance = new XmlConfigureBuilder();
		}
		
		return instance;
	}
	
	@Override
	public Configure buildConfigure(String filename) {
		// TODO Auto-generated method stub
		return null;
	}

}
