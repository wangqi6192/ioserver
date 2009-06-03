package example.chat;

import com.yz.net.IoFuture;
import com.yz.net.IoHandlerAdapter;
import com.yz.net.IoSession;
import com.yz.net.NetMessage;

/**
 * <p>
 * IoServer没有提供逻缉处理的工作线程，只提供了IO读写处理的线程，当收到消息时如果<br>
 * 直接作逻缉处理，则是在io的读写线程中执行的，一般项目应根据需要自行分配逻缉处理线<br>
 * 程，如当收到消息后，再转发到自定义的工作线程内作处理.
 * </p>
 * <br>
 * @author 胡玮@ritsky
 *
 */
public class LogicHandler extends IoHandlerAdapter {

	@Override
	public void ioSessionClosed(IoFuture future) {
		
	}

	@Override
	public void messageReceived(IoSession session, NetMessage msg) {
		
	}
	
}
