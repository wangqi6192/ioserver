package example.chat;

import com.yz.net.IoHandlerAdapter;
import com.yz.net.IoSession;
import com.yz.net.NetMessage;

import example.chat.msgtask.AddFriend;
import example.chat.msgtask.FriendList;
import example.chat.msgtask.HearTbeat;
import example.chat.msgtask.Login;
import example.chat.msgtask.SendMessage;

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
	public void messageReceived(IoSession session, NetMessage msg) {
		//这里如果是真正的项目，最好应该把生成的任务放到项目中的线程池或项目指定的
		//线程里运行，这里只是一个例程，所以没有做这方面的事情
		
		InputMessage inMsg = (InputMessage) msg;
		switch(inMsg.getCmdType()) {
		case ChatCommandId.C_LOGIN_REQ:
			new Login(session, inMsg).run(); 
			break;
		case ChatCommandId.C_FRIENDLIST_REFURBISH_REQ:
			new FriendList(session,inMsg).run();
			break;
		case ChatCommandId.C_ADDFRIEND_REQ:
			new AddFriend(session,inMsg).run();
			break;
		case ChatCommandId.C_HEARTBEAT_REQ:
			new HearTbeat(session,inMsg).run();
			break;
		case ChatCommandId.C_SEND_MSG:
			new SendMessage(session,inMsg).run();
			break;
		}
	}
}
