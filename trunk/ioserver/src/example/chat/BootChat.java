package example.chat;

import java.net.InetSocketAddress;

import com.yz.net.Configure;
import com.yz.net.IoService;
import com.yz.net.ProtocolHandler;
import com.yz.net.impl.IoServerImpl;

/**
 * <p>
 * 聊天例程主类，负责启动聊天服务器
 * </p>
 * <br>
 * @author 胡玮@ritsky
 *
 */
public class BootChat {

	
	public static IoService service;
	
	public static void main(String[] args) throws Exception{
		MessageProcessTask.addFilter(new FiltrateTask());    //添加任务处理的过滤任务
		
		PlayerManager.getInstance();
		
		service = new IoServerImpl();
		ProtocolHandler protocolGroup = service.buildProtocolGroup(4, new CmNetProtocolHandler(), new CmWapProtocolHandler());
		
		Configure config = new Configure();
		config.setAddress(new InetSocketAddress("127.0.0.1",8899));
		config.setProtocolHandler(protocolGroup);
		config.setIoHandler(new LogicHandler());
		
		config.start(service);
		
		
		while(true) {
			Thread.sleep(1000);
		}
	}

}
